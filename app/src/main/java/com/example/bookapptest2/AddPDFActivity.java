package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookapptest2.databinding.ActivityAddPdfactivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPDFActivity extends AppCompatActivity {

    private ActivityAddPdfactivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final int PDF_PICK_CODE = 123;
    private Uri pdfURI = null;
    private ArrayList<String> categoryTitleArrayList;
    private ArrayList<String> categoryIDArrayList;
    private ProgressDialog progressDialog;

    //Debugging tag
    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadPDFCategories();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Xin hãy đợi");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.btnBackAddPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.txtCategoryAddPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });

        binding.btnSubmitPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });
    }

    private String title = "";
    private String description = "";

    private void validData() {
        Log.d(TAG, "validData: validating data");
        
        title = binding.txtBookTitleAddPDF.getText().toString().trim();
        description = binding.txtDescriptionAddPDF.getText().toString().trim();

        if(TextUtils.isEmpty(title)) {
            Toast.makeText(AddPDFActivity.this, "Bạn chưa nhập đúng tiêu đề sách", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(description)) {
            Toast.makeText(AddPDFActivity.this, "Bạn chưa nhập phần mô tả", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(AddPDFActivity.this, "Bạn chưa nhập đúng loại sách", Toast.LENGTH_LONG).show();
        }
        else if(pdfURI == null) {
            Toast.makeText(AddPDFActivity.this, "Bạn chưa chọn file PDF", Toast.LENGTH_LONG).show();
        }
        else {
            uploadPDFToStorage();
        }
    }

    private void uploadPDFToStorage() {
        Log.d(TAG, "uploadPDFToStorage: uploading to storage");

        progressDialog.setMessage("Đang tải file PDF lên");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        //PDF's path on Firebase
        String filePathAndName = "Book/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF upload succeed");
                        Log.d(TAG, "onSuccess: getting PDF url");

                        //Get PDF url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPDFUrl = "" + uriTask.getResult();

                        //Upload PDF file to database
                        uploadPDFToDatabase(uploadedPDFUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due to " + e.getMessage());
                        Toast.makeText(AddPDFActivity.this, "PDF upload failed due to " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadPDFToDatabase(String uploadedPDFUrl, long timestamp) {
        Log.d(TAG, "uploadPDFToDatabase: uploading to database");

        progressDialog.setMessage("Đang tải thông tin PDF lên");

        String uid = firebaseAuth.getUid();

        //Setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + title);
        hashMap.put("description", "" + description);
        hashMap.put("categoryID", "" + selectedCategoryID);
        hashMap.put("url", "" + uploadedPDFUrl);
        hashMap.put("timestamp", timestamp);
        hashMap.put("viewsCount", 0);
        hashMap.put("downloadCount", 0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: upload success to database");
                        Toast.makeText(AddPDFActivity.this, "Tải file lên cơ sở dữ liệu thành công", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: upload failed to database" + e.getMessage());
                        Toast.makeText(AddPDFActivity.this, "Tải file lên cơ sở dữ liệu không thành công", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String selectedCategoryID;
    private String selectedCategoryTitle;
    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");

        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for(int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
       }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get clicked item from list
                        selectedCategoryID = categoryIDArrayList.get(i);
                        selectedCategoryTitle = categoryTitleArrayList.get(i);

                        binding.txtCategoryAddPDF.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: selected category" + selectedCategoryTitle + " " + selectedCategoryID);
                    }
                })
                .show();

    }

    private void loadPDFCategories() {
        Log.d(TAG, "loadPDFCategories: loading pdf categories");
        categoryTitleArrayList = new ArrayList<>();
        categoryIDArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIDArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //Get value from database
                    String categoryID = ds.child("id").getValue().toString();
                    String categoryTitle = ds.child("category").getValue().toString();

                    categoryIDArrayList.add(categoryID);
                    categoryTitleArrayList.add(categoryTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 123) {
            Log.d(TAG, "onActivityResult: PDF Picked");
            pdfURI = data.getData();
            Log.d(TAG, "onActivityResult: URI " + pdfURI);
        }
        else {
            Log.d(TAG, "onActivityResult: cancelled picking pdf");
            Toast.makeText(AddPDFActivity.this, "cancelled picking pdf", Toast.LENGTH_LONG).show();
        }
    }
}