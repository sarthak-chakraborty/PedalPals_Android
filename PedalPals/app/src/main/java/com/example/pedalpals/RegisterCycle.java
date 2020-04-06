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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class RegisterCycle extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    EditText reg_no, model, color, price,condition;
    Spinner location;
    Button register;

    TextView nav_head_name, nav_head_email;

    String location_name;
    String username;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_cycle);

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
        reg_no = findViewById(R.id.reg_no);
        model = findViewById(R.id.model);
        color = findViewById(R.id.color);
        location = findViewById(R.id.location);
        price = findViewById(R.id.price);
        condition = findViewById(R.id.condition);
        register = findViewById(R.id.register_button);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        prefs = this.getSharedPreferences("PedalPals", 0);
        username = prefs.getString("username", "");

        Cursor res1 = db.getData_User_username(username);
        StringBuffer nav_head = new StringBuffer();
        while(res1.moveToNext()){
            nav_head.append(res1.getString(1) + " " + res1.getString(2) + ";");
            nav_head.append(res1.getString(3));
        }
        String[] str_nav_head = nav_head.toString().split(";");
        nav_head_name.setText(str_nav_head[0]);
        nav_head_email.setText(str_nav_head[1]);


        Cursor res = db.getAllData_Location();
        String[] items;
        StringBuffer bf = new StringBuffer();
        bf.append("Select Location;");
        location_name="Select Location";
        if(res.getCount() > 0){
            while(res.moveToNext()){
                bf.append(res.getString(0)+";");
            }
            bf.deleteCharAt(bf.length()-1);
        }
        items = bf.toString().split(";");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterCycle.this,
                android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
            int position, long id) {

                Object item = parent.getItemAtPosition(position);
                location_name = String.valueOf(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        registerCycle();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(RegisterCycle.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(RegisterCycle.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterCycle.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(RegisterCycle.this, MainActivity.class);
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


    private void registerCycle() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(price.getText().toString().trim().equals("") || model.getText().toString().trim().equals("") || color.getText().toString().trim().equals("") ||
                        reg_no.getText().toString().trim().equals("") || condition.getText().toString().trim().equals("")){
                    Toast.makeText(RegisterCycle.this, "Fields cannot be left blank", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Integer reg = Integer.parseInt(reg_no.getText().toString().trim());
                    }
                    catch(Exception e){
                        Toast.makeText(RegisterCycle.this, "Registration Number needs to be Integer", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Integer p = Integer.parseInt(price.getText().toString().trim());
                    }
                    catch(Exception e){
                        Toast.makeText(RegisterCycle.this, "Price needs to be Integer", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(location_name.equals("Select Location")){
                        Toast.makeText(RegisterCycle.this, "Select Valid Location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isInserted = db.insertData_Cycle(Integer.parseInt(reg_no.getText().toString().trim()),
                            model.getText().toString().trim(),
                            color.getText().toString().trim(),
                            location_name,
                            Integer.parseInt(price.getText().toString().trim()),
                            username,
                            condition.getText().toString().trim());

                    if (isInserted) {
                        Toast.makeText(RegisterCycle.this, "Cycle Registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterCycle.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    reg_no.getText().clear();
                    model.getText().clear();
                    color.getText().clear();
                    price.getText().clear();
                    condition.getText().clear();
                    location.setSelection(0);
                }
            }
        });
    }
}
