package com.ensaf.arshopping.settings_activities.hotorique_achat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.Productdetails;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.settings_activities.hotorique_achat.model.ProductMinify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderHistoryDetailAdapter extends RecyclerView.Adapter<OrderHistoryDetailAdapter.ProductViewHolder> {

    Context context;
    List<ProductMinify> products;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference productsRef;

    public OrderHistoryDetailAdapter(Context context, List<ProductMinify> products) {
        this.context = context;
        this.products = products;
        productsRef = database.getReference("all_products");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_product_orderhistory_row, parent, false);
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductMinify product = products.get(position);
        holder.priceTv.setText(product.getPrice()+" Dh");
        try{
            Glide.with(context).load(product.getUrlImage()).into(holder.producIv);
        }catch (Exception e){}

        holder.itemView.setOnClickListener(view -> {
            productsRef.child(product.getCatego()).child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Products products = snapshot.getValue(Products.class);
                        Intent i = new Intent(context, Productdetails.class);
                        i.putExtra("3dImageUrl", products.getUrlImage());
                        i.putExtra("id", products.getId());
                        i.putExtra("title", products.getTitle());
                        i.putExtra("quantite", products.getQuantite());
                        i.putExtra("desc", products.getDesc());
                        i.putExtra("price", products.getPrice());
                        context.startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView producIv;
        TextView priceTv;
        View itemView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            producIv = itemView.findViewById(R.id.ib_prodcutImg2);
            priceTv = itemView.findViewById(R.id.tv_price);


        }
    }

}
