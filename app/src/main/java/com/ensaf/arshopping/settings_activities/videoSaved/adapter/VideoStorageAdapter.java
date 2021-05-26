package com.ensaf.arshopping.settings_activities.videoSaved.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.lang.UProperty;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ensaf.arshopping.MainActivity;
import com.ensaf.arshopping.R;
import com.ensaf.arshopping.helpers.GenralArViewer;
import com.ensaf.arshopping.helpers.VideoViewer;
import com.ensaf.arshopping.settings_activities.videoSaved.VideoCaputuredActivty;
import com.ensaf.arshopping.settings_activities.videoSaved.model.videoInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VideoStorageAdapter extends RecyclerView.Adapter<VideoStorageAdapter.ProductViewHolder> {

    Context context;
    List<videoInfo> files;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference user_video ;
    StorageReference storageReference;
    VideoStorageAdapter videoStorageAdapter = this;

    public VideoStorageAdapter(Context context, List<videoInfo> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_video_row, parent, false);
        storageReference = FirebaseStorage.getInstance().getReference("User videos");
        user_video = database.getReference("user_video").child(MainActivity.getUserId());
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        videoInfo videoinfo = files.get(position);


        File file = new File(videoinfo.getPath());


        if(videoinfo.isDowloaded()){
            holder.upload.setVisibility(View.GONE);
        }

        file.setReadable(true,false);
        holder.delete.setTag(position);

        Uri uri = Uri.fromFile(file);
        Bitmap img = null;

        try {
            img = ThumbnailUtils.createVideoThumbnail(file, new Size(120,80),null);
        } catch (IOException e) {
            img = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_thumbnail);
            e.printStackTrace();
        }

        holder.more.setOnClickListener(view ->{
            if(holder.constraintLayout.getVisibility() == View.VISIBLE){
                holder.constraintLayout.setVisibility(View.GONE);
            }else {
                holder.constraintLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.constraintLayout.setOnClickListener(view -> {
            if(holder.constraintLayout.getVisibility() == View.VISIBLE){
                holder.constraintLayout.setVisibility(View.GONE);
            }else {
                holder.constraintLayout.setVisibility(View.VISIBLE);
            }
        });



        holder.delete.setOnClickListener(view -> {
            holder.linearLayout.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
            View viewModal = LayoutInflater.from(context).inflate(R.layout.dialog_delete_video_layout, null);
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setView(viewModal)
                    .create();

            CheckBox local = viewModal.findViewById(R.id.cb_delete_local);
            CheckBox cloud = viewModal.findViewById(R.id.cb_delete_cloud);
            Button supprimer  = viewModal.findViewById(R.id.btn_supp_vid);
            Button btnCanel = viewModal.findViewById(R.id.btn_annuler_dialog_vid);

            if(videoinfo.isDowloaded()){
                local.setChecked(false);
                local.setVisibility(View.GONE);
            }
            user_video.orderByChild("path").equalTo(file.getPath()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    String nameInStorage = "";
                    String keyRef = "";
                    if(!snapshot.exists()) {
                        cloud.setVisibility(View.GONE);
                    }
                    for(DataSnapshot ds : snapshot.getChildren()){
                        nameInStorage = ds.child("nameStorage").getValue(String.class);
                        keyRef = ds.child("key").getValue(String.class);
                    }
                    String finalKeyRef = keyRef;
                    String finalNameInStorage = nameInStorage;
                    supprimer.setOnClickListener(viewSupp -> {
                        if(!local.isChecked() && !cloud.isChecked() ){
                            Toast.makeText(context,"Cochez un choix ",Toast.LENGTH_SHORT).show();
                        }else {
                            if(cloud.isChecked()){
                                user_video.child(finalKeyRef).removeValue();
                                storageReference.child(finalNameInStorage).delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(context,"Un problème qui parvient ",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                });
                            }
                            if(local.isChecked()){
                                boolean status=  file.delete();
                                 if(status == false)  {
                                     Toast.makeText(context,"Un problème qui parvient ",Toast.LENGTH_SHORT).show();
                                 }else {
                                     files.remove((int)holder.delete.getTag());
                                     videoStorageAdapter.notifyItemRemoved((int)holder.delete.getTag());
                                 }
                                alertDialog.cancel();
                                alertDialog.dismiss();
                                return;
                            }
                        }
                    });
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                    alertDialog.show();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


            btnCanel.setOnClickListener(viewCancel -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });


        });
        holder.share.setOnClickListener(view -> { Toast.makeText(context,"share",Toast.LENGTH_SHORT).show();});
        holder.upload.setOnClickListener(view -> {
            user_video.orderByChild("path").equalTo(file.getPath()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        Toast.makeText(context,"Le video est déja téléverser",Toast.LENGTH_SHORT).show();
                    }else {
                        holder.linearLayout.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        String nameInStorage = System.currentTimeMillis()+".mp4";
                        final StorageReference ref = storageReference.child(nameInStorage);
                        UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    holder.linearLayout.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context,"Un problème qui parvient ",Toast.LENGTH_SHORT).show();
                                    throw task.getException();
                                }
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                String  key = user_video.push().getKey();
                                Uri uri = task.getResult();
                                videoInfo videoInfo  = new videoInfo();
                                videoInfo.setTitle(file.getName());
                                videoInfo.setUrl(uri.toString());
                                videoInfo.setDowloaded(true);
                                videoInfo.setPath(file.getPath());
                                videoInfo.setKey(key);
                                videoInfo.setNameStorage(nameInStorage);
                                user_video.child(key).setValue(videoInfo);
                                holder.linearLayout.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


        });

        holder.thumbnail.setImageBitmap(img);
        holder.title.setText(videoinfo.getTitle());

        holder.itemView.setOnClickListener(view -> {
            if(!videoinfo.isDowloaded() ){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "video/mp4");
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, VideoViewer.class);
                intent.putExtra("url",videoinfo.getUrl());
                context.startActivity(intent);
            }


        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }


    public static final class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView title,upload,share,delete;
        ImageView thumbnail;
        ImageButton more;
        View itemView;
        ConstraintLayout constraintLayout;
        RelativeLayout relativeLayout ;
        LinearLayout linearLayout;
        ProgressBar progressBar;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            more = itemView.findViewById(R.id.im_more_video);
            thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            constraintLayout =itemView.findViewById(R.id.cl_container);
            relativeLayout = itemView.findViewById(R.id.rl_container_recyler_vid);
            upload = itemView.findViewById(R.id.tv_upload_vid);
            share = itemView.findViewById(R.id.tv_share_vid);
            delete = itemView.findViewById(R.id.tv_delete_vid);
            linearLayout = itemView.findViewById(R.id.ll_container);
            progressBar = itemView.findViewById(R.id.progressBar_video_upload);
            this.itemView = relativeLayout;



        }
    }

}
