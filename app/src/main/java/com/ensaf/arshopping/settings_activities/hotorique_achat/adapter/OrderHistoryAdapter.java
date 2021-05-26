package com.ensaf.arshopping.settings_activities.hotorique_achat.adapter;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.model.ProductBasket;
import com.ensaf.arshopping.model.ProductCategory;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.settings_activities.hotorique_achat.model.OrderHistory;
import com.ensaf.arshopping.settings_activities.hotorique_achat.model.ProductMinify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ProductViewHolder> {

    Context context;
    List<OrderHistory> listeOrders;
    List<Products> products = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference productsRef;

    OrderHistoryDetailAdapter orderHistoryDetailAdapter;


    public OrderHistoryAdapter(Context context, List<OrderHistory> listeOrders) {
        this.context = context;
        this.listeOrders = listeOrders;
        productsRef = database.getReference("all_products");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_history_order_row, parent, false);
        // lets create a recyclerview row item layout file
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {




        OrderHistory orderHistory = listeOrders.get(position);
        List<ProductMinify> productsList = orderHistory.getProducts();
        int posRand = getRandom(0,productsList.size()-1);
        ProductMinify randomProd = productsList.get(posRand);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
        holder.recyclerView.setLayoutManager(layoutManager);
        orderHistoryDetailAdapter = new OrderHistoryDetailAdapter(context, productsList);
        holder.recyclerView.setAdapter(orderHistoryDetailAdapter);


        holder.productsTv.setText("et "+ productsList.size() +" autre produit");
        holder.price.setText(orderHistory.getAmount() +" Dh");
        if(productsList.size()>2){
            holder.productsTv.setText("et "+ productsList.size() +" autre produits");
            holder.price.setText("Totale de "+orderHistory.getAmount() +" Dh");
        }
        holder.title.setText(randomProd.getTitle());
        if(productsList.size()>2)holder.title.setText(randomProd.getTitle()+",...");
        try {
            Glide.with(context).load(randomProd.getUrlImage()).into(holder.productImg);
        } catch (Exception e) {
            e.printStackTrace();
        }


/*
        productsRef.child(productsList.get(pos).getCatego()).child(productsList.get(pos).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Products product = snapshot.getValue(Products.class);
                holder.title.setText(product.getTitle());
                if(productsList.size()>2)holder.title.setText(product.getTitle()+",...");

                try {
                    Glide.with(context).load(product.getUrlImage()).into(holder.productImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        int i=0;
        products.clear();
        for (ProductMinify prodm: productsList
        ) {
            i++;
            productsRef.child(prodm.getCatego()).child(prodm.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Products product = snapshot.getValue(Products.class);
                        products.add(product);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
            if(i == productsList.size()){
                orderHistoryDetailAdapter.notifyDataSetChanged();
                holder.btnSeeMore.setEnabled(true);
            }
        }
*/


        holder.btnSeeMore.setOnClickListener(view -> {

            // Initialize a new ChangeBounds transition instance
            ChangeBounds changeBounds = new ChangeBounds();

            // Set the transition start delay
            changeBounds.setStartDelay(50);

            // Set the transition interpolator
            changeBounds.setInterpolator(new BounceInterpolator());


            // Specify the transition duration
            changeBounds.setDuration(1000);

            // Begin the delayed transition
            TransitionManager.beginDelayedTransition(holder.ll_container_detail,changeBounds);

            // Toggle the button size
            toggleSearchAppear(holder.ll_deatil,holder.btnSeeMore);
        });



        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String string1 = orderHistory.getDate();
        try {
            Date result1 = df1.parse(string1);
            holder.dateTv.setText("Commandé le :"+result1.toLocaleString());

        } catch (ParseException e) {
            e.printStackTrace();
            holder.dateTv.setVisibility(View.GONE);
        }





    }

    private void toggleSearchAppear(LinearLayout v,Button thisButton) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if(params.height == 0){
            orderHistoryDetailAdapter.notifyDataSetChanged();
            params.height = 452;
        }else {
            params.height = 0;
        }
        v.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return listeOrders.size();
    }

    private  int getRandom(int min ,int max){
        Random random = new Random();
        return  random.nextInt(max + 1 - min) + min;
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,productsTv,dateTv;
        ImageView productImg;
        View itemView;
        Button btnCommanderOrder,btnSeeMore;
        //For product reccyler view
        RecyclerView recyclerView;
        LinearLayout ll_deatil,ll_container_detail;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;


            productImg = itemView.findViewById(R.id.iv_product);
            title = itemView.findViewById(R.id.tv_title_order);
            price = itemView.findViewById(R.id.tv_price_order);
            dateTv = itemView.findViewById(R.id.tv_date_order);
            productsTv = itemView.findViewById(R.id.tv_other_prodcuts);
            btnSeeMore = itemView.findViewById(R.id.bnt_see_more);

            recyclerView = itemView.findViewById(R.id.rv_orderh_products);
            btnCommanderOrder = itemView.findViewById(R.id.btn_comm_orderhits);
            ll_deatil = itemView.findViewById(R.id.ll_deatail_orderh);
            ll_container_detail = itemView.findViewById(R.id.ll_container_detail);

        }
    }

}
