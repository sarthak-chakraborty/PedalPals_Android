package com.example.pedalpals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class AdminProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TextView user, first_name, last_name, email;
    SharedPreferences prefs;
    TextView nav_head_name, nav_head_email;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        db = new Database(this);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        user = findViewById(R.id.username);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        prefs = this.getSharedPreferences("PedalPals", 0);
        username = prefs.getString("username", "");

        Cursor res = db.getData_Admin_username(username);
        StringBuffer nav_head = new StringBuffer();
        while(res.moveToNext()){
            nav_head.append(res.getString(1) + " " + res.getString(2) + ";");
            nav_head.append(res.getString(3));
        }
        String[] str_nav_head = nav_head.toString().split(";");
        nav_head_name.setText(str_nav_head[0]);
        nav_head_email.setText(str_nav_head[1]);

        showData();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(AdminProfile.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_logout:
                logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private void logout(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminProfile.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(AdminProfile.this, MainActivity.class);
                startActivity(i);
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert=builder.create();
        alert.show();
    }

    public void showData(){
        Cursor res = db.getData_Admin_username(username);

        StringBuffer bf = new StringBuffer();
        while(res.moveToNext()){
            bf.append(res.getString(1) + ";");
            bf.append(res.getString(2) + ";");
            bf.append(res.getString(3));
        }

        String[] data = bf.toString().split(";");

        user.setText(username);
        first_name.setText(data[0]);
        last_name.setText(data[1]);
        email.setText(data[2]);
    }
}
