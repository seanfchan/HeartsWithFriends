package org.bitcoma.heartsClient;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MultiHomeActivity extends Activity
{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = this.getIntent().getExtras();
        String userName = bundle.getString("userName");
        
        LinearLayout mainL = new LinearLayout(this);
        mainL.setBackgroundColor(Color.parseColor("#228b22"));
        mainL.setOrientation(LinearLayout.VERTICAL);
        
        
        TextView welcomeUser = new TextView(this);
        welcomeUser.setText("Welcome, " + userName);
        welcomeUser.setPadding(4,0,0,0);
        welcomeUser.setTextSize(25);
        mainL.addView(welcomeUser);
        
        TextView padding = new TextView(this);
        padding.setText("\n");
        mainL.addView(padding);
        
        Button playButton = new Button(this);
        playButton.setText("Play Hearts :)");
        mainL.addView(playButton);
        
        Button inviteFriends = new Button(this);
        inviteFriends.setText("Invite Friends from PhoneBook");
        mainL.addView(inviteFriends);
        
        getWindow().addContentView(mainL, getWindow().getAttributes());
    }
}