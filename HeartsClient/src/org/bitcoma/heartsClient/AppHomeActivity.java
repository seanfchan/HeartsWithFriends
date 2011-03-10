package org.bitcoma.heartsClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AppHomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apphomeactivity);

        Typeface fontFace = Typeface.createFromAsset(getAssets(), "ATW.ttf");
        // Single-Player will go into game mode right away
        Button singlePlay = (Button) findViewById(R.id.singlePlay);
        singlePlay.setTypeface(fontFace);
        singlePlay.setTextSize(17);
        singlePlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent singleGame = new Intent(AppHomeActivity.this, SinglePlayerGameRoom.class);
                startActivity(singleGame);

            }

        });
        // Multi-Player will have to go through sign up / log in stuff
        Button multiPlay = (Button) findViewById(R.id.multiPlay);
        multiPlay.setTypeface(fontFace);
        multiPlay.setTextSize(17);
        multiPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent authIntent = new Intent(AppHomeActivity.this, AuthActivity.class);
                startActivity(authIntent);

            }

        });

        Button instructButton = (Button) findViewById(R.id.instruction);
        instructButton.setTypeface(fontFace);
        instructButton.setTextSize(17);
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