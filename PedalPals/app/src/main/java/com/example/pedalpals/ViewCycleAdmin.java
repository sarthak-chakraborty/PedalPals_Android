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

public class ViewCycleAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;

    TextView regno_tv, model_tv, color_tv, owner_tv,nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cycle_admin);

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
                Intent i = new Intent(ViewCycleAdmin.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ViewCycleAdmin.this, AdminProfile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewCycleAdmin.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ViewCycleAdmin.this, MainActivity.class);
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
        Cursor result = db.getAllData_Cycle();
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
                bf.append(result.getString(7) + "\n");
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
        header.setText("Reg. No.");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header.setTextSize(15);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header2 = new TextView(this);
        header2.setText("Model");
        header2.setTextColor(Color.WHITE);
        header2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header2.setTextSize(15);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header2.setPadding(15, 25, 15, 25);
        header2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header2);

        TextView header3 = new TextView(this);
        header3.setText("Colour");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header3.setTextSize(15);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);

        TextView header4 = new TextView(this);
        header4.setText("Owner Username");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header4.setPadding(15, 25, 15, 25);
        header4.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header4);

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


            regno_tv = new TextView(this);
            regno_tv.setText(data[0]);
            regno_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            regno_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            regno_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            regno_tv.setTextSize(15);
            //reg_no_tv.setBackgroundColor(Color.LTGRAY);
            regno_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            regno_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(regno_tv);

            model_tv = new TextView(this);
            model_tv.setText(data[1]);
            model_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            model_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            model_tv.setTextSize(15);
            // model_tv.setBackgroundColor(Color.LTGRAY);
            model_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            model_tv.setPadding(15, 20, 15, 20);
            model_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(model_tv);

            color_tv = new TextView(this);
            color_tv.setText(data[2]);
            color_tv.setTextColor(getResources().getColor(R.color.colorAccent,null));
            color_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //price_tv.setBackgroundColor(Color.LTGRAY);
            color_tv.setTextSize(15);
            color_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            color_tv.setPadding(15, 20, 15, 20);
            color_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(color_tv);

            owner_tv = new TextView(this);
            owner_tv.setText(data[5]);
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
                    buffer.append("Reg. No.: "+data[0] + "\n\n");
                    buffer.append("Model: "+data[1]+"\n\n");
                    buffer.append("Colour: "+data[2] + "\n\n");
                    buffer.append("Location: "+data[3] + "\n\n");
                    buffer.append("Price per day: Rs. "+data[4] +"\n\n");
                    if(data[6].trim().equals("0")){
                        buffer.append("Rating: NA\n\n");
                    }
                    else {
                        buffer.append("Rating: " + data[6] + "\n\n");
                    }
                    buffer.append("Condition: "+data[7] + "\n\n");

                    Cursor result_owner = db.getData_User_username(data[5]);
                    result_owner.moveToNext();

                    buffer.append("Owner Username: "+result_owner.getString(0)+"\n\n");
                    buffer.append("Owner Name: "+result_owner.getString(1)+" "+result_owner.getString(2)+"\n\n");
                    buffer.append("Owner Email-ID: "+result_owner.getString(3)+"\n\n");
                    buffer.append("Owner Mobile Number: "+result_owner.getString(8)+"\n\n");


                    showMessage("Details", buffer.toString(), data[0], data[5]);
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

    private void showMessage(String title, String message, final String reg_no, final String usrname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewCycleAdmin.this); //Home is name of the activity
                builder.setMessage("Are you sure you want to delete cycle?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        String date = sdf.format(c.getTime());

                        Cursor res = db.getData_Transaction_delete(reg_no, date);
                        if(res.getCount() > 0){
                            Toast.makeText(ViewCycleAdmin.this, "Cannot Delete. Cycle is currently involved in a transaction.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Integer deleteRows = db.deleteData_Cycle(reg_no,usrname);
                            if(deleteRows > 0) {
                                Toast.makeText(ViewCycleAdmin.this, "Cycle Deleted", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ViewCycleAdmin.this, "Cycle Not Deleted", Toast.LENGTH_SHORT).show();
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

