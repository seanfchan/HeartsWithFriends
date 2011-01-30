package org.bitcoma.heartserver;

import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.model.transfered.GameProtos.GameInfo;
import org.bitcoma.heartserver.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.heartserver.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.heartserver.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.heartserver.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.heartserver.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.heartserver.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.heartserver.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.heartserver.utils.Encryptor;
import org.jboss.netty.channel.Channel;

import activejdbc.Base;

import com.google.protobuf.MessageLite;

public class HeartsServerApiImpl implements IHeartsServerApi {

    private static final String DB_NAME = "test";
    private static final String DB_HOST = "localhost";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    private boolean dbConnected = false;
    private User currentUser = null;
    private GameInstance currentGame = null;

    public void connectDB() {
        if (!dbConnected) {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://" + DB_HOST + "/" + DB_NAME, DB_USER, DB_PASSWORD);
        }
        dbConnected = true;
    }

    public void disconnectDB() {
        if (dbConnected) {
            Base.close();
            dbConnected = false;
        }
    }

    private void setCurrentUser(User user) {
        if (user == null && currentUser != null) {
            ServerState.userIdToChannelMap.remove(currentUser.getLongId());

            setCurrentGame(null);
        }

        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public GameInstance getCurrentGame() {
        return currentGame;
    }

    private void setCurrentGame(GameInstance game) {
        if (game == null && currentGame != null) {
            if (getCurrentUser() != null)
                currentGame.removePlayer(getCurrentUser());
        }

        currentGame = game;
    }

    @Override
    public MessageLite joinGame(JoinGameRequest request) {
        boolean availableGames;

        setCurrentGame(null);

        if (getCurrentUser() == null) {
            // Need to be logged in at this point
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.UNAUTHORIZED).build();
        }
        // Current implementation will only have quick matching. So the request
        // should have a game_room_id of 0 to signify this. We will extend this
        // later as needed.
        else if (request != null && request.hasGameId() && request.getGameId() == 0) {

            // Looking is there are games that are not full and are waiting for
            // people
            synchronized (ServerState.waitingGames) {
                availableGames = !ServerState.waitingGames.isEmpty();
                if (availableGames) {
                    GameInstance game = GameLobby.joinGame(getCurrentUser(), ServerState.waitingGames,
                            ServerState.activeGames);

                    // FAIL: Should never go into this control block, somehow
                    // someone hacked the lock
                    if (game == null) {
                        GenericResponse gr = GenericResponse.newBuilder()
                                .setResponseCode(GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE).build();
                        return gr;
                    } else {
                        // Game successfully joined so keep track of game we are
                        // in.
                        setCurrentGame(game);
                    }
                }
            }
            // No available game rooms to join, so create one
            if (!availableGames) {
                GameInstance game = GameLobby.createGame(getCurrentUser(), ServerState.waitingGames,
                        ServerState.activeGames);

                // FAIL: Should never go into this control block, couldn't
                // insert into the FastMap
                if (game == null) {
                    GenericResponse gr = GenericResponse.newBuilder()
                            .setResponseCode(GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE).build();
                    return gr;
                } else {
                    // Game successfully created.
                    // Keep track of game created
                    setCurrentGame(game);
                }
            }

            GameInfo tempGameInfo = GameInfo.newBuilder()
                    .setCurrentNumberOfPlayers(getCurrentGame().getCurrentNumPlayers())
                    .setGameId(getCurrentGame().getId()).setMaxNumberOfPlayers(getCurrentGame().getMaxPlayers())
                    .build();
            return JoinGameResponse.newBuilder().setGameInfo(tempGameInfo).build();

        } else {
            // Parameters were not as expected
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.MISSING_PARAMS).build();
        }
    }

    @Override
    public MessageLite startGame(StartGameRequest request) {

        if (getCurrentUser() == null && getCurrentGame() == null) {
            // Need to be logged in at this point
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.UNAUTHORIZED).build();
        } else if (request != null && request.hasGameId()) {

            if (request.getGameId() != getCurrentGame().getId()) {
                // Game doesn't match the game that we have them in
                return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.INVALID_PARAMS)
                        .build();
            }

            // Can we successfully add this player to the game?
            if (getCurrentGame().addReadyPlayer()) {
                if (getCurrentGame().getReadyNumPlayers() == getCurrentGame().getMaxPlayers()) {
                    // Send back the dealt out cards and full game information.
                    // TODO: @jon figure out what this should look like for
                    // madiha

                    return null;
                } else {
                    // Not all players are ready so just send an ok in the mean
                    // time
                    return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.OK).build();
                }
            } else {
                // This condition should never happen
                return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE)
                        .build();
            }

        } else {
            // Parameters were not as expected
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.MISSING_PARAMS).build();
        }
    }

    @Override
    public MessageLite login(LoginRequest request, Channel channel) {
        setCurrentUser(null);

        if (request != null && request.hasEmail() && request.hasPassword()) {
            connectDB();

            MessageLite response = null;

            String email = request.getEmail();
            String password = request.getPassword();

            // Step 1: Look for active users with a matching email/password.
            User user = (User) User.findFirst("email = ? and state = ?", email, User.ACTIVE);
            if (user == null) {
                // User doesn't exist so return an error.
                response = GenericResponse.newBuilder()
                        .setResponseCode(GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE).build();
            } else {

                // Step 3: Check that the password matches.
                String userPassword = user.getString("password");
                if (Encryptor.instance().matches(password, userPassword)) {
                    // Login successful
                    response = LoginResponse.newBuilder().setUserId(user.getLongId()).build();

                    // Store state about the logged in user.
                    setCurrentUser(user);
                    ServerState.userIdToChannelMap.put(user.getLongId(), channel);
                } else {
                    // Password doesn't match
                    response = GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.UNAUTHORIZED)
                            .build();
                }
            }

            disconnectDB();

            return response;
        } else {
            // Parameters were not as expected
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.MISSING_PARAMS).build();
        }
    }

    @Override
    public MessageLite signup(SignupRequest request) {
        MessageLite response = null;
        setCurrentUser(null);

        if (request != null && request.hasEmail() && request.hasPassword()) {
            connectDB();

            String email = request.getEmail();
            String password = request.getPassword();

            // Step 1: Check for user with same email.
            User user = (User) User.findFirst("email = ?", email);
            if (user != null) {
                // User already exists so return an error.
                response = GenericResponse.newBuilder()
                        .setResponseCode(GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE).build();
            } else {
                // Step 2: Generate hashed value for password.
                String hashedPassword = Encryptor.instance().encrypt(password);

                // Step 3: Store the user in the database.
                user = new User();
                user.set("email", email);
                user.set("password", hashedPassword);

                // TODO: @jon Needs to be modified to verify email addresses
                // Should send email and wait for user to click link. Should
                // start in pending state first
                user.set("state", User.ACTIVE);

                if (user.save()) {
                    // Saved successfully to database
                    response = GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.OK).build();
                } else {
                    // Incorrectly saved to database either due to validation
                    // failures
                    response = GenericResponse.newBuilder()
                            .setResponseCode(GenericResponse.ResponseCode.INVALID_PARAMS).build();
                }
            }

            disconnectDB();

            return response;
        } else {
            // Parameters were not as expected
            return GenericResponse.newBuilder().setResponseCode(GenericResponse.ResponseCode.MISSING_PARAMS).build();
        }
    }

    /**
     * Resets local state for this logged in user.
     */
    public void resetState() {
        // Remove DB connection if still around
        disconnectDB();

        // Clean up game and logged in user status.
        setCurrentUser(null);
        setCurrentGame(null);
    }

}
