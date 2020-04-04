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

public class ManageCycle extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;

    TextView reg_no_tv, model_tv, price_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cycle);

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
        if(hasData){
            addHeaders();
            addData();
        }
        else{
            TextView noData = new TextView(this);
            noData.setTextSize(20);
            noData.setText("No Data");
            noData.setTextColor(Color.BLACK);
            noData.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            noData.setPadding(15, 25, 15, 25);
            noData.setTypeface(Typeface.SERIF, Typeface.BOLD);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(ManageCycle.this, Menu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ManageCycle.this, Profile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ManageCycle.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ManageCycle.this, MainActivity.class);
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

    private void  getData() {
        Cursor result = db.getData_Cycle_username(username);

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
                bf.append(result.getString(6) + "\n");
            }
            bf.deleteCharAt(bf.length()-1);
            hasData = true;
        }
    }


    private void addHeaders() {
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));


        TextView header = new TextView(this);
        header.setText("Reg. No.");
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(Color.DKGRAY);
        header.setTextSize(20);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        header.setPadding(15, 25, 15, 25);
        header.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header);

        TextView header2 = new TextView(this);
        header2.setText("Model");
        header2.setTextColor(Color.WHITE);
        header2.setBackgroundColor(Color.DKGRAY);
        header2.setTextSize(20);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header2.setPadding(15, 25, 15, 25);
        header2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header2);

        TextView header3 = new TextView(this);
        header3.setText("Price/Day");
        header3.setTextColor(Color.WHITE);
        header3.setBackgroundColor(Color.DKGRAY);
        header3.setTextSize(20);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header3.setPadding(15, 25, 15, 25);
        header3.setTypeface(Typeface.SERIF, Typeface.BOLD);
        tableRow.addView(header3);


        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

    }


    private void addData(){
        String str = bf.toString();

        String[] data_row = str.split("\n");
        for (int i = 0; i < data_row.length; i++)
        {
            final String[] data = data_row[i].split(";");

            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));


            reg_no_tv = new TextView(this);
            reg_no_tv.setText(data[0]);
            reg_no_tv.setTextColor(Color.BLACK);
            reg_no_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            reg_no_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            reg_no_tv.setTextSize(15);
            reg_no_tv.setBackgroundColor(Color.LTGRAY);
            reg_no_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            reg_no_tv.setPadding(15, 20, 15, 20);
            tableRow.addView(reg_no_tv);

            model_tv = new TextView(this);
            model_tv.setText(data[1]);
            model_tv.setTextColor(Color.BLACK);
            model_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            model_tv.setTextSize(15);
            model_tv.setBackgroundColor(Color.LTGRAY);
            model_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            model_tv.setPadding(15, 20, 15, 20);
            model_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(model_tv);

            price_tv = new TextView(this);
            price_tv.setText("Rs. "+data[4]);
            price_tv.setTextColor(Color.BLACK);
            price_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            price_tv.setBackgroundColor(Color.LTGRAY);
            price_tv.setTextSize(15);
            price_tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            price_tv.setPadding(15, 20, 15, 20);
            price_tv.setTypeface(Typeface.SERIF, Typeface.NORMAL);
            tableRow.addView(price_tv);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Reg. No.: "+data[0] + "\n\n");
                    buffer.append("Model: "+data[1] + "\n\n");
                    buffer.append("Color: "+data[2] + "\n\n");
                    buffer.append("Location: "+data[3] + "\n\n");
                    buffer.append("Price/Day: Rs. "+data[4] + "\n\n");
                    buffer.append("Rating: "+data[5] + "\n\n");

                    showMessage("Details", buffer.toString(), data[0]);
                }
            });


            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

        }

    }

    private void showMessage(String title, String message, final String reg_no) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                prefs.edit().putString("reg_no", reg_no).apply();

                Intent i = new Intent(ManageCycle.this, UpdateCycle.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ManageCycle.this); //Home is name of the activity
                builder.setMessage("Are you sure you want to delete cycle?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        String date = sdf.format(c.getTime());

                        Cursor res = db.getData_Transaction_delete(reg_no, date);
                        if(res.getCount() > 0){
                            Toast.makeText(ManageCycle.this, "Cannot Delete. Cycle is in use.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Integer deleteRows = db.deleteData_Cycle(reg_no, username);
                            if(deleteRows > 0) {
                                Toast.makeText(ManageCycle.this, "Cycle Deleted", Toast.LENGTH_SHORT).show();
                                tableLayout.removeAllViews();
                                getData();
                                if(hasData) {
                                    addHeaders();
                                    addData();
                                }
                                else{
                                    TextView noData = new TextView(ManageCycle.this);
                                    noData.setTextSize(20);
                                    noData.setText("No Data");
                                    noData.setTextColor(Color.BLACK);
                                    noData.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                    noData.setPadding(15, 25, 15, 25);
                                    noData.setTypeface(Typeface.SERIF, Typeface.BOLD);

                                }
                            }
                            else
                                Toast.makeText(ManageCycle.this, "Cycle Not Deleted", Toast.LENGTH_SHORT).show();
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
