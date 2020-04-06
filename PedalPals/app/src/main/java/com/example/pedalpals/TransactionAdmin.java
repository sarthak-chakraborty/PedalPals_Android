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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TransactionAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;
    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;

    TextView user_tv, regno_tv, id_tv, owner_tv, nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_admin);

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
        tableLayout = findViewById(R.id.table);
        nodata = findViewById(R.id.nodata);
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

        getData();

        if (hasData) {
            addHeaders();
            addData();
        } else {
            nodata.setText("No Data Available");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(TransactionAdmin.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(TransactionAdmin.this, AdminProfile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(TransactionAdmin.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(TransactionAdmin.this, MainActivity.class);
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


    public void getData() {
        Cursor result = db.getAllData_Transaction();
        if (result.getCount() == 0) {
            hasData = false;
        }
        else {
            bf = new StringBuffer();
            while (result.moveToNext()) {
                bf.append(result.getString(0) + ";");
                bf.append(result.getString(1) + ";");
                bf.append(result.getString(2) + ";");
                bf.append(result.getString(3) + ";");
                bf.append(result.getString(4) + ";");
                bf.append(result.getString(5) + ";");
                bf.append(result.getString(6) + ";");
                bf.append(result.getString(7) + ";");
                bf.append(result.getString(8) + "\n");
            }
            bf.deleteCharAt(bf.length()-1);
            hasData = true;
        }
    }

    public void addHeaders() {
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));


        TextView header4 = new TextView(this);
        header4.setText("ID");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header4.setPadding(15, 25, 15, 25);
        header4.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header4);

        TextView header = new TextView(this);
        header.setText("User ID");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header3 = new TextView(this);
        header3.setText("Cycle Reg. No.");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header3.setTextSize(15);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);

        TextView header5 = new TextView(this);
        header5.setText("Owner ID");
        header5.setTextColor(Color.WHITE);
        header5.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header5.setTextSize(15);
        header5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header5.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header5.setPadding(15, 25, 15, 25);
        header5.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header5);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }


    public void addData() {
        String str = bf.toString();

        String[] data_row = str.split("\n");
        for (int i = 0; i < data_row.length; i++)
        {
            final String[] data = data_row[i].split(";");
            tableRow = new TableRow(this);
            tableRow.setBackgroundColor(getResources().getColor(R.color.color4,null));
            tableRow.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));


            id_tv = new TextView(this);
            id_tv.setText(data[0]);
            id_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            id_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            id_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            id_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            id_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            id_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(id_tv);


            user_tv = new TextView(this);
            user_tv.setText(data[1]);
            user_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            user_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            user_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            user_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            user_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            user_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(user_tv);



            regno_tv = new TextView(this);
            regno_tv.setText(data[3]);
            regno_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            regno_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //price_tv.setBackgroundColor(Color.LTGRAY);
            regno_tv.setTextSize(15);
            regno_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            regno_tv.setPadding(15, 20, 15, 20);
            regno_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(regno_tv);

            owner_tv = new TextView(this);
            owner_tv.setText(data[2]);
            owner_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            owner_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //price_tv.setBackgroundColor(Color.LTGRAY);
            owner_tv.setTextSize(15);
            owner_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            owner_tv.setPadding(15, 20, 15, 20);
            owner_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(owner_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Transaction ID: "+data[0] + "\n\n");
                    buffer.append("User ID: "+data[1] + "\n\n");
                    buffer.append("Cycle Reg. No.: "+data[3] + "\n\n");
                    buffer.append("Owner ID: "+data[2] + "\n\n");
                    buffer.append("Start Date: "+data[4] + "\n\n");
                    buffer.append("End Date: "+data[5] + "\n\n");
                    buffer.append("Price Per Day: "+data[6] + "\n\n");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date curDate = new Date();
                    Date startDate=curDate, endDate=curDate;
                    try{
                        startDate = sdf.parse(data[4]);
                        endDate = sdf.parse(data[5]);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    long diff = endDate.getTime() - startDate.getTime();
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    buffer.append("Amount: Rs. " + (Integer.parseInt(data[6]) * days)+"\n\n");

                    String user_rating;
                    String cycle_rating;

                    if(data[7].isEmpty() || data[7].equals("0"))
                        user_rating = "NA";
                    else
                        user_rating = data[7];

                    if(data[8].isEmpty() || data[8].equals("0"))
                        cycle_rating = "NA";
                    else
                        cycle_rating = data[8];

                    buffer.append("User Rating: "+user_rating+ "\n\n");
                    buffer.append("Cycle Rating: "+cycle_rating+ "\n\n");
                    showMessage("Details", buffer.toString());
                }
            });

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }

    }


          /*  tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Username: " + data[0] + "\n\n");
                    buffer.append("Name: " + data[1] + " " + data[2] + "\n\n");
                    buffer.append("Email ID: " + data[3] + "\n\n");
                    buffer.append("Room: " + data[4] + "\n\n");
                    buffer.append("Hall: " + data[5] + "\n\n");

                    if (data[7] == "") {
                        data[7] = "NA";
                    }
                    buffer.append("Rating: " + data[7] + "\n\n");


                    showMessage("Details", buffer.toString(), data[0]);
                }
            });*/

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}

