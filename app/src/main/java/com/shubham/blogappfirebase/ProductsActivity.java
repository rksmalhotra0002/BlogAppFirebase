package com.shubham.blogappfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubham.blogappfirebase.adapter.ProductAdapter;
import com.shubham.blogappfirebase.databinding.ActivityProductsBinding;
import com.shubham.blogappfirebase.databinding.ContentLayoutBinding;
import com.shubham.blogappfirebase.model.Product;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.shubham.blogappfirebase.adapter.ProductAdapter.iv_content_product_image;


public class ProductsActivity extends AppCompatActivity {

private ActivityProductsBinding activityProductsBinding;

private ProductAdapter productAdapter;

    public static  StorageReference storageReference;

    public static Uri imageuri;

    public static final int PICK_IMAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductsBinding=ActivityProductsBinding.inflate(getLayoutInflater());
        View view=activityProductsBinding.getRoot();
        setContentView(view);

        storageReference= FirebaseStorage.getInstance().getReference();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Product>options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference,Product.class)
                .build();

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        activityProductsBinding.recyclerviewProduct.setLayoutManager(gridLayoutManager);
        productAdapter=new ProductAdapter(options,ProductsActivity.this);
        activityProductsBinding.recyclerviewProduct.setAdapter(productAdapter);

        activityProductsBinding.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(ProductsActivity.this,AddProductActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        productAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.stopListening();
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

                iv_content_product_image.setImageURI(null);
                iv_content_product_image.setImageURI(resulturi);
                iv_content_product_image.setBackgroundResource(0);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}
