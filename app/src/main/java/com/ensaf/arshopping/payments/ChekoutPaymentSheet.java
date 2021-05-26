package com.ensaf.arshopping.payments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ensaf.arshopping.R;
import com.ensaf.arshopping.adapter.ProductAdapter;
import com.ensaf.arshopping.model.Products;
import com.google.gson.Gson;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChekoutPaymentSheet extends AppCompatActivity {


    private static final String BACKEND_URL = "https://obscure-retreat-72694.herokuapp.com/";
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51IrLXKAjEtY2LWHRzgpLlnJFgd7mtXKYRWTkvn7vJK8YedIkCVVpFvBx4vO1WlBO4ibwA5jbhG8HsbfZsvKq8d0P006QfdiKqK";

    private PaymentSheet paymentSheet;

    private String paymentIntentClientSecret;
    private String customerId;
    private String ephemeralKeySecret;
    Context context = this;

    private Button buyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chekout_payment_sheet);



        buyButton = findViewById(R.id.run_btn);

        PaymentConfiguration.init(this, STRIPE_PUBLISHABLE_KEY);

        paymentSheet = new PaymentSheet(this, result -> {
            onPaymentSheetResult(result);
        });

        buyButton.setOnClickListener(v -> presentPaymentSheet());

        fetchInitData();

    }

    private void fetchInitData() {
       // final String requestJson = "{}";

        Map<String,Object> payMap = new HashMap<>();
        payMap.put("currency","usd");
        List<Map<String,Object>> listeProducts = new ArrayList<>();
        for(Products product : ProductAdapter.allProducts){
            Map<String,Object> productId =new HashMap<>();
            productId.put("id",product.getId());
            productId.put("catego",product.getCatego());
            listeProducts.add(productId);
        }
        payMap.put("items",listeProducts);
       final String requestJson = new Gson().toJson(payMap);

        final RequestBody requestBody = RequestBody.create(
                requestJson,
                MediaType.get("application/json; charset=utf-8")
        );

        final Request request = new Request.Builder()
                .url(BACKEND_URL + "payment-sheet")
                .post(requestBody)
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                       Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(
                            @NotNull Call call,
                            @NotNull Response response
                    ) throws IOException {
                        if (!response.isSuccessful()) {
                            // Handle failure
                        } else {
                            final JSONObject responseJson = parseResponse(response.body());

                            paymentIntentClientSecret = responseJson.optString("paymentIntent");
                            customerId = responseJson.optString("customer");
                            ephemeralKeySecret = responseJson.optString("ephemeralKey");

                            runOnUiThread(() -> buyButton.setEnabled(true));
                        }
                    }
                });
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                Log.d("status",responseBody.string());
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log.e("App", "Error parsing response", e);
            }
        }
        return new JSONObject();
    }

    private void presentPaymentSheet() {
        paymentSheet.presentWithSetupIntent(
                paymentIntentClientSecret,
                new PaymentSheet.Configuration(
                        "Example, Inc.",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKeySecret
                        )
                )
        );
    }

    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(
                    this,
                    "Payment Canceled",
                    Toast.LENGTH_LONG
            ).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(
                    this,
                    "Payment Failed. See logcat for details.",
                    Toast.LENGTH_LONG
            ).show();

            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(
                    this,
                    "Payment Complete",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}