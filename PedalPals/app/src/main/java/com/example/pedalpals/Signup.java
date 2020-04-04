package com.example.pedalpals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends AppCompatActivity {

    Database db;

    EditText first_name, last_name, email, hall, room, username, password, re_password;
    Button signUp, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        db = new Database(this);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        hall = findViewById(R.id.hall);
        room = findViewById(R.id.room);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        signUp = findViewById(R.id.signup_button);
        login = findViewById(R.id.goto_login_button);

        AddData();
        gotoLogin();
    }

    private void AddData(){
        signUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(password.getText().toString().equals(re_password.getText().toString())){
                            boolean isInserted = db.insertData_User(first_name.getText().toString(),
                                    last_name.getText().toString(),
                                    email.getText().toString(),
                                    hall.getText().toString(),
                                    room.getText().toString(),
                                    username.getText().toString(),
                                    password.getText().toString());

                            if(isInserted) {
                                Toast.makeText(Signup.this, "User Registered", Toast.LENGTH_SHORT).show();
                                login.setVisibility(View.VISIBLE);
                            }
                            else
                                Toast.makeText(Signup.this, "Error Occurred", Toast.LENGTH_SHORT).show();

                            first_name.getText().clear();
                            last_name.getText().clear();
                            email.getText().clear();
                            hall.getText().clear();
                            room.getText().clear();
                            username.getText().clear();
                            password.getText().clear();
                            re_password.getText().clear();
                        }
                        else{
                            Toast.makeText(Signup.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                            password.getText().clear();
                            re_password.getText().clear();
                        }

                    }
                }
        );
    }

    private void gotoLogin() {
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Signup.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
        );
    }
}
