package com.ensaf.arshopping.settings_activities.videoSaved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.helpers.VideoRecorder;
import com.ensaf.arshopping.settings_activities.videoSaved.adapter.VideoStorageAdapter;
import com.ensaf.arshopping.settings_activities.videoSaved.model.videoInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoCaputuredActivty extends AppCompatActivity {


    RecyclerView recyclerView;
    VideoStorageAdapter videoStorageAdapter;
    List<videoInfo> videoInfos = new ArrayList<>();
    String choice = "local";
    Button local, tous, cloud;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference user_video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_caputured_activty);

        local = findViewById(R.id.btn_local_vid);
        tous = findViewById(R.id.btn_tous_vid);
        cloud = findViewById(R.id.btn_cloud_vid);
        user_video = database.getReference("user_video").child(MainActivity.getUserId());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        cloud.setOnClickListener(view -> {
            if (choice.equals("cloud")) return;
            choice = "cloud";
            videoInfos.clear();
            fetchAllCloud();
        });

        local.setOnClickListener(view -> {
            if (choice.equals("local")) return;
            choice = "local";
            videoInfos.clear();
            fetchAllVidLocal();
            videoStorageAdapter.notifyDataSetChanged();
        });

        tous.setOnClickListener(view -> {
            if (!choice.equals("tous")) {
                if (choice.equals("local")) {
                    fetchAllCloud();
                } else if (choice.equals("cloud")) {
                    fetchAllVidLocal();
                    videoStorageAdapter.notifyDataSetChanged();
                }
                choice = "tous";

            }
        });
        recyclerView = findViewById(R.id.rv_videos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fetchAllVidLocal();
        videoStorageAdapter = new VideoStorageAdapter(this, videoInfos);
        recyclerView.setAdapter(videoStorageAdapter);
    }

    private void fetchAllVidLocal() {
        for (File file : new File(VideoRecorder.pathVideoDir).listFiles()) {
            videoInfo videoInfo = new videoInfo();
            user_video.orderByChild("path").equalTo(file.getPath()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot ds  :snapshot.getChildren()){
                            videoInfo.setKey(ds.getValue(com.ensaf.arshopping.settings_activities.videoSaved.model.videoInfo.class).getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
            videoInfo.setTitle(file.getName());
            videoInfo.setPath(file.getPath());
            videoInfos.add(videoInfo);
        }
    }

    private void fetchAllCloud() {
        user_video.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    videoInfo videoinfo = ds.getValue(videoInfo.class);
                    videoInfos.add(videoinfo);
                }
                videoStorageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}