package com.ensaf.arshopping.helpers;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ensaf.arshopping.R;

public class VideoViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);

        String URL  ="";

        Bundle extras = getIntent().getExtras();
        if(extras.getString("url")!= null) {
            URL = extras.getString("url");
            VideoView videoView= (VideoView)findViewById(R.id.exerciseVideo);
            Uri uri = Uri.parse(URL);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            MediaController mediaController = new MediaController(this);
            videoView.setKeepScreenOn(true);
            videoView.setMediaController(new MediaController(this) {
                @Override
                public void hide()
                {
                    mediaController.show();
                }

            });
            videoView.setMediaController(mediaController);
            videoView.start();
        }



    }
}