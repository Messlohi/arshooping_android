package com.ensaf.arshopping.settings_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;

import com.ensaf.arshopping.LoginActivity;
import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.settings_activities.collections.CollectionSavedActivity;
import com.ensaf.arshopping.settings_activities.hotorique_achat.HistoriqueAchatActivity;
import com.ensaf.arshopping.settings_activities.videoSaved.VideoCaputuredActivty;
import com.google.firebase.auth.FirebaseAuth;

public class AllUserSetingsActivity extends AppCompatActivity {


    CardView video_card,collection_card,achat_card,card_deconn;
    ConstraintLayout return_cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_setings);


        video_card = findViewById(R.id.vide_card);
        return_cl = findViewById(R.id.rcl_eturn);
        collection_card = findViewById(R.id.coll_card);
        achat_card = findViewById(R.id.achat_card);
        card_deconn = findViewById(R.id.card_deconn);


        FirebaseAuth auth = FirebaseAuth.getInstance();

        collection_card.setOnClickListener(view-> startActivity(new Intent(this, CollectionSavedActivity.class)));
        achat_card.setOnClickListener(view-> startActivity(new Intent(this, HistoriqueAchatActivity.class)));
        card_deconn.setOnClickListener(view->
        {auth.signOut();
            Intent intent =new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        video_card.setOnClickListener(view -> startActivity(new Intent(this, VideoCaputuredActivty.class)));
        return_cl.setOnClickListener(view-> finish());



    }
}