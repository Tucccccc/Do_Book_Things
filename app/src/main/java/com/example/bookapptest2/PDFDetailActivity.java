package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookapptest2.databinding.ActivityPdfdetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PDFDetailActivity extends AppCompatActivity {
    private ActivityPdfdetailBinding binding;

    private String bookId;  //Id get from AdapterPDFAdmin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get bookId intent from AdapterPDFAdmin
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        
        loadBookDetail();

        MyApplication.increBookViewCount(bookId);

        binding.btnBackPDFDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnReadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PDFDetailActivity.this, PDFViewActivity.class);
                intent1.putExtra("bookId", bookId);  //Send bookId to PDFViewActivity
                startActivity(intent1);
            }
        });
    }

    private void loadBookDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String title = snapshot.child("title").getValue().toString();
                        String description = snapshot.child("description").getValue().toString();
                        String categoryID = snapshot.child("categoryID").getValue().toString();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadCount").getValue();
                        String url = snapshot.child("url").getValue().toString();
                        String timestamp = snapshot.child("timestamp").getValue().toString();

                        String date = MyApplication.formatTimeStamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(categoryID, binding.txtCategory);
                        MyApplication.loadPDFFromUrlSinglePage(url, title, binding.pdfView, binding.progressBar);
                        MyApplication.loadPDFSize(url, title, binding.txtSize);

                        binding.txtTitle.setText(title);
                        binding.txtDescription.setText(description);
                        binding.txtViewCount.setText(viewsCount.replace("null", "N/A"));
                        binding.txtDownloadCount.setText(downloadsCount.replace("null", "N/A"));
                        binding.txtDate.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}