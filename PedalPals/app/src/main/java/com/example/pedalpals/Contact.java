package com.example.pedalpals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Contact extends AppCompatActivity {

    Database db;

    EditText name, email, subject, body;
    TextView incorrect;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        db = new Database(this);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email_id);
        subject = findViewById(R.id.subject);
        body = findViewById(R.id.body);
        incorrect = findViewById(R.id.incorrect_details);
        submit = findViewById(R.id.submit_button);
        AddData();
    }

    private void AddData(){
        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(name.getText().toString().trim().equals("") || body.getText().toString().trim().equals("") ||
                                email.getText().toString().trim().equals("") || subject.getText().toString().trim().equals("")){
                            incorrect.setText("Fields cannot be left blank.");
                            return;
                        }

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar c = Calendar.getInstance();
                            String date = sdf.format(c.getTime());


                            boolean isInserted = db.insertData_Contact(name.getText().toString().trim(),
                                    email.getText().toString().trim(),
                                    subject.getText().toString().trim(),
                                    body.getText().toString().trim(),
                                    date);

                            if(isInserted) {
                                incorrect.setText("");
                                Toast.makeText(Contact.this, "Query Submitted", Toast.LENGTH_SHORT).show();
                                name.getText().clear();
                                subject.getText().clear();
                                email.getText().clear();
                                body.getText().clear();
                            }
                            else
                                Toast.makeText(Contact.this, "Error Occurred", Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }
}
