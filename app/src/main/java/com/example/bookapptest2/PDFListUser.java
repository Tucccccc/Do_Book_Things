package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.bookapptest2.adapters.AdapterPDFUser;
import com.example.bookapptest2.databinding.ActivityPdflistUserBinding;
import com.example.bookapptest2.models.ModelPDF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PDFListUser extends AppCompatActivity {
    private ActivityPdflistUserBinding binding;
    private ArrayList<ModelPDF> pdfArrayList;
    private String categoryID;
    private String categoryTitle;
    private AdapterPDFUser adapterPDFUser;
    private static final String TAG = "PDF_LIST_USER_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdflistUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get from AdapterCategoryUser
        Intent intent = getIntent();
        categoryID = intent.getStringExtra("categoryID");
        categoryTitle = intent.getStringExtra("categoryTitle");

        loadPDFList();

        binding.txtSearchBooksUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterPDFUser.getFilter().filter(charSequence);
                }
                catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadPDFList() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild("categoryID").equalTo(categoryID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ModelPDF modelPDF = ds.getValue(ModelPDF.class);
                            pdfArrayList.add(modelPDF);

                            Log.d(TAG, "onDataChange: " + modelPDF.getId() + " " + modelPDF.getTitle());
                        }
                        //Set up adapter
                        adapterPDFUser = new AdapterPDFUser(PDFListUser.this, pdfArrayList);
                        binding.listBooksUser.setAdapter(adapterPDFUser);
                        binding.listBooksUser.setLayoutManager(new LinearLayoutManager(PDFListUser.this));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}