package com.protal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // status bar is black for lower versions : tested on nexus S
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            // change the color of the status bar to show full screen for the logo
            HomePageActivity.this.getWindow().setStatusBarColor(ContextCompat.getColor(
                    HomePageActivity.this, android.R.color.transparent));
        final ImageView ivCompanyLogo = findViewById(R.id.ivCompanyLogo);
        final LinearLayout llLoginRegisterContainer = findViewById(R.id.llLoginRegisterContainer);

        final float Y_OFFSET = -2000f;
        int ANIMATION_START_DELAY = 2500;
        int ANIMATION_DURATION = 1000;

        // vertical translation of logo
        ivCompanyLogo.animate()
                .y(Y_OFFSET)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_START_DELAY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // status bar is black for lower versions : tested on nexus S
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            // reset the color of status bar
                            HomePageActivity.this.getWindow().setStatusBarColor(
                                    ContextCompat.getColor(HomePageActivity.this,
                                            R.color.colorPrimaryDark));
                        ivCompanyLogo.setVisibility(View.GONE);
                        //change background
                        findViewById(R.id.rlHome).setBackground(ContextCompat.getDrawable(
                                HomePageActivity.this,
                                R.drawable.ic_gradient_background_homepage));

                        findViewById(R.id.tvNameHome).setVisibility(View.VISIBLE);
                        llLoginRegisterContainer.setAlpha(0);
                        llLoginRegisterContainer.setVisibility(View.VISIBLE);
                        llLoginRegisterContainer.animate().alpha(1).setDuration(500).start();

                    }
                })
                .start();



    }
}
