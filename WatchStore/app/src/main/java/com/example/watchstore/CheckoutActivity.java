package com.example.watchstore;

import static java.lang.Long.parseLong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Adapater.CheckoutAdapter;
import com.Model.Store;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {
    CheckoutAdapter adapter;
    RecyclerView recyclerView;
    private final List<String> item = new ArrayList<>();
    static HashMap<String, Long> priceMap = new HashMap<>();
    static HashMap<String, Long> quantityMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseRecyclerOptions<Store> options =
                new FirebaseRecyclerOptions.Builder<Store>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("store"), Store.class)
                        .build();

        adapter = new CheckoutAdapter(options);

        String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders").child(uuid);
        orders.get().addOnCompleteListener(snapshot -> {
            Iterator<DataSnapshot> iterator = snapshot.getResult().getChildren().iterator();
            while (iterator.hasNext()) {
                DataSnapshot obj = iterator.next();
                String key = obj.getKey();
                if (key != null && !item.contains(key)) {
                    long quantity = parseLong(String.valueOf(obj.getValue()));
                    if (quantity > 0L) {
                        item.add(key);
                        quantityMap.put(key, quantity);
                        FirebaseDatabase.getInstance().getReference().child("store").child(key).get().addOnCompleteListener(sc -> {
                            Iterator<DataSnapshot> itr = sc.getResult().getChildren().iterator();
                            while (itr.hasNext()) {
                                DataSnapshot storeKeys = itr.next();
                                if (storeKeys.getKey().equals("price")) {
                                    priceMap.put(key, Long.parseLong(storeKeys.getValue().toString()));
                                }
                                if (!iterator.hasNext() && !itr.hasNext() && key.equals(item.get(item.size() - 1))) {
                                    double orderTotalVal = item.stream().mapToLong((product) -> quantityMap.get(product) * priceMap.get(product)).sum();
                                    TextView orderTotal = findViewById(R.id.orderTotal);
                                    orderTotal.setText(new StringBuilder().append(String.format("%.2f", orderTotalVal)).append("$").toString());

                                    TextView tax = findViewById(R.id.totalTax);
                                    double taxVal = orderTotalVal * 0.13;
                                    tax.setText(new StringBuilder().append(String.format("%.2f", taxVal)).append("$").toString());

                                    TextView delivery = findViewById(R.id.totalDelivery);
                                    double deliveryVal = orderTotalVal * 0.07;
                                    delivery.setText(new StringBuilder().append(String.format("%.2f", deliveryVal)).append("$").toString());

                                    TextView paymentTotal = findViewById(R.id.paymentTotal);
                                    double paymentTotalVal = orderTotalVal + taxVal + deliveryVal;
                                    paymentTotal.setText(new StringBuilder().append(String.format("%.2f", paymentTotalVal)).append("$").toString());

                                    TextView total = findViewById(R.id.totalValue);
                                    total.setText(new StringBuilder().append(String.format("%.2f", paymentTotalVal)).append("$").toString());
                                }
                            }


                        });
                    }
                }

            }


            if (item.isEmpty()) {
                setContentView(R.layout.activity_noitem);
            } else {
                setContentView(R.layout.activity_checkout);
                recyclerView = findViewById(R.id.rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setItemAnimator(null);
                recyclerView.setAdapter(adapter);

                ((Button) findViewById(R.id.proceedPayment)).setOnClickListener(view -> {
                    String fName = ((TextView) findViewById(R.id.firstName)).getText().toString().trim();
                    String lName = ((TextView) findViewById(R.id.lastName)).getText().toString().trim();
                    String mail = ((TextView) findViewById(R.id.email)).getText().toString().trim();
                    String contact = ((TextView) findViewById(R.id.phone)).getText().toString();
                    String address = ((TextView) findViewById(R.id.address)).getText().toString();
                    String country = ((TextView) findViewById(R.id.country)).getText().toString();


                    if (fName == null || fName.equals("")) {
                        Toast.makeText(this, "First Name is required", Toast.LENGTH_SHORT).show();
                    } else if (lName == null || lName.equals("")) {
                        Toast.makeText(this, "Last Name is required", Toast.LENGTH_SHORT).show();
                    } else if (contact == null || contact.equals("")) {
                        Toast.makeText(this, "Phone Number is required", Toast.LENGTH_SHORT).show();
                    } else if (mail == null || mail.equals("")) {
                        Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    } else if (!contact.matches("^\\d{10}$")) {
                        Toast.makeText(this, "Enter a valid 10 digit phone number only", Toast.LENGTH_SHORT).show();
                    } else if (address == null || address.equals("")) {
                        Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
                    } else if (country == null || country.equals("")) {
                        Toast.makeText(this, "Country is required", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Order Successful", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(this, StocksActivity.class);
                        startActivity(myIntent);
                    }
                });
            }
            navigateTo();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void navigateTo() {
        findViewById(R.id.home).setOnClickListener(v -> {
            if (!v.getParent().getClass().toString().equals(StocksActivity.class.toString())) {
                startActivity(new Intent(this, StocksActivity.class));
            }
        });
        findViewById(R.id.profile).setOnClickListener(v -> {
            if (!v.getParent().getClass().toString().equals(ProfileActivity.class.toString())) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });
    }
}