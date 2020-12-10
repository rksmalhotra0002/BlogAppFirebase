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
import com.shubham.blogappfirebase.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

private ActivityLoginBinding activityLoginBinding;

private FirebaseAuth firebaseAuth;

private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view=activityLoginBinding.getRoot();
        setContentView(view);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(LoginActivity.this);

        setloginFirebaseAuth();
    }

    private void setloginFirebaseAuth()
    {

        activityLoginBinding.tvForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        activityLoginBinding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });
        activityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginemail=activityLoginBinding.etLoginEmail.getText().toString().trim();
                String loginpassword=activityLoginBinding.etLoginpassword.getText().toString().trim();

                if (TextUtils.isEmpty(loginemail)&&TextUtils.isEmpty(loginpassword))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(loginemail))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(loginpassword))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (loginpassword.length()<6){

                    Toast.makeText(LoginActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                else {

                    progressDialog.setMessage("login User");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(loginemail,loginpassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "login Successfully...", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,VideoActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}
