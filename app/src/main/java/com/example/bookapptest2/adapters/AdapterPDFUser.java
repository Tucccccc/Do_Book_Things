package com.example.bookapptest2.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapptest2.MyApplication;
import com.example.bookapptest2.PDFDetailUser;
import com.example.bookapptest2.databinding.RowPdfUserBinding;
import com.example.bookapptest2.filters.FilterPDFUser;
import com.example.bookapptest2.models.ModelPDF;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPDFUser extends RecyclerView.Adapter<AdapterPDFUser.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<ModelPDF> pdfArrayListUser;
    public ArrayList<ModelPDF> filterPDFList;
    private ProgressDialog progressDialog;
    private RowPdfUserBinding binding;
    private FilterPDFUser filter;

    public AdapterPDFUser(Context context, ArrayList<ModelPDF> pdfArrayListUser) {
        this.context = context;
        this.pdfArrayListUser = pdfArrayListUser;
        this.filterPDFList = pdfArrayListUser;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Xin hãy đợi...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public AdapterPDFUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new AdapterPDFUser.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPDFUser.ViewHolder holder, int position) {
        ModelPDF modelPDF = pdfArrayListUser.get(position);
        String pdfID = modelPDF.getId();
        String categoryID = modelPDF.getCategoryID();
        String title = modelPDF.getTitle();
        String des = modelPDF.getDescription();
        String pdfURL = modelPDF.getUrl();

        holder.title.setText(title);
        holder.des.setText(des);

        MyApplication.loadCategory(categoryID, holder.category);
        MyApplication.loadPDFFromUrlSinglePage(pdfURL, title, holder.pdfView, holder.progressBar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PDFDetailUser.class);
                intent.putExtra("bookID", pdfID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayListUser.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new FilterPDFUser(filterPDFList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PDFView pdfView;
        ProgressBar progressBar;
        TextView title;
        TextView des;
        TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfView = binding.pdfViewUser;
            progressBar = binding.progressBarUser;
            title = binding.txtBookTitleUser;
            des = binding.txtBookDesUser;
            category = binding.txtCategoryUserTitle;
        }
    }
}
