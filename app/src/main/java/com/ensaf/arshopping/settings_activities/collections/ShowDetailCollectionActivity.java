package com.ensaf.arshopping.settings_activities.collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.ensaf.arshopping.R;
import com.ensaf.arshopping.adapter.ProductAdapter;
import com.ensaf.arshopping.helpers.GenralArViewer;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.settings_activities.collections.adapter.CollectionDetailAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowDetailCollectionActivity extends AppCompatActivity {

    String keyColl  = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference collection = database.getReference("all_collections");
    DatabaseReference productsref = database.getReference("all_products");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = "";

    RecyclerView recyclerViewProduct;
    CollectionDetailAdapter collectionDetailAdapter ;

    double prixTotal = 0;

    Button voirColl;
    TextView tv_price;

   public static List<Products> listProducts =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_coll_detail_activity);


        Context context = this;
        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            listProducts.clear();
            tv_price = findViewById(R.id.tv_price);
            voirColl = findViewById(R.id.btn_voirCollection_in_detail);

            if(user !=null) userId = user.getUid();

            recyclerViewProduct = findViewById(R.id.rv_coll_products);
            RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this,3);
            recyclerViewProduct.setLayoutManager(layoutManager);
            collectionDetailAdapter = new CollectionDetailAdapter(context,listProducts);
            recyclerViewProduct.setAdapter(collectionDetailAdapter);


            voirColl.setOnClickListener(view -> {
                Intent i = new Intent(context, GenralArViewer.class);
                i.putExtra("fromCollDetail", "true");
                context.startActivity(i);

            });


            keyColl = extras.getString("keyColl");
            collection.child(userId).child(keyColl).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                            if(ds.getKey().equals("name") || ds.getKey().equals("savePos") || ds.getKey().equals("date")) continue;
                            productsref.child(ds.getValue(String.class)).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot2) {
                                        Products product = snapshot2.getValue(Products.class);
                                        listProducts.add(product);
                                        try {
                                            if(!product.getPrice().isEmpty()) prixTotal += Double.parseDouble(product.getPrice());
                                        }catch (Exception e){prixTotal+=0;}

                                        if(snapshot.getChildrenCount()-3 == listProducts.size()){
                                            collectionDetailAdapter.notifyDataSetChanged();
                                            tv_price.setText("Prix Total : "+prixTotal +" Dh");
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                    }


                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }





    }

}