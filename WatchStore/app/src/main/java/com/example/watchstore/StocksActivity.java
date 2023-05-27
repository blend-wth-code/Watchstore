package com.example.watchstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.Adapater.StoreAdapter;
import com.Model.Store;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StocksActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
        navigateTo();

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(null);
        FirebaseRecyclerOptions<Store> options =
                new FirebaseRecyclerOptions.Builder<Store>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("store"), Store.class)
                        .build();

        storeAdapter = new StoreAdapter(options);
        recyclerView.setAdapter(storeAdapter);

    }

    @Override
    protected void onStart(){
        super.onStart();
        storeAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        storeAdapter.stopListening();
    }

    private void navigateTo() {
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