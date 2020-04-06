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
import android.graphics.Color;
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

public class UpdateCycle extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;
    EditText model, color, price,condition;
    Spinner location;
    Button update;
    TextView reg;
    String location_name;
    SharedPreferences cycle;
    StringBuffer bf;
    String reg_no, username;
    int loc_index;
    String actual_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cycle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loc_index = 0;
        drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        db = new Database(this);

        reg = findViewById(R.id.reg_no);
        model = findViewById(R.id.model);
        color = findViewById(R.id.color);
        location = findViewById(R.id.location);
        price = findViewById(R.id.price);
        update = findViewById(R.id.update_button);
        condition = findViewById(R.id.condition);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        cycle = this.getSharedPreferences("PedalPals", 0);
        reg_no = cycle.getString("reg_no", "0");
        username = cycle.getString("username", "");

        Cursor res = db.getData_User_username(username);
        StringBuffer nav_head = new StringBuffer();
        while(res.moveToNext()){
            nav_head.append(res.getString(1) + " " + res.getString(2) + ";");
            nav_head.append(res.getString(3));
        }
        String[] str_nav_head = nav_head.toString().split(";");
        nav_head_name.setText(str_nav_head[0]);
        nav_head_email.setText(str_nav_head[1]);

        Cursor result = db.getData_Cycle_reg(reg_no);
        result.moveToNext();
        actual_loc = result.getString(3);

        res = db.getAllData_Location();
        String[] items;
        StringBuffer bf = new StringBuffer();
        bf.append("Select Location;");
        location_name="Select Location";
        int i=1;
        if(res.getCount() > 0){
            while(res.moveToNext()){
                bf.append(res.getString(0)+";");
                if(res.getString(0).equals(actual_loc)){
                    loc_index=i;
                }
                i++;
            }
            bf.deleteCharAt(bf.length()-1);
        }
        items = bf.toString().split(";");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateCycle.this,
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


        getData();
        updateCycle();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(UpdateCycle.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(UpdateCycle.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(UpdateCycle.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = cycle.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(UpdateCycle.this, MainActivity.class);
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


    private void getData() {
        Cursor result = db.getData_Cycle_reg(reg_no);

        bf = new StringBuffer();
        while (result.moveToNext()) {
            bf.append(result.getString(0) + ";");
            bf.append(result.getString(1) + ";");
            bf.append(result.getString(2) + ";");
            bf.append(result.getString(3) + ";");
            bf.append(result.getString(4) + ";");
            bf.append(result.getString(7));
        }

        String[] cycle_data = bf.toString().split(";");

        reg.setText(reg_no);
        reg.setTextSize(20);
        model.setText(cycle_data[1]);
        color.setText(cycle_data[2]);
        location.setSelection(loc_index);
        price.setText(cycle_data[4]);
        condition.setText(cycle_data[5]);
    }


    private void updateCycle() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(price.getText().toString().trim().equals("") || model.getText().toString().trim().equals("") || color.getText().toString().trim().equals("") ||
                        condition.getText().toString().trim().equals("")){
                    Toast.makeText(UpdateCycle.this, "Fields cannot be left blank", Toast.LENGTH_SHORT).show();
                }

                try {
                    Integer p = Integer.parseInt(price.getText().toString().trim());
                }
                catch(Exception e){
                    Toast.makeText(UpdateCycle.this, "Price needs to be Integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(location.equals("Select Location")){
                    Toast.makeText(UpdateCycle.this, "Select Valid Location", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = db.updateData_Cycle(reg_no,
                        model.getText().toString().trim(),
                        color.getText().toString().trim(),
                        location_name,
                        Integer.parseInt(price.getText().toString().trim()),
                        condition.getText().toString().trim());

                if (isInserted) {
                    Toast.makeText(UpdateCycle.this, "Cycle Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateCycle.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
                Intent i =new Intent(UpdateCycle.this, ManageCycle.class);
                startActivity(i);
                finish();
            }
        });
    }
}

