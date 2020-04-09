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

public class ViewUserAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;
    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;

    TextView username_tv, name_tv, email_tv, n_cycles_tv, nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_admin);

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
                Intent i = new Intent(ViewUserAdmin.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ViewUserAdmin.this, AdminProfile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewUserAdmin.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ViewUserAdmin.this, MainActivity.class);
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
        Cursor result = db.getAllData_User();
        Cursor result2 = db.getAllData_User_cycle();

        if (result.getCount() == 0) {
            hasData = false;
        }
        else {
            HashMap<String,String> n_cycles = new HashMap<String, String>();
            while(result2.moveToNext()){
                n_cycles.put(result2.getString(0),result2.getString(1));
            }
            bf = new StringBuffer();
            while (result.moveToNext()) {
                bf.append(result.getString(0) + ";");
                bf.append(result.getString(1) + ";");
                bf.append(result.getString(2) + ";");
                bf.append(result.getString(3) + ";");
                bf.append(result.getString(4) + ";");
                bf.append(result.getString(5) + ";");
                bf.append(result.getString(7) + ";");
                bf.append(result.getString(8) + ";");
                bf.append(n_cycles.get(result.getString(0)) + "\n");
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


        TextView header = new TextView(this);
        header.setText("Username");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header2 = new TextView(this);
        header2.setText("Name");
        header2.setTextColor(Color.WHITE);
        header2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header2.setTextSize(15);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header2.setPadding(15, 25, 15, 25);
        header2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header2);

        TextView header3 = new TextView(this);
        header3.setText("No. of Cycles");
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


            username_tv = new TextView(this);
            username_tv.setText(data[0]);
            username_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            username_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            username_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            username_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            username_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            username_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(username_tv);

            name_tv = new TextView(this);
            name_tv.setText(data[1]+" "+data[2]);
            name_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            name_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name_tv.setTextSize(15);
            // model_tv.setBackgroundColor(Color.LTGRAY);
            name_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            name_tv.setPadding(15, 20, 15, 20);
            name_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(name_tv);

            n_cycles_tv = new TextView(this);
            n_cycles_tv.setText(data[8]);
            n_cycles_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            n_cycles_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //price_tv.setBackgroundColor(Color.LTGRAY);
            n_cycles_tv.setTextSize(15);
            n_cycles_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            n_cycles_tv.setPadding(15, 20, 15, 20);
            n_cycles_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(n_cycles_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Username.: "+data[0] + "\n\n");
                    buffer.append("Name: "+data[1] + " "+data[2]+"\n\n");
                    buffer.append("Email ID: "+data[3] + "\n\n");
                    buffer.append("Mobile Number: "+data[7] + "\n\n");
                    buffer.append("Room and Hall: "+data[4] +", "+data[5]+ "\n\n");
                    if(data[6].trim().equals("0")){
                        buffer.append("Rating: NA\n\n");
                    }
                    else {
                        buffer.append("Rating: " + data[6] + "\n\n");
                    }
                    buffer.append("Number of Cycles: "+data[8] + "\n\n");

                    Cursor result_cycle = db.getRegNo_Cycle_username(data[0]);
                    if (result_cycle.getCount() != 0) {
                        buffer.append("Cycle Numbers: ");
                        result_cycle.moveToNext();
                        buffer.append(result_cycle.getString(0));
                        while(result_cycle.moveToNext()){
                            buffer.append(", "+result_cycle.getString(0));
                        }
                        buffer.append("\n\n");
                    }

                    showMessage("Details", buffer.toString(), data[0], data[8]);
                }
            });


            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

        }

    }


    private void showMessage(String title, String message, final String username, final String n_cycles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewUserAdmin.this); //Home is name of the activity
                builder.setMessage("Are you sure you want to delete user?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        String date = sdf.format(c.getTime());

                        Cursor res = db.getData_Transaction_user_delete(username, date);
                        if(res.getCount() > 0){
                            Toast.makeText(ViewUserAdmin.this, "Cannot Delete. User is currently involved in a transaction.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Integer delRows = db.deleteData_Cycle_username(username);
                            if(delRows > 0){
                                Integer deleteRows = db.deleteUser(username);
                                if(deleteRows > 0) {
                                    Toast.makeText(ViewUserAdmin.this, "User Deleted", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ViewUserAdmin.this, "User Not Deleted", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(n_cycles.equals("0")){
                                    Integer deleteRows = db.deleteUser(username);
                                    if(deleteRows > 0) {
                                        Toast.makeText(ViewUserAdmin.this, "User Deleted", Toast.LENGTH_SHORT).show();
                                        tableLayout.removeAllViews();
                                        getData();
                                        if (hasData) {
                                            addHeaders();
                                            addData();
                                        } else {
                                            nodata.setText("No Data Available");

                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(ViewUserAdmin.this, "User Not Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
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

