package com.example.meowapp.Main;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

        // Kiểm tra service trước khi khởi động
        if (!isServiceRunning(TimerService.class)) {
            Intent serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("duration", 2 * 1000); // 2 giây
            startService(serviceIntent);
        }

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
                } else if (itemId == R.id.tab_practice) {
                    selectedFragment = new PracticeFragment();
                } else if (itemId == R.id.tab_award) {
                    selectedFragment = new RewardFragment();
                }

                if (selectedFragment != null) {
                    try {
                        loadFragment(selectedFragment, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
        });

        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
