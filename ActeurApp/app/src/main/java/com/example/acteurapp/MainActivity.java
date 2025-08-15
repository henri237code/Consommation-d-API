package com.example.acteurapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import ui.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_main);
        BottomNavigationView nav=findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = (item.getItemId()==R.id.nav_catalog) ? new AllAuthorsFragment() : new MyProfilesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            return true;
        });
        nav.setSelectedItemId(R.id.nav_home);
    }
}