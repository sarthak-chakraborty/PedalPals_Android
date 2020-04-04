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
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConfirmRide extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;

    TextView reg, model, price, date, ride, trans;
    EditText days;
    Button confirm;

    String reg_no;
    String username;
    String startDate;
    String endDate;
    SharedPreferences prefs;

    SimpleDateFormat sdf;
    Calendar c;

    boolean hasData=true;
    String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ride);

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

        reg = findViewById(R.id.reg_no);
        model = findViewById(R.id.model);
        price = findViewById(R.id.price);
        date = findViewById(R.id.date);
        days = findViewById(R.id.days);
        days.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});
        ride = findViewById(R.id.txt_ride_approve);
        trans = findViewById(R.id.txt_transaction_update);
        confirm = findViewById(R.id.confirm_button);

        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        prefs = this.getSharedPreferences("PedalPals", 0);
        reg_no = prefs.getString("ride_reg_no", "0");
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


        getData();
        if(hasData){
            showData();
            insertData();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(ConfirmRide.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ConfirmRide.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ConfirmRide.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ConfirmRide.this, MainActivity.class);
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
        StringBuffer bf;
        if (result.getCount() == 1) {
            hasData = true;
            bf = new StringBuffer();
            while (result.moveToNext()) {
                bf.append(result.getString(0) + ";");
                bf.append(result.getString(1) + ";");
                bf.append(result.getString(4) + ";");
                bf.append(result.getString(5));
            }
            data = bf.toString().split(";");
        }
        else
            hasData = false;
    }

    private void showData() {
        reg.setText(data[0]);
        reg.setTextSize(20);

        model.setText(data[1]);
        model.setTextSize(20);

        price.setText(data[2]);
        price.setTextSize(20);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        startDate = sdf.format(c.getTime());

        date.setText(startDate);
        date.setTextSize(20);
    }

    private void insertData(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    c.setTime(sdf.parse(startDate));
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, Integer.parseInt(days.getText().toString()));
                endDate = sdf.format(c.getTime());

                boolean isInserted = db.insertData_Transaction(username, data[3], reg_no, startDate, endDate, Integer.parseInt(data[2]));
                if(isInserted){
                    Toast.makeText(ConfirmRide.this, "Ride Approved", Toast.LENGTH_SHORT).show();
                    ride.setText("Congratulations! Ride Approved. Go to the cycle location, pay the amount and get your ride.");
                    trans.setText("Transaction is updated");
                }
                else
                    Toast.makeText(ConfirmRide.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
