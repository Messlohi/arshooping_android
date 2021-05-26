package com.ensaf.arshopping.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.model.Products;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductBasketAdapter extends RecyclerView.Adapter<ProductBasketAdapter.ProductViewHolder> {

    Context context;
    public  static  List<Products> listeProducts;
    public  static List<String> listeDesQuantite;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference basketsRef;

    public ProductBasketAdapter(Context context, List<Products> listeProducts) {
        this.context = context;
        this.listeProducts = listeProducts;
    }


    public ProductBasketAdapter(Context context, List<Products> listeProducts, List<String> listeDesQuantite) {
        this.context = context;
        this.listeProducts = listeProducts;
        this.listeDesQuantite = listeDesQuantite;
        basketsRef = database.getReference("all_baskets").child(MainActivity.getUserId());

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_basket_row, parent, false);
        // lets create a recyclerview row item layout file
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Products product = listeProducts.get(position);

        String id = product.getId();


        holder.delete.setOnClickListener(view-> {
                basketsRef.child(product.getId()).removeValue();
                listeProducts.remove(product);
                listeDesQuantite.remove(position);
                this.notifyDataSetChanged();
            });


            holder.quantite.setText(listeDesQuantite.get(position));
            try {
                Map<String,Object> update = new HashMap<>();
                AtomicInteger quantite = new AtomicInteger(Integer.parseInt(listeDesQuantite.get(position)));
                holder.plus.setOnClickListener(view -> {

                    int temp = quantite.getAndIncrement();
                    holder.quantite.setText(temp+"");
                    update.put("/"+id+"/quantite/",temp);
                    basketsRef.updateChildren(update);
                });
                holder.minus.setOnClickListener(view -> {
                    if(quantite.get() -1>=0){
                        int temp = quantite.getAndDecrement();
                        holder.quantite.setText(""+(temp+""));
                        update.put("/"+id+"/quantite/",temp);
                        basketsRef.updateChildren(update);
                    }
                });

            }catch (Exception e){}

            holder.price.setText(product.getPrice()+" Dh");
            holder.title.setText(product.getTitle());
            try {
                if(Integer.parseInt(product.getQuantite())<200) holder.stock.setText(product.getQuantite() +" en Stock");
                holder.stock.setVisibility(View.GONE);
            }catch (Exception e){ holder.stock.setVisibility(View.GONE);}





        try {
            Glide.with(context).load(product.getUrlImage()).into(holder.productImg);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return listeProducts.size();
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView price,title,quantite,stock;
        ImageView minus,plus,productImg,delete;
        View itemView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            price = itemView.findViewById(R.id.tv_price);
            title = itemView.findViewById(R.id.tv_title);
            quantite = itemView.findViewById(R.id.tv_quantite);
            stock = itemView.findViewById(R.id.tv_inStock);
            minus = itemView.findViewById(R.id.tv_minus);
            plus = itemView.findViewById(R.id.iv_add);
            productImg = itemView.findViewById(R.id.iv_product);
            delete =  itemView.findViewById(R.id.ic_delete);


        }
    }

}
