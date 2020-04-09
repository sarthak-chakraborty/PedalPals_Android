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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ManageLocationsAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    Database db;

    TextView nav_head_name, nav_head_email;

    TableLayout tableLayout;
    TableRow tableRow;
    boolean hasData = false;
    StringBuffer bf;
    String username;
    SharedPreferences prefs;
    EditText loc_name;
    Button add_loc_button;
    TextView name_tv, nodata,error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_locations_admin);

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
        add_loc_button = findViewById(R.id.add_loc_button);
        loc_name = findViewById(R.id.loc_name);
        nodata = findViewById(R.id.nodata);
        error = findViewById(R.id.error);
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
        add_loc();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_menu:
                Intent i = new Intent(ManageLocationsAdmin.this, AdminMenu.class);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                Intent i1 = new Intent(ManageLocationsAdmin.this, AdminProfile.class);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ManageLocationsAdmin.this);
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("userlogin", false);
                edit.apply();

                Intent i = new Intent(ManageLocationsAdmin.this, MainActivity.class);
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


    private void add_loc() {
        add_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = db.getLocation_name(loc_name.getText().toString().trim());

                if (res.getCount()==0) {
                    boolean inserted = db.insertData_Location(loc_name.getText().toString().trim());
                    if(inserted){
                        Toast.makeText(ManageLocationsAdmin.this, "Location Inserted", Toast.LENGTH_LONG).show();
                        loc_name.getText().clear();
                        error.setText("");
                        nodata.setText("");
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

                    else {
                        Toast.makeText(ManageLocationsAdmin.this, "Cannot Insert Location", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String s = "Location Already Available";
                    error.setText(s);
                    loc_name.getText().clear();
                }
            }
        });
    }

    public void getData() {
        Cursor result = db.getAllData_Location();
        if (result.getCount() == 0) {
            hasData = false;
        }
        else {

            bf = new StringBuffer();
            while (result.moveToNext()) {
                bf.append(result.getString(0) + "\n");
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
        header4.setText("Location Name");
        header4.setTextColor(Color.WHITE);
        header4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark,null));
        header4.setTextSize(15);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
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


            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Name: "+data[0] + "\n\n");
                    showMessage("Details", buffer.toString(),data[0]);
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

    private void showMessage(String title, String message, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ManageLocationsAdmin.this); //Home is name of the activity
                builder.setMessage("Are you sure you want to delete location?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Integer deleteRows = db.deleteData_Location(name);
                        if(deleteRows > 0) {
                            Toast.makeText(ManageLocationsAdmin.this, "Location Deleted", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ManageLocationsAdmin.this, "Location Not Deleted", Toast.LENGTH_SHORT).show();

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

