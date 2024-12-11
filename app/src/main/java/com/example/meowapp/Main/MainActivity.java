package com.example.meowapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import com.example.meowapp.R;
import com.example.meowapp.service.TimerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main_for_user);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView.setItemIconTintList(null);

        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("duration", 5 * 60 * 1000); // 5 phút
        startService(serviceIntent);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment selectedFragment = null;

                if (itemId == R.id.tab_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.tab_rank) {
                    selectedFragment = new RankFragment();
                } else if (itemId == R.id.tab_user) {
                    selectedFragment = new UserFragment();
                }  else if (itemId == R.id.tab_practice) {
                    selectedFragment = new PracticeFragment();
                } else if (itemId == R.id.tab_award) {
                    selectedFragment = new RewardFragment();
                }


                if (selectedFragment != null) {
                    loadFragment(selectedFragment, false);
                }

                return true;
            }
        });

        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // If app is initialized, use add, otherwise replace
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        // Optionally add to back stack
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
