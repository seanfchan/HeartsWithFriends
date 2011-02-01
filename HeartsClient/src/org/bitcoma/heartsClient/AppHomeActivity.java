package org.bitcoma.heartsClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AppHomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.apphomeactivity);

        // Single-Player will go into game mode right away
        // Multi-Player will have to go through sign up / log in stuff
        Button multiPlay = (Button) findViewById(R.id.multiPlay);
        multiPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent authIntent = new Intent(AppHomeActivity.this, AuthActivity.class);
                startActivity(authIntent);

            }

        });

        Button instructButton = (Button) findViewById(R.id.instruction);
        instructButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent instructionIntent = new Intent(AppHomeActivity.this, InstructionsActivity.class);
                startActivity(instructionIntent);

            }

        });

        // getWindow().addContentView(mainL, getWindow().getAttributes());

    }

}