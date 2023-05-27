package com.Adapater;

import static java.lang.Long.parseLong;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.Store;
import com.bumptech.glide.Glide;
import com.example.watchstore.DetailsActivity;
import com.example.watchstore.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class StoreAdapter extends FirebaseRecyclerAdapter<Store, StoreAdapter.MyViewHolder> {


    public StoreAdapter(@NonNull FirebaseRecyclerOptions<Store> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Store model) {
        holder.name.setText(model.getName());
        holder.price.setText(new StringBuilder().append(model.getPrice()).append("$").toString());
        Glide.with(holder.imageView.getContext()).load(model.getImg_url()).into(holder.imageView);


        String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String prodId = getRef(holder.getAbsoluteAdapterPosition()).getKey();
        DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders").child(uuid);
        orders.get().addOnCompleteListener(snapshot -> snapshot.getResult().getChildren().forEach(obj -> {
            if(obj.exists()){
                Object val = obj.getValue();
                if(val != null && Objects.equals(obj.getKey(), prodId)){
                    holder.quantity.setText(val.toString());
                }
            }
        }));

        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            String id = getRef(holder.getBindingAdapterPosition()).getKey();
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("productId",id);
            context.startActivity(intent);
        });

        holder.increment.setOnClickListener(v -> {
            long quantity = parseLong(holder.quantity.getText().toString()) + 1;
            orders.child(prodId).setValue(quantity);
            holder.quantity.setText(String.valueOf(quantity));
        });

        holder.decrement.setOnClickListener(v -> {
            long quantity = parseLong(holder.quantity.getText().toString()) - 1;
            quantity = quantity > 0 ? quantity : 0;
            orders.child(prodId).setValue(quantity);
            holder.quantity.setText(String.valueOf(quantity));
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        TextView price;
        TextView quantity;
        Button increment;
        Button decrement;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById((R.id.quantity));
            increment = itemView.findViewById((R.id.increment_btn));
            decrement = itemView.findViewById((R.id.decrement_btn));
        }
    }
}
