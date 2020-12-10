package com.shubham.blogappfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubham.blogappfirebase.databinding.ActivityAddvideoBinding;

import java.util.HashMap;

public class AddvideoActivity extends AppCompatActivity {

private ActivityAddvideoBinding activityAddvideoBinding;

public static final int VIDEO_PICK_GALLERY_CODE=100;
public static final int VIDEO_PICK_CAMERA_CODE=101;
public static final int CAMERA_REQUEST_CODE=102;

private String[] camerapermissions;

private Uri videoUrl;

private String title;

private ProgressDialog progressDialog;

private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddvideoBinding=ActivityAddvideoBinding.inflate(getLayoutInflater());
        View view=activityAddvideoBinding.getRoot();
        setContentView(view);

        camerapermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageReference= FirebaseStorage.getInstance().getReference();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);

        activityAddvideoBinding.uploadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 title=activityAddvideoBinding.titleEt.getText().toString().trim();
                if (TextUtils.isEmpty(title))
                {
                    Toast.makeText(AddvideoActivity.this, "Title is Required", Toast.LENGTH_SHORT).show();
                }
                else if (videoUrl==null)
                {
                    Toast.makeText(AddvideoActivity.this, "Please Pick video Before Upload", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadVideoToFirebase();
                }
            }
        });
        activityAddvideoBinding.VideoPickFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videoPickDialog();
            }
        });


    }

    private void uploadVideoToFirebase() {

        progressDialog.show();

        StorageReference filepath=storageReference.child("image").child(videoUrl.getLastPathSegment());

        filepath.putFile(videoUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

              filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {

                      Uri downloaduri=uri;

                      HashMap<String,Object>map=new HashMap<>();
                      map.put("title",title);
                      map.put("videourl",downloaduri.toString());

                      FirebaseDatabase.getInstance().getReference().child("Videos").push()
                              .setValue(map)
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {

                                  }
                              })
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      progressDialog.dismiss();
                                      Toast.makeText(AddvideoActivity.this, "Video Uploaded in Database", Toast.LENGTH_SHORT).show();
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {

                              progressDialog.dismiss();
                              Toast.makeText(AddvideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                          }
                      });
                  }
              });



                }



    });
    }
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,camerapermissions,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean result2= ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK)== PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }

    private void videoPickGallery()
    {
        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_PICK_GALLERY_CODE);
    }

    private void videoPickCamera()
    {
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,VIDEO_PICK_CAMERA_CODE);
    }

    private void videoPickDialog() {

    String [] options={"Camera","Gallery"};

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     if(i==0)
                     {
                         if (!checkCameraPermission())
                         {
                             requestCameraPermission();
                         }
                         else {
                             videoPickCamera();
                         }
                     }

                    else if(i==1)
                     {
                       videoPickGallery();
                     }

                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0)
                {
                   boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;

                   if (cameraAccepted&&storageAccepted)
                   {
                         videoPickCamera();
                   }
                   else {
                       Toast.makeText(this, "Camera & storage permission are required", Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {

                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK)
        {
            if (requestCode==VIDEO_PICK_GALLERY_CODE)
            {
                videoUrl=data.getData();
                setVideoToVideoView();

            }
            else  if (requestCode==VIDEO_PICK_CAMERA_CODE)
            {
                videoUrl=data.getData();

                setVideoToVideoView();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setVideoToVideoView() {

        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(activityAddvideoBinding.videoView);

        activityAddvideoBinding.videoView.setMediaController(mediaController);
        activityAddvideoBinding.videoView.setVideoURI(videoUrl);
        activityAddvideoBinding.videoView.requestFocus();
        activityAddvideoBinding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                activityAddvideoBinding.videoView.pause();
            }
        });

    }
}
