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
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TransactionDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    SharedPreferences trans, prefs;
    TextView transac_id_tv, reg_no_tv, model_tv, color_tv, location_tv, cycle_rating_tv;
    TextView user_info_tv, name_tv, email_tv, hall_tv, user_rating_tv,mobile_tv;
    TextView st_date_tv, end_date_tv, price_tv, days_tv, amt_tv;

    String username;
    String transac_id, user, owner, reg_no, start_date, end_date, price;
    boolean ride;

    TextView nav_head_name, nav_head_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

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

        prefs = this.getSharedPreferences("PedalPals", 0);
        username = prefs.getString("username", "");

        trans = this.getSharedPreferences("Transactions", 0);
        transac_id = trans.getString("transaction_id", "");
        user = trans.getString("user", "");
        owner = trans.getString("owner", "");
        reg_no = trans.getString("reg_no", "");
        start_date = trans.getString("start_date", "");
        end_date = trans.getString("end_date", "");
        price = trans.getString("price", "");
        ride = trans.getBoolean("ride", false);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);

        transac_id_tv = findViewById(R.id.transac_id);
        reg_no_tv = findViewById(R.id.reg_no);
        model_tv = findViewById(R.id.model);
        color_tv = findViewById(R.id.color);
        location_tv = findViewById(R.id.location);
        cycle_rating_tv = findViewById(R.id.cycle_rating);


        user_info_tv = findViewById(R.id.user_info);
        name_tv = findViewById(R.id.name);
        email_tv = findViewById(R.id.email);
        hall_tv = findViewById(R.id.hall);
        user_rating_tv = findViewById(R.id.user_rating);
        mobile_tv = findViewById(R.id.mobile);

        st_date_tv = findViewById(R.id.st_date);
        end_date_tv = findViewById(R.id.end_date);
        price_tv = findViewById(R.id.price);
        days_tv = findViewById(R.id.days);
        amt_tv = findViewById(R.id.amt);


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

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(TransactionDetails.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(TransactionDetails.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(TransactionDetails.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(TransactionDetails.this, MainActivity.class);
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

    private void showData(){
        StringBuffer bf_cycle = new StringBuffer();
        Cursor res = db.getData_Cycle_reg(reg_no);

        while(res.moveToNext()){
            bf_cycle.append(res.getString(1) + ";");
            bf_cycle.append(res.getString(2) + ";");
            bf_cycle.append(res.getString(3) + ";");
            bf_cycle.append(res.getString(6));
        }
        String[] str_cycle = bf_cycle.toString().split(";");

        StringBuffer bf_user = new StringBuffer();
        if (ride){
            user_info_tv.setText("Owner Info");
           res = db.getData_User_username(owner);

            while(res.moveToNext()){
                bf_user.append(res.getString(1) + ";");
                bf_user.append(res.getString(2) + ";");
                bf_user.append(res.getString(3) + ";");
                bf_user.append(res.getString(4) + ";");
                bf_user.append(res.getString(5) + ";");
                bf_user.append(res.getString(7) + ";");
                bf_user.append(res.getString(8) + "\n");
            }
        }
        else{
            user_info_tv.setText("User Info");
            res = db.getData_User_username(user);

            while(res.moveToNext()){
                bf_user.append(res.getString(1) + ";");
                bf_user.append(res.getString(2) + ";");
                bf_user.append(res.getString(3) + ";");
                bf_user.append(res.getString(4) + ";");
                bf_user.append(res.getString(5) + ";");
                bf_user.append(res.getString(7) + ";");
                bf_user.append(res.getString(8) + "\n");
            }
        }
        String[] str_usr = bf_user.toString().split(";");

        transac_id_tv.setText(transac_id);

        reg_no_tv.setText(reg_no);
        model_tv.setText(str_cycle[0]);
        color_tv.setText(str_cycle[1]);
        location_tv.setText(str_cycle[2]);
        if(str_cycle[3].isEmpty() || str_cycle[3].equals("0"))
            cycle_rating_tv.setText("N/A");
        else
            cycle_rating_tv.setText(str_cycle[3]);

        name_tv.setText(str_usr[0] + " " + str_usr[1]);
        email_tv.setText(str_usr[2]);
        hall_tv.setText(str_usr[4] + " " + str_usr[3]);
        if(str_usr[5].isEmpty() || str_usr[5].equals("0"))
            user_rating_tv.setText("NA");
        else
            user_rating_tv.setText(str_usr[5]);

        st_date_tv.setText(start_date);
        end_date_tv.setText(end_date);
        price_tv.append("Rs. " + price);
        mobile_tv.setText(str_usr[6]);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date();
        Date startDate=curDate, endDate=curDate;
        try{
            startDate = sdf.parse(start_date);
            endDate = sdf.parse(end_date);
        } catch (ParseException e){
            e.printStackTrace();
        }
        long diff = endDate.getTime() - startDate.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        days_tv.setText(Long.toString(days));
        amt_tv.setText("Rs. " + (Integer.parseInt(price) * days));
    }
}
