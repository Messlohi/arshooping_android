package com.ensaf.arshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ensaf.arshopping.adapter.ProductAdapter;
import com.ensaf.arshopping.adapter.ProductCategoryAdapter;
import com.ensaf.arshopping.helpers.GenralArViewer;
import com.ensaf.arshopping.model.ProductCategory;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.model.User;
import com.ensaf.arshopping.payments.CheckoutActivityJava;
import com.ensaf.arshopping.payments.ChekoutPaymentSheet;
import com.ensaf.arshopping.settings_activities.AllUserSetingsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import jrizani.jrspinner.JRSpinner;

public class MainActivity extends AppCompatActivity {

    //Views
    ImageView iv_search;
    ImageView iv_simalationBegin;
    ImageView userSettings;
    static ImageView noresult;
    ImageView basketBtn;
    ImageView filterBtn;
    TextView tv_num_in_basket;
    String searchText;
    SearchView searchView;
    LinearLayout ll_seachContainer;
    public static LinearLayout ll_voirColl_container;
    Button  voirCollBtn ;
    //Helpers
    boolean searchDisplayed = false;
    boolean searchIsFound = false;
    public static  String categActuel = "Tous";
    static Boolean init = true, initCategory = true;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    static  public  boolean beginSimaltion = false;
    //For RecylerViews
    RecyclerView productCatRecycler;
    static RecyclerView prodItemRecycler;
    ProductCategoryAdapter productCategoryAdapter;
    List<String> categories  = new ArrayList<>();
    static  ProductAdapter productAdapter;
    static List<Products> productsList = new ArrayList<>();
    List<ProductCategory> productCategoryList = new ArrayList<>();

    //Firebase's Instances
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static String currentUser = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference  productRef;
    DatabaseReference basketsRef ,categoryRef,all_usersRef;
    List<Products> searchedFiltredProducts ;


    //Info User
    int nbProductBasket = 0;
    Context context = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        productCatRecycler = findViewById(R.id.cat_recycler);
        prodItemRecycler = findViewById(R.id.product_recycler);
        iv_search = findViewById(R.id.iv_searchBtn);
        searchView = findViewById(R.id.sv_search_product);
        ll_seachContainer = findViewById(R.id.ll_seachContainer);
        iv_simalationBegin = findViewById(R.id.iv_simalationBegin);
        userSettings = findViewById(R.id.iv_user_setting);
        tv_num_in_basket = findViewById(R.id.tv_num_basket);
        noresult = findViewById(R.id.iv_noresult);
        voirCollBtn = findViewById(R.id.btn_voirCollection);
        ll_voirColl_container = findViewById(R.id.ll_voirColl_container);
        basketBtn = findViewById(R.id.rl_basket_btn);
        filterBtn = findViewById(R.id.iv_filter_btn);


        initRef();
        initRecyclersData();
        handelSimulation();
        setProdItemRecycler(productsList);
        setProductRecycler(productCategoryList);
        if(user!=null){
            currentUser = user.getUid();
            handelUserBasket();
            Bundle extras= getIntent().getExtras();
            if(extras != null){
                if(extras.getString("name")!=null){
                    User user = new User(extras.getString("email"),getUserId(),extras.getString("name"));
                    all_usersRef.child(getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists()) return;
                            all_usersRef.child(getUserId()).setValue(user);
                        }
                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }
        }else {
            sendToLogin();
        }


