package org.bitcoma.heartsClient;

import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.heartsClient.netty.NettyThread;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.MessageLite;

public class AuthActivity extends Activity {
    Dialog signUpDialog;
    Dialog logInDialog;
    String userName;
    String password;
    String emailAddress;
    Long userId;

    private static String TAG = "AuthActivity";
    private boolean bPendingSignupRequest = false;
    private boolean bPendingLoginRequest = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainL = new LinearLayout(this);
        mainL.setOrientation(LinearLayout.VERTICAL);
        mainL.setBackgroundColor(Color.parseColor("#228b22"));

        TextView padding = new TextView(this);
        padding.setText("\n");

        TextView message = new TextView(this);
        message.setText("Welcome to MultiPlayer <something>!\n\n");
        message.setPadding(10, 0, 0, 0);
        mainL.addView(message, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        Button signUp = new Button(this);
        signUp.setText("Sign Up");

        // setting up the alert dialog for sign up
        signUpDialog = new Dialog(this);
        signUpDialog.setContentView(R.layout.signup_dialog);
        signUpDialog.setTitle("Sign Up");

        Button cancelSignIn = (Button) signUpDialog.findViewById(R.id.buttonCancel);
        cancelSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpDialog.cancel();
            }

        });

        Button getInfo = (Button) signUpDialog.findViewById(R.id.buttonOK);
        getInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                userName = ((EditText) signUpDialog.findViewById(R.id.username)).getText().toString();
                emailAddress = ((EditText) signUpDialog.findViewById(R.id.emailadd)).getText().toString();
                String password1 = ((EditText) signUpDialog.findViewById(R.id.givePassword)).getText().toString();
                String password2 = ((EditText) signUpDialog.findViewById(R.id.confirmPassword)).getText().toString();
                String errorString = new String();
                if (password1.length() == 0 || emailAddress.length() == 0 || userName.length() == 0
                        || password2.length() == 0) {
                    errorString = "Please fill in all fields.";
                    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
                }

                if (password1.compareTo(password2) == 0 && userName.contains("@") == false) {
                    password = password1;
                    signUpDialog.dismiss();

                    // TODO @madiha: send the information to the server here.
                    bPendingSignupRequest = true;
                    SignupRequest request = SignupRequest.newBuilder().setEmail(emailAddress).setUserName(userName)
                            .setPassword(password1).build();
                    NettyThread.getInstance().writeMessage(request);

                } else {
                    errorString = new String();
                    if (userName.contains("@")) {
                        errorString = "Username should not contain @.\n";
                    }
                    if (password1.compareTo(password2) != 0) {
                        errorString += "Passwords don't match.";
                    }
                    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
                    signUpDialog.show();
                }

            }

        });

        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                signUpDialog.show();
            }

        });

        mainL.addView(signUp, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        mainL.addView(padding);

        Button logIn = new Button(this);
        logIn.setText("Log In");

        // setting up the alert dialog for sign up
        logInDialog = new Dialog(this);
        logInDialog.setContentView(R.layout.login_dialog);
        logInDialog.setTitle("Log In");

        Button cancelLogIn = (Button) logInDialog.findViewById(R.id.buttonNo);
        cancelLogIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logInDialog.cancel();
            }

        });

        Button doLogIn = (Button) logInDialog.findViewById(R.id.buttonYes);
        doLogIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userName = ((EditText) logInDialog.findViewById(R.id.nameOrEmail)).getText().toString();
                password = ((EditText) logInDialog.findViewById(R.id.putPassword)).getText().toString();
                logInDialog.dismiss();

                // TODO @madiha: send the information to the server here.
                bPendingLoginRequest = true;
                LoginRequest request = LoginRequest.newBuilder().setIdentifier(userName).setPassword(password).build();
                NettyThread.getInstance().writeMessage(request);
            }

        });

        logIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logInDialog.show();
            }

        });
        mainL.addView(logIn, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        getWindow().addContentView(mainL, getWindow().getAttributes());

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

    /**
     * Main handler for all server interaction
     */
    private HeartsProtoHandler mHandler = new HeartsProtoHandler() {
        @Override
        public void handleGenericResponse(GenericResponse msg) {

            Log.i(TAG, "Received Generic Response");

            if (bPendingSignupRequest) {
                bPendingSignupRequest = false;

                switch (msg.getResponseCode()) {
                case OK:
                    // HACK: @jon We will login after a signup
                    bPendingLoginRequest = true;
                    LoginRequest request = LoginRequest.newBuilder().setIdentifier(userName).setPassword(password)
                            .build();
                    NettyThread.getInstance().writeMessage(request);
                    return;
                case RESOURCE_UNAVAILABLE:
                    // Username/email is already taken
                    break;
                case INVALID_PARAMS:
                    // Invalid email/username - doesn't match regex or length
                    // requirements
                    break;
                default:
                    break;
                }

                // NOTE: This is required. Call back is run from the context of
                // a networking thread. If you try to run UI code android barfs.
                // This is how you get around it.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signUpDialog.show();
                    }
                });
            } else if (bPendingLoginRequest) {
                bPendingLoginRequest = false;

                switch (msg.getResponseCode()) {
                case UNAUTHORIZED:
                    // Incorrect password
                    break;
                case RESOURCE_UNAVAILABLE:
                    // No user/email that matches
                    break;
                default:
                    break;
                }

                // NOTE: This is required. Call back is run from the context of
                // a networking thread. If you try to run UI code android barfs.
                // This is how you get around it.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logInDialog.show();
                    }
                });
            }

        }

        @Override
        public void handleLoginResponse(LoginResponse msg) {
            Log.i(TAG, "Received Login Response");
            if (bPendingSignupRequest) {
                bPendingSignupRequest = false;
                // Should not happen
            } else if (bPendingLoginRequest) {
                bPendingLoginRequest = false;
                // Did we successfully login?
                if (msg.hasUserId()) {
                    userId = msg.getUserId();

                    Intent newActIntent = new Intent(AuthActivity.this, MultiHomeActivity.class);
                    newActIntent.putExtra("userName", userName);
                    newActIntent.putExtra("password", password);
                    newActIntent.putExtra("userId", userId);
                    startActivity(newActIntent);
                } else {
                    // Should never happen
                    Log.e(TAG, "Login Response without userId");
                }
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