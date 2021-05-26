package com.ensaf.arshopping.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.adapter.ArViewProducstAdapter;
import com.ensaf.arshopping.adapter.ProductAdapter;
import com.ensaf.arshopping.model.ProductBasket;
import com.ensaf.arshopping.model.Products;
import com.ensaf.arshopping.settings_activities.collections.ShowDetailCollectionActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GenralArViewer extends AppCompatActivity {

    WritingArFragment arFragment;
    private String ASSET_3D = "";
    Boolean fromColl = false,fromCollDetail=false;
    Boolean isPlacingObject = false;

    ArViewProducstAdapter arViewProducstAdapter;
    static RecyclerView recyclerView;
    private ModelRenderable modelRenderable;

    private VideoRecorder videoRecorder;

    //FirebaseInstance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allCollections;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";

    // The UI
    private FloatingActionButton recordButton;
    static ImageView rulerIv,saveCollIv,addCollIv;
    LinearLayout ll_container;
    AlertDialog alertDialog,alertDialog2;

    //For ruler logic
    List<Node> rulerNodes ;
    ArrayList<Float> arrayList1 = new ArrayList<>();
    ArrayList<Float> arrayList2 = new ArrayList<>();
    private AnchorNode lastAnchorNode;
    private TextView txtDistance;
    ModelRenderable cubeRenderable;
    static boolean rulerCliked = false;
    Vector3 point1, point2;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genral_ar_viewer);


        recordButton = findViewById(R.id.record);
        recyclerView = findViewById(R.id.rv_viewGroupProduct);
        rulerIv = findViewById(R.id.iv_ruler);
        txtDistance = findViewById(R.id.tv_distance);
        ll_container = findViewById(R.id.rl_container_recyler);
        saveCollIv = findViewById(R.id.iv_save_coll);
        addCollIv  = findViewById(R.id.iv_add_coll_to_cart);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ASSET_3D = extras.getString("3dProdUrl");
            if (extras.getString("fromColl") != null) {
                fromColl = true;
                saveCollIv.setVisibility(View.VISIBLE);
                addCollIv.setVisibility(View.VISIBLE);
                ll_container.setVisibility(View.VISIBLE);
            }
            if(extras.get("fromCollDetail")!= null) {
                Log.d("status","true");
                fromCollDetail = true;
                addCollIv.setVisibility(View.VISIBLE);
                ll_container.setVisibility(View.VISIBLE);

            }
        }


        if(user!=null)
        {
            currentUser = user.getUid();
        }
        allCollections = database.getReference("all_collections");



        Context context = this;


        saveCollIv.setOnClickListener(viewP -> {
            try {
                View viewModal = getLayoutInflater().inflate(R.layout.dialog_save_colle_layout, null);
                alertDialog = new AlertDialog.Builder(GenralArViewer.this)
                        .setView(viewModal)
                        .create();

                Button btnSave = viewModal.findViewById(R.id.btn_save_coll);
                Button btnCanel = viewModal.findViewById(R.id.btn_annuler);
                final EditText input = viewModal.findViewById(R.id.et_save_coll);
                final CheckBox checkBox = viewModal.findViewById(R.id.checkBox_savePos_coll);

                btnSave.setOnClickListener(view -> {
                    if (!input.getText().toString().isEmpty()) {
                        String key = allCollections.child(currentUser).push().getKey();
                        Map<String, String> liste = new HashMap<>();
                        for (Products product : ProductAdapter.allProducts) {
                            liste.put(product.getId(), product.getCatego());
                        }
                        if(checkBox.isChecked()){
                            liste.put("savePos","true");
                        }else{
                            liste.put("savePos","false");
                        }
                        liste.put("date",MainActivity.getCurrentTime());
                        liste.put("name", input.getText().toString());
                        allCollections.child(currentUser).child(key).setValue(liste);
                        alertDialog.dismiss();
                        alertDialog.cancel();
                        saveCollIv.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Veuilez preciser un nom du collection", Toast.LENGTH_SHORT).show();
                    }


                });

                btnCanel.setOnClickListener(view -> {
                    alertDialog.dismiss();
                    alertDialog.cancel();
                });
                alertDialog.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        addCollIv.setOnClickListener(viewP -> {
            try {
                View viewModal = getLayoutInflater().inflate(R.layout.dialog_add_colle_layout, null);
                alertDialog = new AlertDialog.Builder(GenralArViewer.this)
                        .setView(viewModal)
                        .create();

                Button btnAjouter = viewModal.findViewById(R.id.btn_ajouter_coll);
                Button btnCanel2 = viewModal.findViewById(R.id.btn_annuler2);

                btnAjouter.setOnClickListener(view -> {
                    if(ProductAdapter.allProducts!= null && ProductAdapter.allProducts.size()!=0){
                        for(Products prod : ProductAdapter.allProducts){
                            ProductBasket productBasket = new  ProductBasket(prod.getId(),"1",prod.getCatego());
                            database.getReference("all_baskets").child(currentUser).child(prod.getId()).setValue(productBasket);
                        }
                    }
                });
                btnCanel2.setOnClickListener(view -> {
                    alertDialog.dismiss();
                    alertDialog.cancel();
                });
                alertDialog.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        initForTheRuler();

        rulerIv.setOnClickListener(view -> {
            rulerCliked = !rulerCliked;
            if(rulerNodes == null) rulerNodes = new ArrayList<>();
            if (rulerCliked) {
                rulerIv.setImageResource(R.drawable.ruler_active);
            } else {
                rulerIv.setImageResource(R.drawable.ruler);
                onClear();
            }


        });



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if(fromCollDetail){
            arViewProducstAdapter = new ArViewProducstAdapter(this, ShowDetailCollectionActivity.listProducts);
        }else {
            arViewProducstAdapter = new ArViewProducstAdapter(this, ProductAdapter.allProducts);

        }
        recyclerView.setAdapter(arViewProducstAdapter);


        arFragment = (WritingArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_frgment_genral);
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if (!fromColl && !rulerCliked && !fromCollDetail) {
                    if(!isPlacingObject){
                        isPlacingObject = true;
                        initModelToScene(context, hitResult, ASSET_3D);

                    }
                } else if (rulerCliked) {
                    addRulerToScene(hitResult, motionEvent);
                } else if(fromCollDetail || fromColl){
                    if(!isPlacingObject){
                        isPlacingObject = true;
                        initModelToScene(context, hitResult, ArViewProducstAdapter.currentProduct.getUrl3dShape());
                    }
                }
            }
        });

        // Initialize the VideoRecorder.
        videoRecorder = new VideoRecorder();
        int orientation = getResources().getConfiguration().orientation;
        videoRecorder.setVideoBaseName("test");
        videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_2160P, orientation);
        videoRecorder.setSceneView(arFragment.getArSceneView());
        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(this::toggleRecording);
        recordButton.setEnabled(true);
    }








    public static void productDeselected(int pos) {
        recyclerView.getChildAt(pos).setBackgroundResource(R.drawable.bg_product_arview);
    }

    private void addRulerToScene(HitResult hitResult, MotionEvent motionEvent) {
        if (cubeRenderable == null) {
            return;
        }
        if (lastAnchorNode == null) {
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            Pose pose = anchor.getPose();
            if (arrayList1.isEmpty()) {
                arrayList1.add(pose.tx());
                arrayList1.add(pose.ty());
                arrayList1.add(pose.tz());
            }
            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            transformableNode.setRenderable(cubeRenderable);
            transformableNode.select();
            lastAnchorNode = anchorNode;

        } else {
            int val = motionEvent.getActionMasked();
            Log.d("motion ", motionEvent.toString());
            float axisVal = motionEvent.getAxisValue(MotionEvent.AXIS_X, motionEvent.getPointerId(motionEvent.getPointerCount() - 1));
            Log.e("Values:", String.valueOf(val) + String.valueOf(axisVal));
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Pose pose = anchor.getPose();
            if (arrayList2.isEmpty()) {
                arrayList2.add(pose.tx());
                arrayList2.add(pose.ty());
                arrayList2.add(pose.tz());
                float d = getDistanceMeters(arrayList1, arrayList2);
                txtDistance.setText("Distance: " + String.valueOf(d));
            } else {
                arrayList1.clear();
                arrayList1.addAll(arrayList2);
                arrayList2.clear();
                arrayList2.add(pose.tx());
                arrayList2.add(pose.ty());
                arrayList2.add(pose.tz());
                float d = getDistanceMeters(arrayList1, arrayList2);
                txtDistance.setText("Distance: " + String.valueOf(d));
            }

            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            transformableNode.setRenderable(cubeRenderable);
            transformableNode.select();


            // Vector3 point1, point2;
            rulerNodes.add(lastAnchorNode);
            rulerNodes.add(anchorNode);
            point1 = lastAnchorNode.getWorldPosition();
            point2 = anchorNode.getWorldPosition();



            final Vector3 difference = Vector3.subtract(point1, point2);
            final Vector3 directionFromTopToBottom = difference.normalized();
            final Quaternion rotationFromAToB =
                    Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
            MaterialFactory.makeOpaqueWithColor(getApplicationContext(), new Color(0, 255, 244))
                    .thenAccept(
                            material -> {
                                ModelRenderable model = ShapeFactory.makeCube(
                                        new Vector3(.01f, .01f, difference.length()),
                                        Vector3.zero(), material);
                                Node node = new Node();
                                node.setParent(anchorNode);
                                node.setRenderable(model);
                                node.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                                node.setWorldRotation(rotationFromAToB);
                            }
                    );
            lastAnchorNode = anchorNode;
        }
    }

    public static void rulerDeslecetd() {
        if (rulerCliked) {
            rulerIv.setImageResource(R.drawable.ruler);
            rulerCliked = false;

        }
    }

    @Override
    protected void onPause() {
        if (videoRecorder.isRecording()) {
            toggleRecording(null);
        }
        super.onPause();
    }

    private void onClear() {
        List<Node> children = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (Node node : rulerNodes) {
            if (node instanceof AnchorNode ) {
                if (((AnchorNode) node).getAnchor() != null) {
                    ((AnchorNode) node).getAnchor().detach();
                }
            }
            if (!(node instanceof Camera) && !(node instanceof Sun) ) {
                node.setParent(null);
            }
        }
        arrayList1.clear();
        arrayList2.clear();
        lastAnchorNode = null;
        point1 = null;
        point2 = null;
        txtDistance.setText("");
    }

    private float getDistanceMeters(ArrayList<Float> arayList1, ArrayList<Float> arrayList2) {

        float distanceX = arayList1.get(0) - arrayList2.get(0);
        float distanceY = arayList1.get(1) - arrayList2.get(1);
        float distanceZ = arayList1.get(2) - arrayList2.get(2);
        return (float) Math.sqrt(distanceX * distanceX +
                distanceY * distanceY +
                distanceZ * distanceZ);
    }

    private void initForTheRuler() {
        MaterialFactory.makeTransparentWithColor(this, new Color(0F, 0F, 244F))
                .thenAccept(
                        material -> {
                            Vector3 vector3 = new Vector3(0.01f, 0.01f, 0.01f);
                            cubeRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            cubeRenderable.setShadowCaster(false);
                            cubeRenderable.setShadowReceiver(false);
                        });


    }

    private void toggleRecording(View unusedView) {
        if (!arFragment.hasWritePermission()) {
            Log.e("TAG", "Video recording requires the WRITE_EXTERNAL_STORAGE permission");
            Toast.makeText(
                    this,
                    "Video recording requires the WRITE_EXTERNAL_STORAGE permission",
                    Toast.LENGTH_LONG)
                    .show();
            arFragment.launchPermissionSettings();
            return;
        }


        boolean recording = videoRecorder.onToggleRecord();

        if (recording) {
            recordButton.setImageResource(R.drawable.round_stop);
        } else {
            recordButton.setImageResource(R.drawable.round_videocam);
            String videoPath = "";
            if (videoRecorder.getVideoPath() != null) {
                videoPath = videoRecorder.getVideoPath().getAbsolutePath();
            }
            Toast.makeText(this, "Video saved: " + videoPath, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Video saved: " + videoPath);

            // Send  notification of updated content.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, "Sceneform Video");
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, videoPath);
            getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }
    }


    private void addNodeToScene(ModelRenderable rederable, Anchor anchor) {

        isPlacingObject = false;
        AnchorNode anchorNode = new AnchorNode(anchor);

        anchorNode.setParent(arFragment.getArSceneView().getScene());

        TransformableNode ts = new TransformableNode(arFragment.getTransformationSystem());

        ts.setParent(anchorNode);

        ts.getScaleController().setMaxScale(1.1f);

        ts.setRenderable(rederable);

        ts.select();

        this.arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void initModelToScene(Context context, HitResult hitResult, String url) {
        ModelRenderable
                .builder()
                .setSource(context,
                        RenderableSource
                                .builder()
                                .setScale(0.5f)
                                .setSource(context, Uri.parse(url), RenderableSource.SourceType.GLB)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                .build()

                ).setRegistryId(url)
                .build()
                .thenAccept(rederable -> addNodeToScene(rederable, hitResult.createAnchor()))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(throwable.getMessage()).show();
                    return null;
                });
    }




}