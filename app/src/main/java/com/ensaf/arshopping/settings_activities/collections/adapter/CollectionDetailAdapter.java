package com.ensaf.arshopping.settings_activities.collections.adapter;

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
import com.ensaf.arshopping.settings_activities.collections.ShowDetailCollectionActivity;
import com.ensaf.arshopping.settings_activities.collections.model.Collection;

import java.util.List;

public class CollectionDetailAdapter extends RecyclerView.Adapter<CollectionDetailAdapter.ProductViewHolder> {

    Context context;
    List<Products> products;

    public CollectionDetailAdapter(Context context, List<Products> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_product_incollectoin_row, parent, false);
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = products.get(position);

        holder.priceTv.setText(product.getPrice()+" Dh");

        try{
            Glide.with(context).load(product.getUrlImage()).into(holder.producIv);
        }catch (Exception e){}

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, Productdetails.class);
            i.putExtra("3dImageUrl", product.getUrlImage());
            i.putExtra("id", product.getId());
            i.putExtra("title", product.getTitle());
            i.putExtra("quantite", product.getQuantite());
            i.putExtra("desc", product.getDesc());
            i.putExtra("price", product.getPrice());
            context.startActivity(i);

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
