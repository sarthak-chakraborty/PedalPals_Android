package com.example.pedalpals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {
    Database db;

    EditText username, password;
    Button login, delete;
    TextView txt_incorrect_user;
    SharedPreferences prefs;
    Boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        db = new Database(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login_button);
        delete = findViewById(R.id.delete_button);
        txt_incorrect_user = findViewById(R.id.text_incorrect_user);

        prefs = this.getSharedPreferences("PedalPals", 0);
        isLogin = prefs.getBoolean("userlogin", false);

        db.insertData_Admin("Sarthak", "Chakraborty", "sarthak.chakraborty@gmail.com", "Sarthak", "Sarthak");

        deleteData();
        logging();
    }

    private void logging() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean res = db.login_Admin(username.getText().toString(), password.getText().toString());

                if(res){
                    Toast.makeText(AdminLogin.this, "Logged In", Toast.LENGTH_LONG).show();
                    prefs.edit().putString("username", username.getText().toString()).apply();

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("userlogin", true);
                    edit.apply();

                    Intent i = new Intent(AdminLogin.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    String s = "Incorrect Username or Password";
                    txt_incorrect_user.setText(s);
                    username.getText().clear();
                    password.getText().clear();
                }
            }
        });
    }


    private void deleteData(){
        delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.deleteData_Admin();
                    }
                }
        );
    }

}
