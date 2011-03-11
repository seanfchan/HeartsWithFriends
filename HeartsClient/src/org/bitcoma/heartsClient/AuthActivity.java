package org.bitcoma.heartsClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity extends Activity {
    Dialog signUpDialog;
    Dialog logInDialog;
    String userName;
    String password;
    String emailAddress;

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
                    Toast.makeText(getApplicationContext(), userName + " " + emailAddress + " " + password,
                            Toast.LENGTH_SHORT).show();
                    Intent newActivity = new Intent(AuthActivity.this, MultiHomeActivity.class);
                    newActivity.putExtra("userName", userName);
                    startActivity(newActivity);
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
                Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                Intent newActivity = new Intent(AuthActivity.this, MultiHomeActivity.class);
                newActivity.putExtra("userName", userName);
                startActivity(newActivity);

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
}