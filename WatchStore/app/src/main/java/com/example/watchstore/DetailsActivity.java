package com.example.watchstore;

import static java.lang.Long.parseLong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class DetailsActivity extends AppCompatActivity {

    String productId;
    ImageView image;
    TextView title;
    TextView desc;
    TextView price;
    Button increment;
    Button decrement;
    TextView quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        productId = getIntent().getExtras().get("productId").toString();
        DatabaseReference storeReference = FirebaseDatabase.getInstance().getReference().child("store").child(productId);
        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        desc =  findViewById(R.id.description);
        increment = findViewById(R.id.increment_btn);
        decrement = findViewById(R.id.decrement_btn);
        quantity = findViewById(R.id.quantity);
        navigateTo();

        storeReference.get().addOnCompleteListener(snapshot -> snapshot.getResult().getChildren().forEach(obj -> {
            switch (Objects.requireNonNull(obj.getKey())){
                case "img_url":
                    Glide.with(image.getContext()).load(obj.getValue()).into(image);
                    break;
                case "name":
                    title.setText(Objects.requireNonNull(obj.getValue()).toString());
                    break;
                case "price":
                    price.setText(new StringBuilder().append(Objects.requireNonNull(obj.getValue())).append("$").toString());
                    break;
                case "description":
                    desc.setText(Objects.requireNonNull(obj.getValue()).toString());
                    break;
                default:
                    // do Nothing
            }
        }));

        AtomicLong quantityValue = new AtomicLong();
        String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders").child(uuid);
        setQuantityViewFromDb(orders);

        increment.setOnClickListener(v -> {
            long quantityVal = getQuantityFromDb(orders, quantityValue) + 1;
            orders.child(productId).setValue(quantityVal);
            quantity.setText(String.valueOf(quantityVal));
        });
        decrement.setOnClickListener(v -> {
            long quantityVal = getQuantityFromDb(orders, quantityValue) - 1;
            quantityVal = quantityVal > 0 ? quantityVal : 0;
            orders.child(productId).setValue(quantityVal);
            quantity.setText(String.valueOf(quantityVal));

        });

    }
    private long getQuantityFromDb(DatabaseReference orders, AtomicLong quantity){
        orders.get().addOnCompleteListener(snapshot -> snapshot.getResult().getChildren().forEach(obj -> {
            if(obj.exists()){
                Object val = obj.getValue();
                if(val != null && Objects.equals(obj.getKey(), productId)){
                    quantity.set(parseLong(val.toString()));
                }
            }
        }));
        return quantity.get();
    }

    private void setQuantityViewFromDb(DatabaseReference orders){
        orders.get().addOnCompleteListener(snapshot -> snapshot.getResult().getChildren().forEach(obj -> {
            if(obj.exists()){
                Object val = obj.getValue();
                if(val != null && Objects.equals(obj.getKey(), productId)){
                    quantity.setText(val.toString());
                }
            }
        }));
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

        findViewById(R.id.profile).setOnClickListener(v -> {
            if(!v.getParent().getClass().toString().equals(ProfileActivity.class.toString())){
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });
    }
}