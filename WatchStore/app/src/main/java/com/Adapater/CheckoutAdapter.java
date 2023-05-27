package com.Adapater;

import static java.lang.Long.parseLong;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.Store;
import com.bumptech.glide.Glide;
import com.example.watchstore.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckoutAdapter extends FirebaseRecyclerAdapter<Store, CheckoutAdapter.MyViewHolder> {

    private final List<String> item = new ArrayList<>();
    String productId;
    String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public CheckoutAdapter(@NonNull FirebaseRecyclerOptions<Store> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Store model) {
        DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders").child(uuid);
        DatabaseReference store = FirebaseDatabase.getInstance().getReference("store");
        orders.get().addOnCompleteListener(snapshot -> {
            Iterator<DataSnapshot> iterator = snapshot.getResult().getChildren().iterator();
            while(iterator.hasNext()){
                DataSnapshot obj = iterator.next();
                String key = obj.getKey();
                if(key != null && !item.contains(key)){
                    long quantity = parseLong(String.valueOf(obj.getValue()));
                    if(quantity > 0L){
                        item.add(key);
                    }
                }
                if(!iterator.hasNext()){
                    if(item.isEmpty() || position >= item.size() + 1 || position-1 < 0){
                        holder.cv.setVisibility(View.GONE);
                        holder.cv.setMinimumHeight(0);
                        holder.cv.setMinimumWidth(0);

                    }
                    else{
                        productId = item.get(position-1);
                        AtomicReference<String> quantityVal = new AtomicReference<>("0");
                        orders.child(productId).get().addOnCompleteListener((os -> {
                            quantityVal.set(os.getResult().getValue().toString());
                            holder.quantity.setText(quantityVal.get());
                        }));

                        store.child(productId).get().addOnCompleteListener(sc -> {
                            sc.getResult().getChildren().forEach(storeKeys -> {
                                switch (Objects.requireNonNull(storeKeys.getKey())){
                                    case "img_url":
                                        Glide.with(holder.productIcon.getContext()).load(storeKeys.getValue()).into(holder.productIcon);
                                        break;
                                    case "name":
                                        holder.title.setText(Objects.requireNonNull(storeKeys.getValue()).toString());
                                        break;
                                    case "price":
                                        holder.price.setText(new StringBuilder().append(Objects.requireNonNull(storeKeys.getValue())).append("$").toString());
                                        double total = Long.parseLong(quantityVal.get()) * Double.parseDouble(storeKeys.getValue().toString());
                                        holder.quantity.setText(new StringBuilder().append(quantityVal.get()).append(" X ").append(storeKeys.getValue().toString()).append(" = ").append(String.format("%.2f", total)).append("$").toString());
                                        break;
                                    default:
                                        // do Nothing
                                }
                            });
                        });


                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout,parent,false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView price;
        TextView quantity;
        CardView cv;
        CircleImageView productIcon;

        public MyViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            cv = view.findViewById(R.id.item);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            productIcon = view.findViewById(R.id.image);
        }
    }
}
