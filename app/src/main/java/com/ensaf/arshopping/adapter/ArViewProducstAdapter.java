package com.ensaf.arshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.Productdetails;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.helpers.GenralArViewer;
import com.ensaf.arshopping.model.Products;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ArViewProducstAdapter extends RecyclerView.Adapter<ArViewProducstAdapter.ProductViewHolder> {

    Context context;
    List<Products> productsList;
    int selectedPosition = 0;
    public static  Products currentProduct = null;


    public ArViewProducstAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
      //  currentProduct3dUrl = productsList.get(0).getUrl3dShape();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ar_view_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
             Products product = productsList.get(position);

             holder.deleteImg.setOnClickListener(view -> {
                 product.setSelected(false);
                 ProductAdapter.allProducts = ProductAdapter.allProducts.stream()
                         .filter(p -> !p.getId().equals(product.getId()))
                         .collect(Collectors.toList());
                 ProductAdapter.selectedProduct.remove(product.getId());
                 productsList.remove(position);
                 selectedPosition = 0;
                 currentProduct = productsList.get(0);
                 MainActivity.changeCategory(MainActivity.categActuel,true);
                 this.notifyDataSetChanged();
             });
            if(ProductAdapter.allProducts.size()<2){
                holder.deleteImg.setVisibility(View.GONE);
            }
             if(position == 0) currentProduct = product;
             try {
                 Glide.with(context).load(product.getUrlImage()).into(holder.prodcutImg);
             }catch (Exception e){}

             holder.prodcutImg.setOnClickListener(view -> {
                 GenralArViewer.productDeselected(selectedPosition);
                 GenralArViewer.rulerDeslecetd();
                 currentProduct = product;
                 selectedPosition = position;
                 holder.itemView.setBackgroundResource(R.drawable.bg_product_arview_selected);

             });
                if(position == selectedPosition){
                    holder.itemView.setBackgroundResource(R.drawable.bg_product_arview_selected);
                }else {
                    holder.itemView.setBackgroundResource(R.drawable.bg_product_arview);
                }
    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView prodcutImg,deleteImg;
        View itemView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            prodcutImg = itemView.findViewById(R.id.ib_prodcutImg);
            deleteImg = itemView.findViewById(R.id.iv_remove_prodcut);
            this.itemView = itemView;

        }
    }



}