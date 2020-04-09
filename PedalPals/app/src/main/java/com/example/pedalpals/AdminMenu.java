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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class AdminMenu extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;

    Button user, cycle, transaction, location, query;
    TextView welcome;
    private String username = "";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

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

        user = findViewById(R.id.user_admin_button);
        cycle = findViewById(R.id.cycle_admin_button);
        transaction = findViewById(R.id.transaction_admin_button);
        location = findViewById(R.id.location_admin_button);
        welcome = findViewById(R.id.text_welcome);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);
        query = findViewById(R.id.contact_query_button);

        prefs = this.getSharedPreferences("PedalPals", 0);
        username = prefs.getString("username", "UNKNOWN");

        Cursor res = db.getData_Admin_username(username);
        StringBuffer nav_head = new StringBuffer();
        while(res.moveToNext()){
            nav_head.append(res.getString(1) + " " + res.getString(2) + ";");
            nav_head.append(res.getString(3));
        }
        String[] str_nav_head = nav_head.toString().split(";");
        nav_head_name.setText(str_nav_head[0]);
        nav_head_email.setText(str_nav_head[1]);


        welcome.setText("Welcome " + username);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenu.this, ViewUserAdmin.class);
                startActivity(i);
            }
        });

        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenu.this, ViewCycleAdmin.class);
                startActivity(i);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenu.this, TransactionAdmin.class);
                startActivity(i);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenu.this, ManageLocationsAdmin.class);
                startActivity(i);
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenu.this, ViewQueryAdmin.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(AdminMenu.this, AdminProfile.class);
                startActivity(i1);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminMenu.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(AdminMenu.this, MainActivity.class);
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
}
