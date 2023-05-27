package com.example.watchstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseAuth oAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        Button register = findViewById(R.id.register);
        TextView login = findViewById(R.id.login);
        oAuth = FirebaseAuth.getInstance();


        register.setOnClickListener(view -> {
            String email = this.email.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String confirmPwd = confirmPassword.getText().toString();
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();

            if(fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pwd.isEmpty()){
                Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show();
            }
            else if(pwd.length() < 8){
                Toast.makeText(this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Invalid Email ID", Toast.LENGTH_SHORT).show();
            }
            else if (!pwd.equals(confirmPwd)){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }

            else{
                oAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()){
                            User user = new User(fName, lName, email);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(e ->  {
                                    Toast.makeText(this, "User registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                });
                        }
                        else{
                            Toast.makeText(this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        login.setOnClickListener(view ->  startActivity(new Intent(this, LoginActivity.class)));
    }

    private void register() {
    }
}