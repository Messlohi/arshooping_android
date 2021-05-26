package com.ensaf.arshopping.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.Productdetails;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.helpers.GenralArViewer;
import com.ensaf.arshopping.model.Products;
import com.google.ar.sceneform.SceneView;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ensaf.arshopping.R.drawable.bg_product_detail_selected;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    public List<Products> productsList;
    public static HashMap<String,View> selectedProduct = new HashMap<>();
    public static List<Products> allProducts = new ArrayList<>();

    public ProductAdapter() {
    }

    public ProductAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row_item2, parent, false);
        return new ProductViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {

        Products product = productsList.get(position);

        ImageView.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Productdetails.class);
                if(v.getId() != holder.itemView.findViewById(R.id.iv_readMore).getId()){
                    i.putExtra("3dProdUrl", product.getUrl3dShape());
                }else {
                    i.putExtra("3dImageUrl", product.getUrlImage());
                }
                i.putExtra("id", product.getId());
                i.putExtra("catego", product.getCatego());
                i.putExtra("title", product.getTitle());
                i.putExtra("quantite", product.getQuantite());
                i.putExtra("desc", product.getDesc());
                i.putExtra("price", product.getPrice());
         /*   Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(holder.prodImage, "image");
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);*/
                context.startActivity(i/*,activityOptions.toBundle()*/);
            }
        };

        holder.itemView.setOnClickListener(listener);
        holder.btn3d.setOnClickListener(listener);
        holder.readMore.setOnClickListener(listener);

        holder.itemView.setTag(product.getId());

        holder.btnSimilation.setOnClickListener(view -> {
            if (MainActivity.beginSimaltion) {
                if(MainActivity.ll_voirColl_container.getVisibility() == View.GONE){
                    MainActivity.ll_voirColl_container.setVisibility(View.VISIBLE);
                }
                if (product.getSelected() == false) {
                    product.setSelected(true);
                    allProducts.add(product);
                    selectedProduct.put(product.getId(),holder.itemView);
                    holder.btnSimilation.setImageResource(R.drawable.push_pin_in);
                    holder.itemView.setBackgroundResource(R.drawable.bg_product_detail_selected);

                } else {
                    product.setSelected(false);
                    allProducts = allProducts.stream()
                            .filter(p -> !p.getId().equals(product.getId()))
                            .collect(Collectors.toList());
                    selectedProduct.remove(product.getId());
                    holder.btnSimilation.setImageResource(R.drawable.push_pin_out);
                    holder.itemView.setBackgroundResource(R.drawable.bg_product_detail);
                    if(allProducts.size() ==0) MainActivity.ll_voirColl_container.setVisibility(View.GONE);
                }
            }


        });

        if (product.getSelected() == false) {
            holder.btnSimilation.setImageResource(R.drawable.push_pin_out);
            holder.itemView.setBackgroundResource(R.drawable.bg_product_detail);

        } else {
            holder.btnSimilation.setImageResource(R.drawable.push_pin_in);
            holder.itemView.setBackgroundResource(R.drawable.bg_product_detail_selected);
            selectedProduct.remove(product.getId());
            selectedProduct.put(product.getId(),holder.itemView);
        }


        holder.btnAr.setOnClickListener(view -> {
            Intent i = new Intent(context, GenralArViewer.class);
            i.putExtra("3dProdUrl", product.getUrl3dShape());
            context.startActivity(i);
        });


        try {
            Glide.with(context).load(product.getUrlImage()).into(holder.prodImage);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView prodImage, btnAr, btn3d, btnSimilation, readMore;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prod_image);
            btnAr = itemView.findViewById(R.id.view_ar_btn);
            btn3d = itemView.findViewById(R.id.see_in_3d_btn);
            btnSimilation = itemView.findViewById(R.id.iv_chose_prodcut);
            readMore = itemView.findViewById(R.id.iv_readMore);

        }
    }


}