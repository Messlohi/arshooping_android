package com.ensaf.arshopping.settings_activities.collections.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensaf.arshopping.R;
import com.ensaf.arshopping.settings_activities.collections.model.Collection;
import com.ensaf.arshopping.settings_activities.collections.ShowDetailCollectionActivity;

import java.text.DateFormat;
import java.util.List;

public class CollectionStorageAdapter extends RecyclerView.Adapter<CollectionStorageAdapter.ProductViewHolder> {

    Context context;
    List<Collection> collections;

    public CollectionStorageAdapter(Context context, List<Collection> collections) {
        this.context = context;
        this.collections = collections;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_coll_row, parent, false);
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Collection collection = collections.get(position);

        holder.title.setText(collection.getName());
        holder.date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(collection.getDate()));
        holder.nbCollTV.setText(collection.getNbProduit()+" Produits");
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShowDetailCollectionActivity.class);
            intent.putExtra("keyColl",collection.getKey());
            context.startActivity(intent);

      });

    }

    @Override
    public int getItemCount() {
        return collections.size();
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView title,date,nbCollTV;
        View itemView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.tv_title_collection);
            date = itemView.findViewById(R.id.tv_date);
            nbCollTV = itemView.findViewById(R.id.tv_nbCollection);


        }
    }

}
