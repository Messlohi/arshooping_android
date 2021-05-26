package com.ensaf.arshopping;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.ensaf.arshopping.model.ProductBasket;
import com.furkanakdemir.surroundcardview.StartPoint;
import com.furkanakdemir.surroundcardview.SurroundCardView;
import com.furkanakdemir.surroundcardview.SurroundListener;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Productdetails extends AppCompatActivity {


    SceneView scene;
    ImageView productImage,iv_addQ,iv_subQ;
    ConstraintLayout  imgContainer;
    String ASSET_3D = "",title,desc,price,productId;
    SurroundCardView surroundCardView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference basketRef;
    //3d Model isSet
    Boolean isPlaced = false;
    TextView tv_title, tv_desc, tv_price,tv_quanitie;
    Button addProduct;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";
    int quantite =1 ;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);
        surroundCardView = findViewById(R.id.sampleSurroundCardView);
        tv_title = findViewById(R.id.tv_title);
        tv_desc = findViewById(R.id.tv_desc);
        tv_price = findViewById(R.id.tv_price);
        tv_quanitie = findViewById(R.id.tv_quantite);
        addProduct = findViewById(R.id.btn_addToCarte);
        scene = findViewById(R.id.scene_sceneform_datail_product);
        productImage = findViewById(R.id.iv_product);
        imgContainer = findViewById(R.id.cl_imgProd_container);
        iv_addQ = findViewById(R.id.iv_add);
        iv_subQ = findViewById(R.id.tv_minus);




        iv_addQ.setOnClickListener(view -> {quantite++; tv_quanitie.setText(quantite+"");} );
        iv_subQ.setOnClickListener(view -> {if((quantite-1>=0))quantite--; tv_quanitie.setText(quantite+"");} );



        if(user!=null) {
            currentUser = user.getUid();
            basketRef = database.getReference("all_baskets").child(currentUser);


        }
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            if(extras.getString("3dProdUrl")!=null){
                ASSET_3D = extras.getString("3dProdUrl");
                productImage.setVisibility(View.GONE);
                surroundCardView.setVisibility(View.VISIBLE);
                try {
                    ModelRenderable
                            .builder()
                            .setSource(this,
                                    RenderableSource
                                            .builder()
                                            .setSource(this, Uri.parse(ASSET_3D),RenderableSource.SourceType.GLB)
                                            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                            .build()
                            ).setRegistryId(ASSET_3D)
                            .build()
                            .thenAccept(rederable -> addNodeToScene(rederable,scene))
                            .exceptionally(throwable -> {
                                Toast toast =
                                        Toast.makeText(this,"erreur :" +throwable.getMessage(), Toast.LENGTH_SHORT);
                                toast.show();
                                return null;
                            });
                }catch (Exception e){finish();}

            }else {
                Glide.with(this).load(extras.getString("3dImageUrl")).into(productImage);
                surroundCardView.setVisibility(View.GONE);
                imgContainer.setVisibility(View.VISIBLE);
                productImage.setVisibility(View.VISIBLE);
            }
            title = extras.getString("title");
            desc = extras.getString("desc");
            price = extras.getString("price");
            productId = extras.getString("id");
            surroundCardView.surround();
            tv_price.setText(price);
            tv_desc.setText(desc);
            tv_title.setText(title);





        }else {
            finish();
        }


        final int[] i = {0};
        surroundCardView.setSurroundListener(new SurroundListener() {
            @Override
            public void onSurround() {
                if(!isPlaced){
                    surroundCardView.setSurrounded(false);
                    if(i[0] %2 == 0) surroundCardView.setStartPoint(StartPoint.BOTTOM_END);
                    if(i[0] %2 != 0) surroundCardView.setStartPoint(StartPoint.TOP_START);
                    i[0]++;
                    surroundCardView.surround();
                }

            }
        });

        addProduct.setOnClickListener(view -> {
            ProductBasket newOne = new ProductBasket(productId,tv_quanitie.getText().toString(),extras.getString("catego"));
            basketRef.child(productId).setValue(newOne);
        });



    }


    private void addNodeToScene(ModelRenderable modelRenderable, SceneView scene) {

        isPlaced = true;

        Camera camera = scene.getScene().getCamera();

        camera.worldToScreenPoint(new Vector3(0f,-0.6f,-1f));

        surroundCardView.setVisibility(View.GONE);
        scene.setVisibility(View.VISIBLE);

        Node modelNode = new Node();

        modelNode.setParent(scene.getScene());

        modelNode.setLocalPosition(new Vector3(0f,-0.6f,-1f));

        TransformationSystem transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(),new FootprintSelectionVisualizer());
        TransformableNode transformableNode = new TransformableNode(transformationSystem);

        transformableNode.setRenderable(modelRenderable);

        transformableNode.getRotationController().setRotationRateDegrees(20f);
        transformableNode.getTranslationController().setEnabled(true);
        transformableNode.getScaleController().setMinScale(0.5f);
        transformableNode.getScaleController().setMaxScale(1.1f);


        transformableNode.setParent(modelNode);

        scene.getScene().addChild(modelNode);

        transformableNode.select();

        scene.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {

                try {
                    transformationSystem.onTouch(hitTestResult,motionEvent);

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    protected void onResume() {


        super.onResume();
        if(scene != null) {
            try {
                scene.resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onPause() {


        super.onPause();
        if(scene != null)
            scene.pause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if(scene != null)
            scene.destroy();
    }
}