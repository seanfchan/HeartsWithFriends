package org.bitcoma.heartsClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ml = new LinearLayout(this);
        ml.setBackgroundResource(R.drawable.splashback);

        TextView tv = new TextView(this);
        tv.setText("BITCOMA");
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.parseColor("#ffffff"));
        ml.addView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        getWindow().addContentView(ml, getWindow().getAttributes());

        // splash thread backend
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    Intent i = new Intent(SplashActivity.this, AppHomeActivity.class);
                    startActivity(i);
                }
            }
        };
        splashThread.start();

    }
}