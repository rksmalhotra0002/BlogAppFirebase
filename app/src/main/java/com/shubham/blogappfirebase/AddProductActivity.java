package com.shubham.blogappfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shubham.blogappfirebase.databinding.ActivityAddProductBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding productBinding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    public static final int PICK_IMAGE=1;

    private Uri imageuri;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productBinding=ActivityAddProductBinding.inflate(getLayoutInflater());
        View view=productBinding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();

        storageReference= FirebaseStorage.getInstance().getReference();

        progressDialog=new ProgressDialog(AddProductActivity.this);

        uploadProductToFirebaseDatabase();

    }


    private void uploadProductToFirebaseDatabase()
    {

        productBinding.ivAddproductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);

            }
        });

        productBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                Intent intent=new Intent(AddProductActivity.this,LoginActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        });

            productBinding.btnUploadProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String productname=productBinding.etProductName.getText().toString().trim();
                    String productDescription=productBinding.etProductDescription.getText().toString().trim();

                    if (TextUtils.isEmpty(productname)&& TextUtils.isEmpty(productDescription)&&imageuri==null)
                    {
                        Toast.makeText(AddProductActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(productname))
                    {
                        Toast.makeText(AddProductActivity.this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(productDescription))
                    {
                        Toast.makeText(AddProductActivity.this, "Please Enter Product Description", Toast.LENGTH_SHORT).show();

                    }
                    else if (!TextUtils.isEmpty(productname)&&!TextUtils.isEmpty(productDescription)&&imageuri!=null){


                        progressDialog.setMessage("Add Product....");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        StorageReference filepath=storageReference.child("image").child(imageuri.getLastPathSegment());

                        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Uri downloaduri=uri;

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("title",productname);
                        map.put("Description",productDescription);
                        map.put("image",downloaduri.toString());

                        FirebaseDatabase.getInstance().getReference().child("Products").push().setValue(map).addOnCompleteListener(AddProductActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Intent intent=new Intent(AddProductActivity.this,ProductsActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddProductActivity.this, "Successfully Added to database", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
                            }
                        });

                    }
                }
            });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 if (requestCode==PICK_IMAGE && resultCode==RESULT_OK)
 {
     imageuri=data.getData();
     CropImage.activity(imageuri)
             .setGuidelines(CropImageView.Guidelines.ON)
             .setMinCropResultSize(10,10)
             .setMaxCropResultSize(600,600)
             .setCropShape(CropImageView.CropShape.RECTANGLE)
             .setAspectRatio(1,1)
             .start(this);

 }
 if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
 {
     CropImage.ActivityResult result=CropImage.getActivityResult(data);
     if (resultCode==RESULT_OK)
     {
         Uri resulturi=result.getUri();
         productBinding.ivAddproductImage.setImageURI(null);
         productBinding.ivAddproductImage.setImageURI(resulturi);
         productBinding.ivAddproductImage.setBackgroundResource(0);
     }
     else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
         Exception error = result.getError();
     }

 }
    }
}
