package com.example.bookapptest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookapptest2.databinding.ActivityPdfdetailUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PDFDetailUser extends AppCompatActivity {
    private ActivityPdfdetailUserBinding binding;
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfdetailUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get bookID from AdapterPDFUser
        Intent intent = getIntent();
        bookID = intent.getStringExtra("bookID");

        loadBookDetail();

        MyApplication.increBookViewCount(bookID);

        binding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PDFDetailUser.this, PDFViewActivity.class);
                intent1.putExtra("bookId", bookID);
                startActivity(intent1);
            }
        });
    }

    private void loadBookDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title = snapshot.child("title").getValue().toString();
                        String des = snapshot.child("description").getValue().toString();
                        String cateID = snapshot.child("categoryID").getValue().toString();
                        String url = snapshot.child("url").getValue().toString();

                        MyApplication.loadCategory(cateID, binding.txtCatePDFUser);
                        MyApplication.loadPDFFromUrlSinglePage(url, title, binding.pdfViewUser, binding.progressBarUser);

                        binding.txtTitlePDFUser.setText(title);
                        binding.txtDesPDFUser.setText(des);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}