package com.raksha.assignment.assignment4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BouncingCircleSurface bouncingCircleSurface
                = (BouncingCircleSurface) findViewById(R.id.bouncingBallSurface);

        Button button = (Button) findViewById(R.id.clear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bouncingCircleSurface.clearView();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
