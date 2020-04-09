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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewQueryAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;
    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;

    TextView name_tv, subject_tv, date_tv, nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_query_admin);

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

        Cursor res = db.getData_Admin_username(username);
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
                Intent i = new Intent(ViewQueryAdmin.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ViewQueryAdmin.this, AdminProfile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewQueryAdmin.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ViewQueryAdmin.this, MainActivity.class);
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
        Cursor result = db.getAllData_Contact();
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
                bf.append(result.getString(4) + "\n");
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
        header4.setText("Date");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header4.setPadding(15, 25, 15, 25);
        header4.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header4);

        TextView header = new TextView(this);
        header.setText("Name");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header3 = new TextView(this);
        header3.setText("Subject");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header3.setTextSize(15);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);


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


            date_tv = new TextView(this);
            date_tv.setText(data[4]);
            date_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            date_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            date_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            date_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            date_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(date_tv);


            name_tv = new TextView(this);
            name_tv.setText(data[0]);
            name_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            name_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            name_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            name_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            name_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(name_tv);



            subject_tv = new TextView(this);
            subject_tv.setText(data[2]);
            subject_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            subject_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //price_tv.setBackgroundColor(Color.LTGRAY);
            subject_tv.setTextSize(15);
            subject_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            subject_tv.setPadding(15, 20, 15, 20);
            subject_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(subject_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Query Date.: "+data[4] + "\n\n");
                    buffer.append("Name.: "+data[0] + "\n\n");
                    buffer.append("Email ID: "+data[1] + "\n\n");
                    buffer.append("Subject: "+data[2] + "\n\n");
                    buffer.append("Query: "+data[3]+ "\n\n");
                    showMessage("Details", buffer.toString(), data[4],data[1],data[2]);
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

    private void showMessage(String title, String message, final String date,final String email,final String subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewQueryAdmin.this); //Home is name of the activity
                builder.setMessage("Are you sure you want to delete query?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            Integer deleteRows = db.deleteQuery(date,email,subject);
                            if(deleteRows > 0) {
                                Toast.makeText(ViewQueryAdmin.this, "Query Deleted", Toast.LENGTH_SHORT).show();
                                tableLayout.removeAllViews();
                                getData();
                                if(hasData) {
                                    addHeaders();
                                    addData();
                                }
                                else{
                                    nodata.setText("No Data Available");
                                }
                            }
                            else
                                Toast.makeText(ViewQueryAdmin.this, "Query Not Deleted", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();
            }
        });


        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}

