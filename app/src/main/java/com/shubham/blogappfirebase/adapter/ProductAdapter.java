package com.shubham.blogappfirebase.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.shubham.blogappfirebase.AddProductActivity;
import com.shubham.blogappfirebase.ProductsActivity;
import com.shubham.blogappfirebase.R;
import com.shubham.blogappfirebase.databinding.ItemBinding;
import com.shubham.blogappfirebase.model.Product;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.shubham.blogappfirebase.ProductsActivity.imageuri;
import static com.shubham.blogappfirebase.ProductsActivity.storageReference;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product,ProductAdapter.ItemViewHolder> {

    private  Context context;
    public static final int PICK_IMAGE=1;

    public static ImageView iv_content_product_image;

    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options,Context context) {
        super(options);

        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Product model) {

        holder.itemBinding.tvItemDescription.setText(model.getTitle());
        holder.itemBinding.tvItemName.setText(model.getDescription());
        String image=getItem(position).getImage();

        Picasso.get().load(model.getImage()).into(holder.itemBinding.ivProductImage);

        holder.itemBinding.ivItemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50,0,50,0)
                        .setContentHolder(new ViewHolder(R.layout.content_layout))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();

                View holderView = dialog.getHolderView();

                 iv_content_product_image=holderView.findViewById(R.id.iv_content_product_image);
                EditText Productname=holderView.findViewById(R.id.et_content_product_name);
                EditText ProductDescription=holderView.findViewById(R.id.et_content_product_desc);
                Button btn_content_update=holderView.findViewById(R.id.btn_content_update);

                Productname.setText(model.getTitle());
                ProductDescription.setText(model.getDescription());
                Picasso.get().load(image).into(iv_content_product_image);

                iv_content_product_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ((ProductsActivity)context).startActivityForResult(intent,PICK_IMAGE);

                    }
                });

                btn_content_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        StorageReference filepath = storageReference.child("image").child(imageuri.getLastPathSegment());

                        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Uri downloaduri = uri;

                                        HashMap<String,Object>map=new HashMap<>();
                                        map.put("title",Productname.getText().toString().trim());
                                        map.put("Description",ProductDescription.getText().toString().trim());
                                        map.put("image",downloaduri.toString());

                                        FirebaseDatabase.getInstance().getReference().child("Products")
                                                .child(getRef(position).getKey())
                                                .updateChildren(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        dialog.dismiss();

                                                    }
                                                });
                                    }
                                });

                                    }
                                });

                            }
                        });


            }
        });

        holder.itemBinding.ivItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Products")
                        .child(getRef(position).getKey())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {

                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);

                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully

                                    Toast.makeText(context, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Toast.makeText(context, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        ItemBinding itemBinding=ItemBinding.inflate(inflater,parent,false);
        return new ItemViewHolder(itemBinding);

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemBinding itemBinding;

        public ItemViewHolder(ItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.itemBinding=itemBinding;

        }
    }
}
