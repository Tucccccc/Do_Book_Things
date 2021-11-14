package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookapptest2.databinding.ActivityPdfeditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

public class PDFEditActivity extends AppCompatActivity {
    private ActivityPdfeditBinding binding;
    private String bookID;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArrayList;
    private ArrayList<String> categoryIDArrayList;
    private static final String TAG = "BOOK_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfeditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookID = getIntent().getStringExtra("BookID"); //Get from AdapterPDFAdmin

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Xin hãy đợi");
        progressDialog.setCanceledOnTouchOutside(false);
        
        loadCategories();
        loadBookInfor();

        binding.txtCategoryEditPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        binding.btnBackEditPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnEditPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });
    }

    private String title = "";
    private String description = "";
    private void validData() {
        title = binding.txtBookTitleEditPDF.getText().toString().trim();
        description = binding.txtDescriptionEditPDF.getText().toString().trim();

        if(TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Chưa nhập tên sách", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Chưa nhập mô tả", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(selectedCategoryID)) {
            Toast.makeText(this, "Chưa chọn danh mục sách", Toast.LENGTH_LONG).show();
        }
        else {
            updatePDF();
        }

    }

    private void updatePDF() {
        Log.d(TAG, "updatePDF: Bắt đầu sửa file PDF");

        progressDialog.setMessage("Đang sửa thông tin sách");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("categoryID", selectedCategoryID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookID)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Sửa file thành công");
                        progressDialog.dismiss();
                        Toast.makeText(PDFEditActivity.this, "Sửa file thành công", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "onFailure: Sửa file thất bại " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PDFEditActivity.this,"" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadBookInfor() {
        Log.d(TAG, "loadBookInfor: Tải thông tin sách");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        //Line 43
        ref.child(bookID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        selectedCategoryID = "" + snapshot.child("categoryID").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String title = "" + snapshot.child("title").getValue();

                        binding.txtBookTitleEditPDF.setText(title);
                        binding.txtDescriptionEditPDF.setText(description);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");

                        reference.child(selectedCategoryID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String category = "" + snapshot.child("category").getValue();
                                        binding.txtCategoryEditPDF.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedCategoryID = "";
    private String selectedCategoryTitle = "";

    private void categoryDialog() {
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for(int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCategoryID = categoryIDArrayList.get(i);
                        selectedCategoryTitle = categoryTitleArrayList.get(i);

                        //Set text
                        binding.txtCategoryEditPDF.setText(selectedCategoryTitle);
                    }
                })
                .show();
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Đang tải danh mục");

        categoryIDArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIDArrayList.clear();
                categoryTitleArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String id = ds.child("id").getValue().toString();
                    String category = ds.child("category").getValue().toString();
                    categoryIDArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID " + id);
                    Log.d(TAG, "onDataChange: Danh mục " + category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}