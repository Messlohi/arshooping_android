package com.ensaf.arshopping.settings_activities.hotorique_achat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.adapter.ProductBasketAdapter;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.settings_activities.hotorique_achat.adapter.OrderHistoryAdapter;
import com.ensaf.arshopping.settings_activities.hotorique_achat.model.OrderHistory;
import com.ensaf.arshopping.settings_activities.hotorique_achat.model.ProductMinify;
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

public class HistoriqueAchatActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference orderHistory;

    List<OrderHistory> orderHistories = new ArrayList<>();
    OrderHistoryAdapter orderHistoryAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_achat);


        


        orderHistory = database.getReference("user_trsanctions").child(MainActivity.getUserId());
        orderHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                int count =0;
               for(DataSnapshot ds : snapshot.getChildren()) {
                   OrderHistory orderHistory = ds.getValue(OrderHistory.class);
                   orderHistories.add(orderHistory);
                   GenericTypeIndicator<List<ProductMinify>> to = new
                           GenericTypeIndicator<List<ProductMinify>>() {};
                   List<ProductMinify> model = ds.child("prodcuts").getValue(to);
                   orderHistory.setProducts(model);
                   if (orderHistories.size() == size) {
                       orderHistoryAdapter.notifyDataSetChanged();

                   }
               }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        recyclerView = findViewById(R.id.rv_histyory_order);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistories);
        recyclerView.setAdapter(orderHistoryAdapter);


    }
}