package com.example.bookapptest2.filters;

import android.widget.Filter;

import com.example.bookapptest2.adapters.AdapterPDFUser;
import com.example.bookapptest2.models.ModelPDF;

import java.util.ArrayList;

public class FilterPDFUser extends Filter {
    private ArrayList<ModelPDF> listPDFUserFilter;
    private AdapterPDFUser adapterPDFUser;

    public FilterPDFUser(ArrayList<ModelPDF> listPDFUserFilter, AdapterPDFUser adapterPDFUser) {
        this.listPDFUserFilter = listPDFUserFilter;
        this.adapterPDFUser = adapterPDFUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();

        if(charSequence != null && charSequence.length() > 0) {
            //Change to to uppercase
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPDF> filteredList = new ArrayList<>();

            for(int i = 0; i < listPDFUserFilter.size(); i++) {
                if(listPDFUserFilter.get(i).getTitle().toUpperCase().contains(charSequence)) {
                    filteredList.add(listPDFUserFilter.get(i));
                }
            }
            filterResults.count = filteredList.size();
            filterResults.values = filteredList;
        }
        else {
            filterResults.count = listPDFUserFilter.size();
            filterResults.values = listPDFUserFilter;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterPDFUser.pdfArrayListUser = (ArrayList<ModelPDF>) filterResults.values;

        adapterPDFUser.notifyDataSetChanged();
    }
}
