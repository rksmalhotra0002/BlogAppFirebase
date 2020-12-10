package com.shubham.blogappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.shubham.blogappfirebase.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

private ActivityRegisterBinding activityRegisterBinding;

private FirebaseAuth firebaseAuth;

private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding=ActivityRegisterBinding.inflate(getLayoutInflater());
        View view=activityRegisterBinding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();

        setAuthFirebase();
    }

    private void setAuthFirebase()
    {

        progressDialog=new ProgressDialog(RegisterActivity.this);

        activityRegisterBinding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        activityRegisterBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=activityRegisterBinding.etRegisterName.getText().toString().trim();
                String email=activityRegisterBinding.etRegisterEmail.getText().toString().trim();
                String password=activityRegisterBinding.etRegisterPassword.getText().toString().trim();
                String confirmpassword=activityRegisterBinding.etRegisterConfirmpassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(email)&&TextUtils.isEmpty(password)&&TextUtils.isEmpty(confirmpassword))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(name))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(confirmpassword))
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                }
                else if (!password.matches(confirmpassword))
                {
                    Toast.makeText(RegisterActivity.this, "Please match Password And Confirm Password", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<7)
                {
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals(confirmpassword))
                {

                    progressDialog.setMessage("Registered Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String,String>map=new HashMap<>();
                                map.put("name",name);
                                map.put("email",email);

                                databaseReference.setValue(map).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully....", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }

                                    }
                                });
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                               progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}
