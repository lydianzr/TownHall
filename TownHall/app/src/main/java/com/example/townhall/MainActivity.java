package com.example.townhall;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.townhall.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity  {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        replace(new HomeFragment());

        Toolbar toolbar = findViewById(R.id.TBMainAct);
        setSupportActionBar(toolbar);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
        NavController navController = host.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        setupBottomNavMenu(navController);

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.NHFMain, new MyMapsActivity())
//                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            Navigation.findNavController(this, R.id.NHFMain).navigate(item.getItemId());
            return true;
        } catch (Exception ex) {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.NHFMain).navigateUp();
    }

    private void setupBottomNavMenu(NavController navController) {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Set up navigation with the NavController
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Add a custom listener to handle the Forum item
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.forumsFragment) {
                // Launch the ForumActivity
                Intent intent = new Intent(this, ForumActivity.class);
                startActivity(intent);
                return true; // Indicate that the event has been handled
            } else if (item.getItemId() == R.id.govFragment) {
                // Launch the ForumActivity
                Intent intent = new Intent(this, MainActivityRauf.class);
                startActivity(intent);
                return true; // Indicate that the event has been handled
            } else {
                // Let the NavigationUI handle other cases
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });
    }


}