package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.bookapptest2.databinding.ActivityRegisterBinding;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Xin hãy đợi");
        progressDialog.setCanceledOnTouchOutside(false);

//        //Back button
//        binding.btnBackReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        //Evensign up button
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });
    }

    private String name = "";
    private String email = "";
    private String password = "";
    private void validData() {
        //Check input data
        name = binding.txtName.getText().toString().trim();
        email = binding.txtEmailRegis.getText().toString().trim();
        password = binding.txtPassRegis.getText().toString().trim();
        String passRepeat = binding.txtPassRepeat.getText().toString().trim();

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Bạn không được để trống ", Toast.LENGTH_LONG).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Bạn nhập sai email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Bạn không được để trống mật khẩu", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passRepeat)) {
            Toast.makeText(this, "Bạn chưa xác nhận lại mật khảu", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(passRepeat)) {
            Toast.makeText(this, "Mật khẩu xác nhận sai", Toast.LENGTH_LONG).show();
        }
        else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        progressDialog.setMessage("Đang tạo tài khoản");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInformation();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" +e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUserInformation() {
        progressDialog.setMessage("Đang lưu thông tin người dùng...");

        long timeStamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();      //Check in database
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");   //Data to Users in Firebase Realtime Database
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Đã tạo tài khoản", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}