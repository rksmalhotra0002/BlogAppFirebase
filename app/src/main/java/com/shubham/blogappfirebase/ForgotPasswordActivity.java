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
import com.google.firebase.auth.FirebaseAuth;
import com.shubham.blogappfirebase.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding activityForgotPasswordBinding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = activityForgotPasswordBinding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);

        setForgotAuth();
    }

    private void setForgotAuth() {

        activityForgotPasswordBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String forgotemail = activityForgotPasswordBinding.etForgotEmail.getText().toString().trim();

                if (TextUtils.isEmpty(forgotemail)) {

                    Toast.makeText(ForgotPasswordActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setMessage("Send Password Reset Email");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(forgotemail).addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(ForgotPasswordActivity.this, "Send Password Reset Email", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }
}
