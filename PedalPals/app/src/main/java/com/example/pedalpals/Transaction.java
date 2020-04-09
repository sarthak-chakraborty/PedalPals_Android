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
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Transaction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TableLayout tableLayout_ride, tableLayout_rent;
    TableRow tableRow;
    boolean hasData_ride = false, hasData_rent = false;
    StringBuffer bf_ride, bf_rent;
    String username;
    SharedPreferences prefs;
    SharedPreferences trans;

    TextView nav_head_name, nav_head_email;

    TextView ride_regno_tv, ride_st_date_tv, ride_end_date_tv, ride_amt_tv;
    TextView rent_user_tv, rent_regno_tv, rent_st_date_tv, rent_end_date_tv,id_tv, nodata_ride,nodata_rent;

    boolean flag_ride = false;
    boolean flag_rent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

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
        tableLayout_ride = findViewById(R.id.table_ride);
        nodata_ride = findViewById(R.id.nodata_ride);
        nodata_rent = findViewById(R.id.nodata_rent);
        tableLayout_rent = findViewById(R.id.table_rent);
        nav_head_name = hView.findViewById(R.id.nav_welcome);
        nav_head_email = hView.findViewById(R.id.nav_mail);
        prefs = this.getSharedPreferences("PedalPals", 0);
        trans = this.getSharedPreferences("Transactions", 0);

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


        getData_ride();
        if(hasData_ride){
            nodata_ride.setText("Ride Data");
            addHeaders_ride();
            addData_ride();
        } else {
            nodata_ride.setText("No Ride Data Available");
        }

        getData_rent();
        if(hasData_rent){
            nodata_rent.setText("Rent Data");
            addHeaders_rent();
            addData_rent();
        } else {
            nodata_rent.setText("No Rent Data Available");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(Transaction.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(Transaction.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(Transaction.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(Transaction.this, MainActivity.class);
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


    private void  getData_ride() {
        Cursor result = db.getData_Transaction_ride(username);

        if (result.getCount() == 0) {
            hasData_ride = false;
        }
        else {
            bf_ride = new StringBuffer();
            while (result.moveToNext()) {
                bf_ride.append(result.getString(0) + ";");
                bf_ride.append(result.getString(1) + ";");
                bf_ride.append(result.getString(2) + ";");
                bf_ride.append(result.getString(3) + ";");
                bf_ride.append(result.getString(4) + ";");
                bf_ride.append(result.getString(5) + ";");
                bf_ride.append(result.getString(6) + ";");
                bf_ride.append(result.getString(7) + ";");
                bf_ride.append(result.getString(8) + "\n");
            }
            bf_ride.deleteCharAt(bf_ride.length()-1);
            hasData_ride = true;
        }
    }

    private void addHeaders_ride(){
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));


        TextView header5 = new TextView(this);
        header5.setText("ID");
        header5.setTextColor(Color.WHITE);
        header5.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header5.setTextSize(15);
        header5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header5.setPadding(15, 25, 15, 25);
        header5.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header5);

        TextView header = new TextView(this);
        header.setText("Reg. No.");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header2 = new TextView(this);
        header2.setText("Start Date");
        header2.setTextColor(Color.WHITE);
        header2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header2.setTextSize(15);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header2.setPadding(15, 25, 15, 25);
        header2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header2);

        TextView header3 = new TextView(this);
        header3.setText("End Date");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header3.setTextSize(15);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);

        TextView header4 = new TextView(this);
        header4.setText("Amount");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header4.setPadding(15, 25, 15, 25);
        header4.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header4);


        tableLayout_ride.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    private void addData_ride(){
        String str = bf_ride.toString();
        Date curDate = new Date();

        String[] data_row = str.split("\n");
        for (int i = 0; i < data_row.length; i++)
        {
            final String[] data = data_row[i].split(";");

            System.out.println(data[2]);
            System.out.println(data[7]);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate=curDate, endDate=curDate;
            try{
                startDate = sdf.parse(data[4]);
                endDate = sdf.parse(data[5]);
            } catch (ParseException e){
                e.printStackTrace();
            }

            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            if((curDate.getTime()-endDate.getTime())>=0 && (Integer.parseInt(data[8]) <= 0)) {
                tableRow.setBackgroundColor(Color.rgb(250, 185, 185));
                flag_ride = true;
            }
            else
                tableRow.setBackgroundColor(getResources().getColor(R.color.color2,null));


            id_tv = new TextView(this);
            id_tv.setText(data[0]);
            id_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            id_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            id_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            id_tv.setTextSize(14);
            id_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            id_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(id_tv);

            ride_regno_tv = new TextView(this);
            ride_regno_tv.setText(data[3]);
            ride_regno_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            ride_regno_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ride_regno_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            ride_regno_tv.setTextSize(14);
            ride_regno_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            ride_regno_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(ride_regno_tv);

            ride_st_date_tv = new TextView(this);
            ride_st_date_tv.setText(data[4]);
            ride_st_date_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            ride_st_date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ride_st_date_tv.setTextSize(14);
            ride_st_date_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            ride_st_date_tv.setPadding(15, 20, 15, 20);
            ride_st_date_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(ride_st_date_tv);

            ride_end_date_tv = new TextView(this);
            ride_end_date_tv.setText(data[5]);
            ride_end_date_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            ride_end_date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ride_end_date_tv.setTextSize(14);
            ride_end_date_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            ride_end_date_tv.setPadding(15, 20, 15, 20);
            ride_end_date_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(ride_end_date_tv);


            long diff = endDate.getTime() - startDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            ride_amt_tv = new TextView(this);
            ride_amt_tv.setText("Rs. "+ (Integer.parseInt(data[6]) * days));
            ride_amt_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            ride_amt_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ride_amt_tv.setTextSize(14);
            ride_amt_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            ride_amt_tv.setPadding(15, 20, 15, 20);
            ride_amt_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(ride_amt_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(flag_ride){
                        rate_ride("Rate the Cycle", data);
                    }
                    else{
                        trans.edit().putString("transaction_id", data[0]).apply();
                        trans.edit().putString("user", data[1]).apply();
                        trans.edit().putString("owner", data[2]).apply();
                        trans.edit().putString("reg_no", data[3]).apply();
                        trans.edit().putString("start_date", data[4]).apply();
                        trans.edit().putString("end_date", data[5]).apply();
                        trans.edit().putString("price", data[6]).apply();
                        trans.edit().putBoolean("ride", true).apply();

                        Intent i = new Intent(Transaction.this, TransactionDetails.class);
                        startActivity(i);
                    }
                }
            });

            tableLayout_ride.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

        }
    }


    private void rate_ride(String title, final String[] data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final RatingBar rating = new RatingBar(this);
        rating.setMax(5);
        rating.setNumStars(5);
        rating.setStepSize(1);
        builder.setTitle(title);
        builder.setView(rating);

        builder.setPositiveButton("RATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String rate = String.valueOf(rating.getProgress());

                if (Float.parseFloat(rate) == 0){
                    Toast.makeText(Transaction.this, "No Rating Given", Toast.LENGTH_SHORT).show();

                    trans.edit().putString("transaction_id", data[0]).apply();
                    trans.edit().putString("user", data[1]).apply();
                    trans.edit().putString("owner", data[2]).apply();
                    trans.edit().putString("reg_no", data[3]).apply();
                    trans.edit().putString("start_date", data[4]).apply();
                    trans.edit().putString("end_date", data[5]).apply();
                    trans.edit().putString("price", data[6]).apply();
                    trans.edit().putBoolean("ride", true).apply();

                    Intent i = new Intent(Transaction.this, TransactionDetails.class);
                    startActivity(i);
                }
                else{
                    boolean isInserted1 = db.updateTransaction_rating_cycle(data[0], Float.parseFloat(rate));
                    if (isInserted1)
                        db.updateData_Cycle_rating(data[3]);


                    trans.edit().putString("transaction_id", data[0]).apply();
                    trans.edit().putString("user", data[1]).apply();
                    trans.edit().putString("owner", data[2]).apply();
                    trans.edit().putString("reg_no", data[3]).apply();
                    trans.edit().putString("start_date", data[4]).apply();
                    trans.edit().putString("end_date", data[5]).apply();
                    trans.edit().putString("price", data[6]).apply();
                    trans.edit().putBoolean("ride", true).apply();

                    flag_ride=false;
                    Intent i = new Intent(Transaction.this, TransactionDetails.class);
                    startActivity(i);
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });

        AlertDialog alert=builder.create();
        alert.show();
    }



    public void getData_rent(){
        Cursor result = db.getData_Transaction_rent(username);

        if (result.getCount() == 0) {
            hasData_rent = false;
        }
        else {
            bf_rent = new StringBuffer();
            while (result.moveToNext()) {
                bf_rent.append(result.getString(0) + ";");
                bf_rent.append(result.getString(1) + ";");
                bf_rent.append(result.getString(2) + ";");
                bf_rent.append(result.getString(3) + ";");
                bf_rent.append(result.getString(4) + ";");
                bf_rent.append(result.getString(5) + ";");
                bf_rent.append(result.getString(6) + ";");
                bf_rent.append(result.getString(7) + ";");
                bf_rent.append(result.getString(8) + "\n");
            }
            bf_rent.deleteCharAt(bf_rent.length()-1);
            hasData_rent = true;
        }
    }

    private void addHeaders_rent(){
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView header5 = new TextView(this);
        header5.setText("ID");
        header5.setTextColor(Color.WHITE);
        header5.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header5.setTextSize(15);
        header5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header5.setPadding(15, 25, 15, 25);
        header5.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header5);


        TextView header = new TextView(this);
        header.setText("Username");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header2 = new TextView(this);
        header2.setText("Reg.No.");
        header2.setTextColor(Color.WHITE);
        header2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header2.setTextSize(15);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header2.setPadding(15, 25, 15, 25);
        header2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header2);

        TextView header3 = new TextView(this);
        header3.setText("Start Date");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header3.setTextSize(15);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);

        TextView header4 = new TextView(this);
        header4.setText("End Date");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header4.setPadding(15, 25, 15, 25);
        header4.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header4);


        tableLayout_rent.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    private void addData_rent(){
        String str = bf_rent.toString();
        Date curDate = new Date();

        String[] data_row = str.split("\n");
        for (int i = 0; i < data_row.length; i++)
        {
            final String[] data = data_row[i].split(";");

            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate=curDate;
            try{
                endDate = sdf.parse(data[5]);
            } catch (ParseException e){
                e.printStackTrace();
            }

            if((curDate.getTime()-endDate.getTime())>=0 && (Integer.parseInt(data[7]) <= 0)) {
                tableRow.setBackgroundColor(Color.rgb(250, 185, 185));
                flag_rent = true;
            }
            else
                tableRow.setBackgroundColor(getResources().getColor(R.color.color2,null));

            id_tv = new TextView(this);
            id_tv.setText(data[0]);
            id_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            id_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            id_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            id_tv.setTextSize(14);
            id_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            id_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(id_tv);

            rent_user_tv = new TextView(this);
            rent_user_tv.setText(data[1]);
            rent_user_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            rent_user_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rent_user_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            rent_user_tv.setTextSize(14);
            rent_user_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            rent_user_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(rent_user_tv);

            rent_regno_tv = new TextView(this);
            rent_regno_tv.setText(data[3]);
            rent_regno_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            rent_regno_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rent_regno_tv.setTextSize(14);
            rent_regno_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            rent_regno_tv.setPadding(15, 20, 15, 20);
            rent_regno_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(rent_regno_tv);

            rent_st_date_tv = new TextView(this);
            rent_st_date_tv.setText(data[4]);
            rent_st_date_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            rent_st_date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rent_st_date_tv.setTextSize(15);
            rent_st_date_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            rent_st_date_tv.setPadding(15, 20, 15, 20);
            rent_st_date_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(rent_st_date_tv);

            rent_end_date_tv = new TextView(this);
            rent_end_date_tv.setText(data[5]);
            rent_end_date_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            rent_end_date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rent_end_date_tv.setTextSize(14);
            rent_end_date_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            rent_end_date_tv.setPadding(15, 20, 15, 20);
            rent_end_date_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(rent_end_date_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_rent){
                        rate_rent("Rate the User", data);
                    }
                    else{
                        trans.edit().putString("transaction_id", data[0]).apply();
                        trans.edit().putString("user", data[1]).apply();
                        trans.edit().putString("owner", data[2]).apply();
                        trans.edit().putString("reg_no", data[3]).apply();
                        trans.edit().putString("start_date", data[4]).apply();
                        trans.edit().putString("end_date", data[5]).apply();
                        trans.edit().putString("price", data[6]).apply();
                        trans.edit().putBoolean("ride", false).apply();

                        Intent i = new Intent(Transaction.this, TransactionDetails.class);
                        startActivity(i);
                    }
                }
            });

            tableLayout_rent.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

        }
    }

    private void rate_rent(String title, final String[] data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final RatingBar rating = new RatingBar(this);
        rating.setMax(5);
        rating.setNumStars(5);
        rating.setStepSize(1);
        builder.setTitle(title);
        builder.setView(rating);

        builder.setPositiveButton("RATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String rate = String.valueOf(rating.getProgress());

                if (Float.parseFloat(rate) == 0){
                    Toast.makeText(Transaction.this, "No Rating given", Toast.LENGTH_SHORT).show();

                    trans.edit().putString("transaction_id", data[0]).apply();
                    trans.edit().putString("user", data[1]).apply();
                    trans.edit().putString("owner", data[2]).apply();
                    trans.edit().putString("reg_no", data[3]).apply();
                    trans.edit().putString("start_date", data[4]).apply();
                    trans.edit().putString("end_date", data[5]).apply();
                    trans.edit().putString("price", data[6]).apply();
                    trans.edit().putBoolean("ride", false).apply();

                    Intent i = new Intent(Transaction.this, TransactionDetails.class);
                    startActivity(i);
                }
                else{
                    boolean isInserted1 = db.updateTransaction_rating_user(data[0], Float.parseFloat(rate));
                    if (isInserted1)
                        db.updateData_User_rating(data[1]);


                    trans.edit().putString("transaction_id", data[0]).apply();
                    trans.edit().putString("user", data[1]).apply();
                    trans.edit().putString("owner", data[2]).apply();
                    trans.edit().putString("reg_no", data[3]).apply();
                    trans.edit().putString("start_date", data[4]).apply();
                    trans.edit().putString("end_date", data[5]).apply();
                    trans.edit().putString("price", data[6]).apply();
                    trans.edit().putBoolean("ride", false).apply();

                    flag_rent=true;
                    Intent i = new Intent(Transaction.this, TransactionDetails.class);
                    startActivity(i);
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert=builder.create();
        alert.show();
    }

}
