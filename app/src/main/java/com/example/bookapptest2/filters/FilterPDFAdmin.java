package com.example.bookapptest2.filters;

import android.widget.Filter;

import com.example.bookapptest2.adapters.AdapterCategory;
import com.example.bookapptest2.adapters.AdapterPDFAdmin;
import com.example.bookapptest2.models.ModelCategory;
import com.example.bookapptest2.models.ModelPDF;

import java.util.ArrayList;

public class FilterPDFAdmin extends Filter {

    private ArrayList<ModelPDF> filterList;
    private AdapterPDFAdmin adapterPDFAdmin;

    public FilterPDFAdmin(ArrayList<ModelPDF> filterList, AdapterPDFAdmin adapterPDFAdmin) {
        this.filterList = filterList;
        this.adapterPDFAdmin = adapterPDFAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //Check null and empty
        if(charSequence != null && charSequence.length() > 0) {
            //Change to uppercase or lowercase to avoid sensitive case
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPDF> filteredModels = new ArrayList<>();

            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).getTitle().toUpperCase().contains(charSequence)) {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //Apply changes
        adapterPDFAdmin.pdfArrayList = (ArrayList<ModelPDF>)filterResults.values;

        adapterPDFAdmin.notifyDataSetChanged();
    }
}
