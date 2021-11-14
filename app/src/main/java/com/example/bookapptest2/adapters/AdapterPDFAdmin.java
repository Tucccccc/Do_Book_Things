package com.example.bookapptest2.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapptest2.MyApplication;
import com.example.bookapptest2.PDFDetailActivity;
import com.example.bookapptest2.PDFEditActivity;
import com.example.bookapptest2.databinding.RowPdfAdminBinding;
import com.example.bookapptest2.filters.FilterPDFAdmin;
import com.example.bookapptest2.models.ModelPDF;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPDFAdmin extends RecyclerView.Adapter<AdapterPDFAdmin.HolderPDFAdmin> implements Filterable {
    public static final long MAX_BYTES_PDF = 52428800;
    private Context context;
    public ArrayList<ModelPDF> pdfArrayList;
    public ArrayList<ModelPDF> filterList;
    private RowPdfAdminBinding binding;
    private static final String TAG = "PDF_ADAPTER_TAG";
    private FilterPDFAdmin filter;
    private ProgressDialog progressDialog;

    public AdapterPDFAdmin(Context context, ArrayList<ModelPDF> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Xin hãy đợi...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public AdapterPDFAdmin() {
    }

    @NonNull
    @Override
    public HolderPDFAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPDFAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPDFAdmin holder, int position) {
        ModelPDF model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryID();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        //Convert timestamp to dd/mm/yyyy
        String formattedDate = MyApplication.formatTimeStamp(timestamp);

        holder.txtTitle.setText(title);
        holder.txtDescription.setText(description);
        holder.txtDate.setText(formattedDate);

        MyApplication.loadCategory(
                categoryId, //"" + categoryId
                holder.txtCategory);
        MyApplication.loadPDFFromUrlSinglePage(
                pdfUrl,
                title,
                holder.pdfView,
                holder.progressBar
        );
        MyApplication.loadPDFSize(
                pdfUrl,
                title,
                holder.txtSize
        );

        //More button
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });

        //Click to items in pdf view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PDFDetailActivity.class);
                intent.putExtra("bookId", pdfId);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(ModelPDF model, HolderPDFAdmin holder) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(context, PDFEditActivity.class);
                            intent.putExtra("BookID", bookId);
                            context.startActivity(intent);
                        }
                        else if(i == 1) {
                            MyApplication.deleteBook(context,
                                    bookId.toString(),  //"" + bookId
                                    bookUrl.toString(),
                                    bookTitle.toString());
                        }
                    }
                }).show();
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size(); //Returns number of records
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new FilterPDFAdmin(filterList, this);
        }
        return filter;
    }

    public class HolderPDFAdmin extends RecyclerView.ViewHolder{
        PDFView pdfView;
        ProgressBar progressBar;
        TextView txtTitle;
        TextView txtDescription;
        TextView txtCategory;
        TextView txtSize;
        TextView txtDate;
        ImageButton btnMore;

        public HolderPDFAdmin(@NonNull View itemView) {
            super(itemView);

            //Init pdfView
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            txtTitle = binding.txtTitle;
            txtDescription = binding.txtDesPDF;
            txtCategory = binding.txtCategoryPDF;
            txtSize = binding.txtSizePDF;
            txtDate = binding.txtDatePDF;
            btnMore = binding.btnMore;
        }
    }
}
