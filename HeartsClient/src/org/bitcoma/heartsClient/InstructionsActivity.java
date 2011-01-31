package org.bitcoma.heartsClient;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class InstructionsActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Here are the instructions for the user.
        ScrollView scrollView = new ScrollView(this);
        TextView content = new TextView(this);
        content.setText("\nHere are the instructions to play this game.\n\nYou can play this game for fun");
        content.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
        content.setTextSize(15);
        content.setTextColor(Color.parseColor("#ffffff"));
        
        scrollView.setBackgroundColor(Color.parseColor("#228b22"));
        scrollView.addView(content);
        getWindow().addContentView(scrollView, getWindow().getAttributes());
        
    }
}