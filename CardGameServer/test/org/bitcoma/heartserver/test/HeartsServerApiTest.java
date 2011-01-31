package org.bitcoma.heartserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.heartserver.HeartsServerApiImpl;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.utils.Encryptor;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.MessageLite;

public class HeartsServerApiTest {

    HeartsServerApiImpl api;

    public static final String DEFAULT_USERNAME = "username";
    public static final String DEFAULT_EMAIL = "username@mymail.com";
    public static final String DEFAULT_PASSWORD = "password";

    @Before
    public void setUp() {
        api = new HeartsServerApiImpl();

        removeUser(DEFAULT_EMAIL);
        createUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD, User.ACTIVE);
    }

    public void createUser(String userName, String email, String password, int state) {
        api.connectDB();
        User user = new User();
        user.set("user_name", userName);
        user.set("email", email);
        user.set("password", Encryptor.instance().encrypt(password));
        user.set("state", state);
        user.save();
        api.disconnectDB();
    }

    public void removeUser(String email) {
        api.connectDB();
        User user = (User) User.findFirst("email = ?", email);
        if (user != null) {
            user.delete();
        }
        api.disconnectDB();
    }

    public void loginUser() {
        // User not logged in
        LoginRequest lr = LoginRequest.newBuilder().setIdentifier(DEFAULT_USERNAME).setPassword(DEFAULT_PASSWORD)
                .build();
        api.login(lr, null);
        assertNotNull("Current user should not be null", api.getCurrentUser());
    }

    @Test
    public void testJoinGame() {
        // Test that you need to be logged in
        JoinGameRequest jgr = JoinGameRequest.newBuilder().setGameId(0).build();
        MessageLite response = api.joinGame(jgr);
        assertTrue("Find game without login should have a generic response", response instanceof GenericResponse);
        GenericResponse gr = (GenericResponse) response;
        assertEquals("Find games without login should return unauthorized response code",
                GenericResponse.ResponseCode.UNAUTHORIZED, gr.getResponseCode());
        assertNull("Current game should be null", api.getCurrentGame());

        loginUser();

        // Missing params
        jgr = JoinGameRequest.newBuilder().build();
        response = api.joinGame(jgr);
        assertTrue("Missing find game params should have a generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Missing find games params should return missing params response code",
                GenericResponse.ResponseCode.MISSING_PARAMS, gr.getResponseCode());
        assertNull("Current game should be null", api.getCurrentGame());

        // Make sure we consider we are in a game after finding one
        jgr = JoinGameRequest.newBuilder().setGameId(0).build();
        response = api.joinGame(jgr);
        assertNotNull("Current Game should not be null", api.getCurrentGame());
    }

    @Test
    public void testLogin() {
        // Missing params
        LoginRequest lr = LoginRequest.newBuilder().build();
        MessageLite response = api.login(lr, null);
        assertTrue("Missing login params should have a generic response", response instanceof GenericResponse);
        GenericResponse gr = (GenericResponse) response;
        assertEquals("Missing login params should return missing params response code",
                GenericResponse.ResponseCode.MISSING_PARAMS, gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());

        // Successful login
        lr = LoginRequest.newBuilder().setIdentifier(DEFAULT_USERNAME).setPassword(DEFAULT_PASSWORD).build();
        response = api.login(lr, null);
        assertTrue("Successful login should have a login response", response instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response;
        assertTrue("Login should be successful and should have valid user id", loginResponse.getUserId() > 0);
        assertNotNull("Current user should not be null", api.getCurrentUser());

        // Login for user that doesn't exist
        lr = LoginRequest.newBuilder().setIdentifier("bademail@mymail.com").setPassword("doggy").build();
        response = api.login(lr, null);
        assertTrue("Unsuccessful login should have generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Unsuccessful login with bad username should have resource unavailable",
                GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE, gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());

        // Login with bad password
        lr = LoginRequest.newBuilder().setIdentifier(DEFAULT_USERNAME).setPassword("badpassword").build();
        response = api.login(lr, null);
        assertTrue("Unsuccessful login should have generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Unsuccessful login with bad password should have unauthorized",
                GenericResponse.ResponseCode.UNAUTHORIZED, gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());
    }

    @Test
    public void testSignup() {
        final String USERNAME = "dudeman";
        final String EMAIL = "dude@mymail.com";

        // Missing params
        SignupRequest sr = SignupRequest.newBuilder().build();
        MessageLite response = api.signup(sr);
        assertTrue("Missing signup params should have a generic response", response instanceof GenericResponse);
        GenericResponse gr = (GenericResponse) response;
        assertEquals("Missing signup params should return missing params response code",
                GenericResponse.ResponseCode.MISSING_PARAMS, gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());

        // Successful signup
        removeUser(EMAIL);
        sr = SignupRequest.newBuilder().setUserName(USERNAME).setEmail(EMAIL).setPassword("doggy").build();
        response = api.signup(sr);
        assertTrue("Successful signup should have a generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Signup should be successful and have OK response code", GenericResponse.ResponseCode.OK,
                gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());

        // Signup for user that exists already
        sr = SignupRequest.newBuilder().setUserName(USERNAME).setEmail(EMAIL).setPassword("doggy").build();
        response = api.signup(sr);
        assertTrue("Unsuccessful signup should have generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Unsuccessful signup with bad username should have resource unavailable",
                GenericResponse.ResponseCode.RESOURCE_UNAVAILABLE, gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());

        // Signup with invalid email address and invalid username
        sr = SignupRequest.newBuilder().setUserName("myspecialman").setEmail("dude_mymail.com")
                .setPassword("badpassword").build();
        response = api.signup(sr);
        assertTrue("Unsuccessful signup should have generic response", response instanceof GenericResponse);
        gr = (GenericResponse) response;
        assertEquals("Unsuccessful signup with invalid email address", GenericResponse.ResponseCode.INVALID_PARAMS,
                gr.getResponseCode());
        assertNull("Current user should be null", api.getCurrentUser());
    }

    @Test
    public void testResetState() {
        fail("Not yet implemented");
    }

}
