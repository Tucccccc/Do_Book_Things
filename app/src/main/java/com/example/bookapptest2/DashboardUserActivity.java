package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.bookapptest2.adapters.AdapterCategoryUser;
import com.example.bookapptest2.databinding.ActivityDashboardUserBinding;
import com.example.bookapptest2.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    private ActivityDashboardUserBinding binding;
    private ArrayList<ModelCategory> modelCategoryArrayList;
    private AdapterCategoryUser adapterCategoryUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategory();

//        //Logout
//        binding.btnDashUserLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//                checkUser();
//            }
//        });
        binding.txtSearchCateUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterCategoryUser.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadCategory() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        modelCategoryArrayList = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelCategoryArrayList.clear(); // Clear arraylist before get data
                for(DataSnapshot ds: snapshot.getChildren()) {
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    modelCategoryArrayList.add(model);
                }
                //Set up adapter
                adapterCategoryUser = new AdapterCategoryUser(DashboardUserActivity.this, modelCategoryArrayList);
                //Connect adapter to recycler
                binding.listCateUser.setAdapter(adapterCategoryUser);
                binding.listCateUser.setLayoutManager(new LinearLayoutManager(DashboardUserActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUser() {
         //Get current user
         FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

         if(firebaseUser == null) {
             startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
             finish();
         }
         else {
             String email = firebaseUser.getEmail();
         }
     }


}