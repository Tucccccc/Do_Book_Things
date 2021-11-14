package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookapptest2.databinding.ActivityPdfviewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PDFViewActivity extends AppCompatActivity {
    private ActivityPdfviewBinding binding;
    private String bookId;

    private static final String TAG = "PDF_VIEW_TAG";
    private static final long maxBytesPDF = 52428800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");  //Get intent from PDFDetailActivity

        loadBookDetails();
        
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Lấy PDF url từ database");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String pdfUrl = snapshot.child("url").getValue().toString();
                        String bookName = snapshot.child("title").getValue().toString();

                        Log.d(TAG, "onDataChange: PDF url: " + pdfUrl);

                        binding.txtTitlePage.setText(bookName);
                        loadBookFromURL(pdfUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    private void loadBookFromURL(String pdfUrl) {
        Log.d(TAG, "loadBookFromURL: Lấy file PDF từ storage");

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);

        ref.getBytes(maxBytesPDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        binding.progressBarRead.setVisibility(View.GONE);
                        binding.pdfViewRead.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int a = page + 1;
                                        binding.txtSubtitleToolbar.setText(a + "/" + pageCount);

                                        Log.d(TAG, "onPageChanged: " + a + "/" + pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(PDFViewActivity.this, "" + t.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "onError: " + t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(PDFViewActivity.this, "Lỗi ở trang: " + page + " " + t.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                    }
                                })
                                .load();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBarRead.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}