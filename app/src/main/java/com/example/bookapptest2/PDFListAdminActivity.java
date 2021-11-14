package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.bookapptest2.adapters.AdapterPDFAdmin;
import com.example.bookapptest2.databinding.ActivityPdflistAdminBinding;
import com.example.bookapptest2.models.ModelPDF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PDFListAdminActivity extends AppCompatActivity {
    private ArrayList<ModelPDF> pdfArrayList;
    private AdapterPDFAdmin adapterPDFAdmin;
    private ActivityPdflistAdminBinding binding;
    private String categoryID;
    private String categoryTitle;
    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdflistAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get from AdapterCategory
        Intent intent = getIntent();
        categoryID = intent.getStringExtra("categoryID");
        categoryTitle = intent.getStringExtra("categoryTitle");

        binding.txtSubTitle.setText(categoryTitle);

        loadPDFList();

        binding.txtSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterPDFAdmin.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPDFList() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryID").equalTo(categoryID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ModelPDF  model = ds.getValue(ModelPDF.class);
                            pdfArrayList.add(model);

                            Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                        }
                        //Set up adapter
                        adapterPDFAdmin = new AdapterPDFAdmin(PDFListAdminActivity.this, pdfArrayList);
                        binding.listBook.setAdapter(adapterPDFAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}