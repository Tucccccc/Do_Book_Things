package com.example.bookapptest2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapptest2.PDFListUser;
import com.example.bookapptest2.databinding.RowCategoryUserBinding;
import com.example.bookapptest2.filters.FilterCategory;
import com.example.bookapptest2.filters.FilterCategoryUser;
import com.example.bookapptest2.models.ModelCategory;

import java.util.ArrayList;

public class AdapterCategoryUser extends RecyclerView.Adapter<AdapterCategoryUser.HolderCategoryUser> implements Filterable {
    private Context context;
    public ArrayList<ModelCategory> modelCategories;
    private ArrayList<ModelCategory> filterListCateUser;
    private FilterCategoryUser filterCategoryUser;
    private RowCategoryUserBinding binding;

    public AdapterCategoryUser(Context context, ArrayList<ModelCategory> modelCategories) {
        this.context = context;
        this.modelCategories = modelCategories;
        this.filterListCateUser = modelCategories;
    }

    @NonNull
    @Override
    public HolderCategoryUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCategoryUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCategoryUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategoryUser holder, int position) {
        ModelCategory modelCategory = modelCategories.get(position);
        String id = modelCategory.getId();
        String category = modelCategory.getCategory();
        int pos = position;

        holder.txtCategoryRowUser.setText(category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PDFListUser.class);
                //Pass ID and Title to PDFListUser
                intent.putExtra("categoryID", id);
                intent.putExtra("categoryTitle", category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelCategories.size();
    }

    @Override
    public Filter getFilter() {
        if(filterCategoryUser == null) {
            filterCategoryUser = new FilterCategoryUser(filterListCateUser, this);
        }
        return filterCategoryUser;
    }


    public class HolderCategoryUser extends RecyclerView.ViewHolder {
        private TextView txtCategoryRowUser;

        public HolderCategoryUser(@NonNull View itemView) {
            super(itemView);
            txtCategoryRowUser = binding.txtCategoryRowUser;
        }
    }
}
