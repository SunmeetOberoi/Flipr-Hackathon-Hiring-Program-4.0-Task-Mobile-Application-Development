package com.protal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.protal.R;
import com.protal.fragments.ContainerFragment;

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

        // intro animation attributes
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
                        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                            startActivity(new Intent(HomePageActivity.this,
                                    MainHomePageActivity.class));
                            HomePageActivity.this.finish();
                        }
                        else {
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
                            showFragment(ContainerFragment.class);
                        }
                    }
                })
                .start();



    }

    private void showFragment(Class fragmentclass) {

        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentclass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentclass == ContainerFragment.class){
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.rlFragmentHome, fragment)
                        .commit();
            }
            else{
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.rlFragmentHome, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
