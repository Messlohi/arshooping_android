package com.ensaf.arshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.ensaf.arshopping.adapter.ProductBasketAdapter;
import com.ensaf.arshopping.model.ProductBasket;
import com.ensaf.arshopping.model.Products;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.Stripe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import com.ensaf.arshopping.adapter.ProductAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class BasketActivity extends AppCompatActivity {


    //For payment
    private static final String BACKEND_URL = "https://obscure-retreat-72694.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    @NonNull
    private String paymentIntentClientSecret = "";
    private Stripe stripe;
    //----------------------

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference basketsRef, productsref,all_transaction,user_transaction;
    int nbProductBasket = 0;

    Button btnChekout;
    AlertDialog alertDialog;
    View sheetView;
    RecyclerView recyclerView;
    ProductBasketAdapter productBasketAdapter;
    private String KeyTransaction = "";
    List<Products> listProducts = new ArrayList<>();
    List<String> listQuantite = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        basketsRef = database.getReference("all_baskets").child(MainActivity.getUserId());
        productsref = database.getReference("all_products");
        btnChekout = findViewById(R.id.btn_chekout);

        Context context = this;
        try {
            basketsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nbProductBasket = (int) snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.exists()) {
                            listQuantite.add(ds.child("quantite").getValue().toString());
                            productsref.child(ds.child("catego").getValue(String.class)).child(ds.child("productId").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot2) {
                                    Products product = snapshot2.getValue(Products.class);
                                    listProducts.add(product);
                                    if (snapshot.getChildrenCount() == listProducts.size()) {
                                        productBasketAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
        }


        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51IrLXKAjEtY2LWHRzgpLlnJFgd7mtXKYRWTkvn7vJK8YedIkCVVpFvBx4vO1WlBO4ibwA5jbhG8HsbfZsvKq8d0P006QfdiKqK")
        );


        recyclerView = findViewById(R.id.rv_prod_bakset);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        productBasketAdapter = new ProductBasketAdapter(context, listProducts, listQuantite);
        recyclerView.setAdapter(productBasketAdapter);


        btnChekout.setOnClickListener(view -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(BasketActivity.this, R.style.BottomSheetTheme);
            sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_payment, null);
            // sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_payment,(ViewGroup) findViewById(R.id.cl_conntainer_bottomSheet));
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
            startCheckout();

        });


    }


    //Its chekoutAvtivity class

    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
       Map<String, Object> payMap = new HashMap<>();
        payMap.put("currency", "usd");
        List<Map<String, Object>> listeProducts = new ArrayList<>();
        int i =0;
        for (Products product : ProductBasketAdapter.listeProducts) {
            Map<String, Object> productId = new HashMap<>();
            productId.put("id", product.getId());
            productId.put("title", product.getTitle());
            productId.put("urlImage", product.getUrlImage());
            productId.put("quantite", ProductBasketAdapter.listeDesQuantite.get(i));
            i++;
            productId.put("price", product.getPrice());
            productId.put("catego", product.getCatego());
            listeProducts.add(productId);
        }
        payMap.put("items", listeProducts);
        payMap.put("customer", MainActivity.getUserId());
        String json = new Gson().toJson(payMap);


        // Hook up the pay button to the card widget and stripe instance
        Button payButton = sheetView.findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            RequestBody body = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(this));
        });

    }


    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
        KeyTransaction = responseMap.get("key_trasaction");
        CardInputWidget cardInputWidget = sheetView.findViewById(R.id.cardInputWidget);
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            stripe.confirmPayment(this, confirmParams);
        }
    }

    private final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<BasketActivity> activityRef;

        PayCallback(@NonNull BasketActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final BasketActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error http: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final BasketActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                startCheckout();
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);

            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference all_transaction;
        @NonNull
        private final WeakReference<BasketActivity> activityRef;

        PaymentResultCallback(@NonNull BasketActivity activity) {
            activityRef = new WeakReference<>(activity);
            all_transaction = database.getReference("all_trsanctions");

        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final BasketActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                all_transaction.child(activity.KeyTransaction+"/status").setValue("Payé");
                activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent)
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                all_transaction.child(activity.KeyTransaction+"/status").setValue("Incomplet");
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final BasketActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            all_transaction.child(activity.KeyTransaction+"/status").setValue("Erreur");
            activity.displayAlert("Error", e.toString());
        }
    }
//end chekout activty class
}