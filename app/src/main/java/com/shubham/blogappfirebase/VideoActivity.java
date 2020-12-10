package com.shubham.blogappfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shubham.blogappfirebase.databinding.ActivityVideoBinding;

public class VideoActivity extends AppCompatActivity {

private ActivityVideoBinding activityVideoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVideoBinding=ActivityVideoBinding.inflate(getLayoutInflater());
        View view=activityVideoBinding.getRoot();
        setContentView(view);

        uploadVideoFirebase();

    }

    private void uploadVideoFirebase()
    {

        activityVideoBinding.floatingVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(VideoActivity.this,AddvideoActivity.class);
                startActivity(intent);
            }
        });
    }


}