        //Set Up stripe public Key
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51IrLXKAjEtY2LWHRzgpLlnJFgd7mtXKYRWTkvn7vJK8YedIkCVVpFvBx4vO1WlBO4ibwA5jbhG8HsbfZsvKq8d0P006QfdiKqK"
        );


        userSettings.setOnClickListener(view -> {
            Intent i = new Intent(this, AllUserSetingsActivity.class );
            startActivity(i);
            userSettings.setEnabled(false);

        });

        basketBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, BasketActivity.class);
            startActivity(i);
        });


        filterBtn.setOnClickListener(view -> {
            View viewModal = LayoutInflater.from(context).inflate(R.layout.dialog_filter_serach_layout, null);
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setView(viewModal)
                    .create();
            AutoCompleteTextView autoCompleteTextView = viewModal.findViewById(R.id.autoComplete);
            Spinner spinner = viewModal.findViewById(R.id.spinner);
            Button chercher = viewModal.findViewById(R.id.btn_search_filter);
            EditText min = viewModal.findViewById(R.id.et_min_filter);
            EditText max = viewModal.findViewById(R.id.et_max_filter);
            min.setInputType(InputType.TYPE_CLASS_NUMBER);
            max.setInputType(InputType.TYPE_CLASS_NUMBER);
            String[] spinnerArray =new String[categories.size()];
            spinnerArray = categories.toArray(spinnerArray);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (this, R.layout.simple_spiner_item,
                            spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);


            chercher.setOnClickListener(viewChrcher -> {
                float maxNumber =0;
                float minNumber =0;
                try {
                    if(!min.getText().toString().equals("min")) minNumber = Float.parseFloat(min.getText().toString().trim());
                    if(!max.getText().toString().equals("max")) maxNumber = Float.parseFloat(max.getText().toString().trim());
                }catch (Exception e){Toast.makeText(context,"Veuillez saisir un nombre valid",Toast.LENGTH_SHORT).show();}
                filterProduct(spinner.getSelectedItem().toString(),minNumber,maxNumber);
                alertDialog.cancel();
                alertDialog.dismiss();

            });


            alertDialog.show();
        });

        iv_search.setOnClickListener(view -> {
            // Initialize a new ChangeBounds transition instance
            ChangeBounds changeBounds = new ChangeBounds();

            // Set the transition start delay
            changeBounds.setStartDelay(300);

            // Set the transition interpolator
            changeBounds.setInterpolator(new AnticipateOvershootInterpolator());

            // Specify the transition duration
            changeBounds.setDuration(1000);

            // Begin the delayed transition
            TransitionManager.beginDelayedTransition(ll_seachContainer,changeBounds);

            // Toggle the button size
            toggleSearchAppear(searchView);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                searchIsFound = false;
                searchProduct();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    noresult.setVisibility(View.GONE);
                    prodItemRecycler.setVisibility(View.VISIBLE);
                    if(searchIsFound){
                        changeCategory(categActuel,true);
                        searchIsFound = false;
                    }

                }
                return true;
            }
        });

        voirCollBtn.setOnClickListener(view -> {
            Intent i = new Intent(context, GenralArViewer.class);
            i.putExtra("fromColl", "true");
            context.startActivity(i);
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getUid() == null) sendToLogin();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getUid() == null) sendToLogin();
        userSettings.setEnabled(true);

    }
    private  void sendToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static String getCurrentTime(){
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("dd-MMMM-yyyy' à 'HH:mm:ss");
        return  currenttime.format(ctime.getTime());
    }

    public static String getUserId(){
        if(user != null) return  FirebaseAuth.getInstance().getCurrentUser().getUid();
        return  "";
    }

    private  void filterProduct(String catego,float min ,float max){
        searchedFiltredProducts = new ArrayList<>();
        if(catego.equals("Tous")){
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot ds :snapshot.getChildren()){
                        GenericTypeIndicator<HashMap<String, Products>> to = new
                                GenericTypeIndicator<HashMap<String, Products>>() {};
                        HashMap<String, Products> model = ds.getValue(to);
                        for(String key : model.keySet()){
                            Products product = model.get(key);
                            float price = Float.parseFloat(product.getPrice());
                            if(max ==0 && price>=min) searchedFiltredProducts.add(product);
                            if(max!=0 && price>=min && price<=max) searchedFiltredProducts.add(product);
                        }
                    }

                    if(searchedFiltredProducts.size() !=0){
                        noresult.setVisibility(View.GONE);
                        prodItemRecycler.setVisibility(View.VISIBLE);
                        productsList.clear();
                        productsList.addAll(searchedFiltredProducts);
                        productAdapter.notifyDataSetChanged();
                    }else {
                        noresult.setVisibility(View.VISIBLE);
                        prodItemRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }else {
            productRef.child(catego).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot ds :snapshot.getChildren()){
                        Products product = ds.getValue(Products.class);
                        float price = Float.parseFloat(product.getPrice());
                        if(max ==0 && price>=min) searchedFiltredProducts.add(product);
                        if(max!=0 && price>=min && price<=max) searchedFiltredProducts.add(product);

                    }

                    if(searchedFiltredProducts.size() !=0){
                        noresult.setVisibility(View.GONE);
                        prodItemRecycler.setVisibility(View.VISIBLE);
                        productsList.clear();
                        productsList.addAll(searchedFiltredProducts);
                        productAdapter.notifyDataSetChanged();
                    }else {
                        noresult.setVisibility(View.VISIBLE);
                        prodItemRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }

    private  void searchProduct(){
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> searchedProducts = new ArrayList<>();
                for(DataSnapshot ds :snapshot.getChildren()){
                    GenericTypeIndicator<HashMap<String, Products>> to = new
                            GenericTypeIndicator<HashMap<String, Products>>() {};
                    HashMap<String, Products> model = ds.getValue(to);
                    for(String key : model.keySet()){
                        Products product = model.get(key);
                        if(product.getTitle().toLowerCase().contains(searchText.toLowerCase()) || product.getTitle().toLowerCase().startsWith(searchText.toLowerCase())){
                            searchIsFound = true;
                            searchedProducts.add(product);
                        }
                    }
                }
                if(searchIsFound){
                    noresult.setVisibility(View.GONE);
                    prodItemRecycler.setVisibility(View.VISIBLE);
                    productsList.clear();
                    productsList.addAll(searchedProducts);
                    productAdapter.notifyDataSetChanged();
                }else {
                    noresult.setVisibility(View.VISIBLE);
                    prodItemRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void toggleSearchAppear(SearchView v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if(!searchDisplayed){
            params.width = ll_seachContainer.getLayoutParams().width - iv_search.getLayoutParams().width-20;
            searchDisplayed = true;

        }else {
            params.width = 0;
            searchDisplayed = false;
        }
        v.setLayoutParams(params);
    }

    private void setProductRecycler(List<ProductCategory> productCategoryList){

        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        productCatRecycler.setLayoutManager(layoutManager);
        productCategoryAdapter = new ProductCategoryAdapter(this, productCategoryList);
        productCatRecycler.setAdapter(productCategoryAdapter);

    }

    private void setProdItemRecycler(List<Products> productsList){

        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this,2);
        prodItemRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this, productsList);
        prodItemRecycler.setAdapter(productAdapter);

    }


    private void initRecyclersData(){
        //For category recylerView
        productsList.clear();
        productCategoryList.clear();
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                    if(initCategory){
                        productCategoryList.add(new ProductCategory(1,"Tous"));
                        categories.add("Tous");
                        for(DataSnapshot ds :snapshot.getChildren()){
                            ProductCategory productCategory = new ProductCategory(1,ds.getValue().toString());
                            categories.add(productCategory.getCategoryName());
                            productCategoryList.add(productCategory);
                        }
                        productCategoryAdapter.notifyDataSetChanged();
                        initCategory = false;
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        categoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!initCategory){
                    ProductCategory productCategory = new ProductCategory(1,snapshot.getValue().toString());
                    productCategoryList.add(productCategory);
                    if(productCategoryAdapter != null)
                        productCategoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                    if(init){
                        for(DataSnapshot ds :snapshot.getChildren()){
                            GenericTypeIndicator<HashMap<String, Products>> to = new
                                    GenericTypeIndicator<HashMap<String, Products>>() {};
                            HashMap<String, Products> model = ds.getValue(to);
                            for(String key : model.keySet()){
                                Products product = model.get(key);
                                if(ProductAdapter.selectedProduct.containsKey(key)){
                                    product.setSelected(true);
                                }
                                productsList.add(model.get(key));
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                        init = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        productRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!init){
                    GenericTypeIndicator<HashMap<String, Products>> to = new
                            GenericTypeIndicator<HashMap<String, Products>>() {};
                    HashMap<String, Products> model = snapshot.getValue(to);
                    for(String key : model.keySet()){
                        productsList.add(model.get(key));
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void changeCategory(String catego,Boolean persitance){
        if(noresult.getVisibility() == View.VISIBLE) persitance = true;
        noresult.setVisibility(View.GONE);
        prodItemRecycler.setVisibility(View.VISIBLE);
        if(!catego.equals(categActuel)|| persitance){
            categActuel = catego;
            if(!catego.equals("Tous")){
                productRef.child(catego).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                            if(!init){
                                productsList.clear();
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    Products product = ds.getValue(Products.class);
                                    if(ProductAdapter.selectedProduct.containsKey(product.getId())){
                                        product.setSelected(true);
                                    }
                                    productsList.add(product);
                                }
                                productAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }else {
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                            productsList.clear();
                            for(DataSnapshot ds :snapshot.getChildren()){
                                GenericTypeIndicator<HashMap<String, Products>> to = new
                                        GenericTypeIndicator<HashMap<String, Products>>() {};
                                HashMap<String, Products> model = ds.getValue(to);
                                for(String key : model.keySet()){
                                    Products product = model.get(key);
                                    if(ProductAdapter.selectedProduct.containsKey(product.getId())){
                                        product.setSelected(true);
                                    }
                                    productsList.add(product);
                                }
                            }
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        }

    }

    private  void handelUserBasket(){
        basketsRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nbProductBasket = (int)snapshot.getChildrenCount();
                tv_num_in_basket.setText(nbProductBasket+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void handelSimulation(){
        iv_simalationBegin.setOnClickListener(view -> {
            beginSimaltion = !beginSimaltion;
            if(beginSimaltion){
                iv_simalationBegin.setImageResource(R.drawable.push_pin_in);
            }else {
                if(ProductAdapter.selectedProduct.size() != 0 ){
                    new AlertDialog.Builder(context)
                            .setTitle("Désélectionner")
                            .setMessage("Vous voulez annuler toute la sélection ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Log.d("size",ProductAdapter.selectedProduct.size()+"");
                                        for(String key : ProductAdapter.selectedProduct.keySet()){
                                            View item = ProductAdapter.selectedProduct.get(key);
                                            item.setBackgroundResource(R.drawable.bg_product_detail);
                                            ImageView img = item.findViewById(R.id.iv_chose_prodcut);
                                            img.setImageResource(R.drawable.push_pin_out);
                                        }
                                        iv_simalationBegin.setImageResource(R.drawable.push_pin_out);
                                        ll_voirColl_container.setVisibility(View.GONE);
                                        ProductAdapter.selectedProduct.clear();
                                        ProductAdapter.allProducts.clear();

                                    }catch (Exception e){e.printStackTrace();Toast.makeText(context,"nous rencontrons des problèmes :"+e.getMessage(),Toast.LENGTH_SHORT).show();}

                                }
                            })
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else {
                    iv_simalationBegin.setImageResource(R.drawable.push_pin_out);
                }

            }
        });
    }

    private  void initRef(){
        productRef = database.getReference("all_products");
        categoryRef = database.getReference("all_categories");
        basketsRef = database.getReference("all_baskets");
        all_usersRef = database.getReference("all_users");
    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}