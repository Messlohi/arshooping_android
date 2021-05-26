package com.ensaf.arshopping.settings_activities.collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.ensaf.arshopping.R;
import com.ensaf.arshopping.settings_activities.collections.adapter.CollectionStorageAdapter;
import com.ensaf.arshopping.settings_activities.collections.model.Collection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectionSavedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CollectionStorageAdapter collectionStorageAdapter ;


    List<Collection> collectionsList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference collectionsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_saved);

        if(user!=null) currentUser = user.getUid();

        collectionsRef = database.getReference("all_collections").child(currentUser);


        recyclerView = findViewById(R.id.rv_collections);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Context context =this;
        collectionsRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap<String,String> model = new HashMap<>();
                for(DataSnapshot ds :snapshot.getChildren()){
                    Collection collection = new Collection();
                    collection.setKey(ds.getKey());
                    collection.setName(ds.child("name").getValue(String.class));
                    collection.setDate(ds.child("date").getValue(String.class));
                    //"-3" because we have tree attribute which are not the product key
                    collection.setNbProduit(ds.getChildrenCount()-3);
                    collectionsList.add(collection);
                }
                collectionStorageAdapter = new CollectionStorageAdapter(context,collectionsList);
                recyclerView.setAdapter(collectionStorageAdapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });





    }
}