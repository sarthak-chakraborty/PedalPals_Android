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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TextView user;
    EditText first_name, last_name, email, hall, room, mobile, password, re_password;
    Button update;
    SharedPreferences prefs;

    TextView nav_head_name, nav_head_email;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        hall = findViewById(R.id.hall);
        room = findViewById(R.id.room);
        user = findViewById(R.id.username);
        mobile = findViewById(R.id.mobile);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        update = findViewById(R.id.update_button);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        prefs = this.getSharedPreferences("PedalPals", 0);
        username = prefs.getString("username", "");

        Cursor res = db.getData_User_username(username);
        StringBuffer nav_head = new StringBuffer();
        while(res.moveToNext()){
            nav_head.append(res.getString(1) + " " + res.getString(2) + ";");
            nav_head.append(res.getString(3));
        }
        String[] str_nav_head = nav_head.toString().split(";");
        nav_head_name.setText(str_nav_head[0]);
        nav_head_email.setText(str_nav_head[1]);

        showData();
        updateData();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(Profile.this, Menu.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(Profile.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(Profile.this, MainActivity.class);
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


    private void showData() {
        Cursor res = db.getData_User_username(username);

        StringBuffer bf = new StringBuffer();
        while(res.moveToNext()){
            bf.append(res.getString(1) + ";");
            bf.append(res.getString(2) + ";");
            bf.append(res.getString(3) + ";");
            bf.append(res.getString(4) + ";");
            bf.append(res.getString(5) + ";");
            bf.append(res.getString(8));
        }

        String[] data = bf.toString().split(";");

        user.setText(username);
        first_name.setText(data[0]);
        last_name.setText(data[1]);
        email.setText(data[2]);
        hall.setText(data[4]);
        room.setText(data[3]);
        mobile.setText(data[5]);
    }


    private void updateData() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first_name.getText().toString().trim().equals("") ||
                        email.getText().toString().trim().equals("") || hall.getText().toString().trim().equals("") ||
                        room.getText().toString().trim().equals("") || mobile.getText().toString().trim().equals("")){
                    Toast.makeText(Profile.this, "Fields left blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor res;
                res = db.getData_User_email_id(email.getText().toString().trim());
                if(res.getCount()!=0){
                    Toast.makeText(Profile.this, "Email ID is already in use", Toast.LENGTH_SHORT).show();
                    return;
                }

                res = db.getData_User_mobile_number(mobile.getText().toString().trim());
                if(res.getCount()!=0){
                    Toast.makeText(Profile.this, "Mobile Number is already in use", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Long i = Long.parseLong(mobile.getText().toString().trim());
                } catch (NumberFormatException nfe) {
                    Toast.makeText(Profile.this, "Mobile Number is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    boolean isInserted = db.updateData_User(username,
                            first_name.getText().toString().trim(),
                            last_name.getText().toString().trim(),
                            email.getText().toString().trim(),
                            hall.getText().toString().trim(),
                            room.getText().toString().trim(),
                            mobile.getText().toString().trim()
                            );

                    if (isInserted) {
                        Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(Profile.this, Profile.class);
                    startActivity(i);
                    finish();
                }
                else{
                    if (password.getText().toString().equals(re_password.getText().toString())){
                        boolean isInserted = db.updateData_User_pass(username,
                                first_name.getText().toString().trim(),
                                last_name.getText().toString().trim(),
                                email.getText().toString().trim(),
                                hall.getText().toString().trim(),
                                room.getText().toString().trim(),
                                password.getText().toString().trim(),
                                mobile.getText().toString().trim());

                        if (isInserted) {
                            Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                        Intent i = new Intent(Profile.this, Profile.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(Profile.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        password.getText().clear();
                        re_password.getText().clear();
                    }
                }
            }
        });
    }
}
