package com.example.watchstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference store = FirebaseDatabase.getInstance().getReference().child("users").child(uuid);
        store.get().addOnCompleteListener(snapshot -> {
           snapshot.getResult().getChildren().forEach(obj -> {
               switch (Objects.requireNonNull(obj.getKey()).toLowerCase()){
                   case "email":
                       ((TextView) findViewById(R.id.email)).setText(obj.getValue().toString());
                       break;
                   case "firstname":
                       ((TextView) findViewById(R.id.firstName)).setText(obj.getValue().toString());
                       break;
                   case "lastname":
                       ((TextView) findViewById(R.id.lastName)).setText(obj.getValue().toString());
                       break;
                   case "phone":
                       ((TextView) findViewById(R.id.phone)).setText(obj.getValue().toString());
                       break;
                   case "address":
                       ((TextView) findViewById(R.id.address)).setText(obj.getValue().toString());
                       break;
                   case "country":
                       ((TextView) findViewById(R.id.country)).setText(obj.getValue().toString());
                       break;
                   default:
                       //do Nothing
               }
           });
        });

        findViewById(R.id.updateProfile).setOnClickListener(view -> {
            String fName = ((TextView) findViewById(R.id.firstName)).getText().toString().trim();
            String lName = ((TextView) findViewById(R.id.lastName)).getText().toString().trim();
            String email = ((TextView) findViewById(R.id.email)).getText().toString().trim();
            String phone = ((TextView) findViewById(R.id.phone)).getText().toString();
            String address = ((TextView) findViewById(R.id.address)).getText().toString();
            String country = ((TextView) findViewById(R.id.country)).getText().toString();


            if (fName == null || fName.equals("")) {
                Toast.makeText(this, "First Name is required", Toast.LENGTH_SHORT).show();
            } else if (lName == null || lName.equals("")) {
                Toast.makeText(this, "Last Name is required", Toast.LENGTH_SHORT).show();
            } else if (phone == null || phone.equals("")) {
                Toast.makeText(this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            } else if (email == null || email.equals("")) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            } else if (!phone.matches("^\\d{10}$")) {
                Toast.makeText(this, "Enter a valid 10 digit phone number only", Toast.LENGTH_SHORT).show();
            } else if (address == null || address.equals("")) {
                Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            } else if (country == null || country.equals("")) {
                Toast.makeText(this, "Country is required", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Updated profile details successfully", Toast.LENGTH_SHORT).show();
                store.child("firstName").setValue(fName);
                store.child("lastName").setValue(lName);
                store.child("email").setValue(email);
                store.child("country").setValue(country);
                store.child("phone").setValue(phone);
                store.child("address").setValue(address);
                Intent myIntent = new Intent(this, StocksActivity.class);
                startActivity(myIntent);
            }
        });
        navigateTo();
    }

    private void navigateTo() {
        findViewById(R.id.home).setOnClickListener(v -> {
            if(!v.getParent().getClass().toString().equals(StocksActivity.class.toString())){
                startActivity(new Intent(this, StocksActivity.class));
            }
        });

        findViewById(R.id.cart).setOnClickListener(v -> {
            if(!v.getParent().getClass().toString().equals(CheckoutActivity.class.toString())){
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });


    }
}