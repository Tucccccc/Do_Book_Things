package com.example.bookapptest2.filters;

import android.widget.Filter;

import com.example.bookapptest2.adapters.AdapterCategory;
import com.example.bookapptest2.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {

    private ArrayList<ModelCategory> filterList;
    private AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //Check null and empty parameter
        if(charSequence != null && charSequence.length() > 0) {
            //Change all to uppercase or lowercase
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();

            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).getCategory().toUpperCase().contains(charSequence)) {
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
        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>)filterResults.values;

        adapterCategory.notifyDataSetChanged();
    }
}
