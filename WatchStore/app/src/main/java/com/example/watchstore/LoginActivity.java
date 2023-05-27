package com.example.watchstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        FirebaseAuth oAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(view -> {
            String mail = email.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            if(mail.isEmpty() || pwd.isEmpty()){
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
            else{
                oAuth.signInWithEmailAndPassword(mail, pwd).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            startActivity(new Intent(this, StocksActivity.class));
                        }
                        else{
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                });
            }
        });

        findViewById(R.id.register)
                .setOnClickListener(view -> startActivity(new Intent(this, RegistrationActivity.class)));
    }
}