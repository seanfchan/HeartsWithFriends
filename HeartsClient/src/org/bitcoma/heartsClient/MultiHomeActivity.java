package org.bitcoma.heartsClient;

import org.bitcoma.hearts.model.transfered.GameProtos.GameInfo;
import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.LeaveGameProtos.LeaveGameRequest;
import org.bitcoma.heartsClient.netty.NettyThread;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.MessageLite;

public class MultiHomeActivity extends Activity {

    private static String TAG = "MultiHomeActivity";
    private static final int QUICK_MATCH_DIALOG = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        String userName = bundle.getString("userName");

        LinearLayout mainL = new LinearLayout(this);
        mainL.setBackgroundColor(Color.parseColor("#228b22"));
        mainL.setOrientation(LinearLayout.VERTICAL);

        TextView welcomeUser = new TextView(this);
        welcomeUser.setText("Welcome, " + userName);
        welcomeUser.setPadding(4, 0, 0, 0);
        welcomeUser.setTextSize(25);
        mainL.addView(welcomeUser);

        TextView padding = new TextView(this);
        padding.setText("\n");
        mainL.addView(padding);

        Button playButton = new Button(this);
        playButton.setText("Play Hearts :)");

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Put up a progress dialog showing we are matchmaking.
                showDialog(QUICK_MATCH_DIALOG);

                // Perform a quick match.
                JoinGameRequest request = JoinGameRequest.newBuilder().setGameId(0).build();
                NettyThread.getInstance().writeMessage(request);
            }
        });
        mainL.addView(playButton);

        Button inviteFriends = new Button(this);
        inviteFriends.setText("Invite Friends from PhoneBook");
        mainL.addView(inviteFriends);

        getWindow().addContentView(mainL, getWindow().getAttributes());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog pd;
        switch (id) {
        case QUICK_MATCH_DIALOG:
            pd = new ProgressDialog(MultiHomeActivity.this);
            pd.setMessage(MultiHomeActivity.this.getResources().getString(R.string.findGame));
            pd.setIndeterminate(true);

            // If they cancel then tell the server to remove them from the game.
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // Remove them from a game as they have cancelled the
                    // request.
                    // TODO: @madiha remove any state for joining a game if we
                    // have any

                    LeaveGameRequest request = LeaveGameRequest.newBuilder().setGameId(0).build();
                    NettyThread.getInstance().writeMessage(request);
                }
            });
            return pd;
        default:
            return null;
        }
    }

    @Override
    protected void onStart() {
        // Start up the server thread to listen and send messages
        NettyThread.getInstance().start();

        super.onStart();
    }

    @Override
    protected void onResume() {
        // Activity is visible so we listen to server messages.
        NettyThread.getInstance().registerHandler(mHandler);

        super.onResume();
    }

    @Override
    protected void onPause() {
        // Activity is no longer visible so we don't listen for server
        // notifications here.
        NettyThread.getInstance().unregisterHandler(mHandler);

        super.onPause();
    }

    HeartsProtoHandler mHandler = new HeartsProtoHandler() {

        @Override
        public void handleGenericResponse(GenericResponse response, MessageLite origRequest) {

            if (origRequest instanceof JoinGameRequest) {

                // We are no longer looking for a quick match
                dismissDialog(QUICK_MATCH_DIALOG);

                switch (response.getResponseCode()) {
                case MISSING_PARAMS:
                    Log.i(TAG, "Could not join game. Request missing information.");
                    break;
                case INVALID_PARAMS:
                    Log.i(TAG, "Could not join game. Invalid request sent.");
                    break;
                case RESOURCE_UNAVAILABLE:
                    Log.i(TAG, "Could not join game. None available");
                    break;
                case UNAUTHORIZED:
                    Log.i(TAG, "Could not join game. Not logged in.");
                    break;
                default:
                    Log.i(TAG, "Could not join game. Unexpected reason.");
                    break;
                }
            } else if (origRequest instanceof LeaveGameRequest) {
                switch (response.getResponseCode()) {
                case OK:
                    Log.i(TAG, "Successfully left game.");
                    break;
                case MISSING_PARAMS:
                    Log.i(TAG, "Could not leave game. Request missing information.");
                    break;
                case INVALID_PARAMS:
                    Log.i(TAG, "Could not leave game. Invalid request sent.");
                    break;
                case RESOURCE_UNAVAILABLE:
                    Log.i(TAG, "Could not leave game. None available");
                    break;
                case UNAUTHORIZED:
                    Log.i(TAG, "Could not leave game. Not logged in.");
                    break;
                case UNEXPECTED_REQUEST:
                    Log.i(TAG, "Could not leave game. Not in a game.");
                    break;
                default:
                    Log.i(TAG, "Could not leave game. Unexpected reason.");
                    break;
                }
            }
        }

        @Override
        public void handleJoinGameResponse(JoinGameResponse response, MessageLite origRequest) {

            // If we are hear then we have successfully joined a game. Do not
            // move to the game room until it is full.

            GameInfo info = response.getGameInfo();

            // Here the game is full. So we should move to the game room.
            if (info.getPlayersCount() == info.getMaxNumberOfPlayers()) {
                // Here we need to move to the game room and save all the game
                // state. Also after we are done moving then we want to send a
                // StartGameRequest

                // We are no longer looking for a quick match
                dismissDialog(QUICK_MATCH_DIALOG);
            }

        }

        @Override
        public void handleUnexpectedMessage(MessageLite msg) {
            Log.i(TAG, "Received Unexpected Response");
        }

        @Override
        public void handleThrowable(Throwable throwable) {
            Log.i(TAG, "Received Throwable from Netty: " + throwable.getMessage());
        }
    };

}