package org.bitcoma.heartsClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePlayerGameRoom extends Activity {

    SlidingDrawer slidingDrawer;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LayoutInflater inflater = getLayoutInflater();
        View slideView = inflater.inflate(R.layout.sliding, null);
        
        getWindow().addContentView(slideView, getWindow().getAttributes());

        slidingDrawer = (SlidingDrawer) findViewById(R.id.drawer);
        
        //slidingDrawer.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
             
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() 
        {

            @Override
            public void onDrawerOpened() {
                
                Toast.makeText(SinglePlayerGameRoom.this, "OPEN", Toast.LENGTH_LONG).show();
            }
            
        });
        
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener()
        {

            @Override
            public void onDrawerClosed() {
                
                Toast.makeText(SinglePlayerGameRoom.this, "CLOSE", Toast.LENGTH_LONG).show();
            }
            
        });
        
        
    }
}