package com.example.bookapptest2.filters;

import android.widget.Filter;

import com.example.bookapptest2.adapters.AdapterCategoryUser;
import com.example.bookapptest2.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategoryUser extends Filter {
    private ArrayList<ModelCategory> modelCategoriesList;
    private AdapterCategoryUser adapterCategoryUser;

    public FilterCategoryUser(ArrayList<ModelCategory> modelCategoriesList, AdapterCategoryUser adapterCategoryUser) {
        this.modelCategoriesList = modelCategoriesList;
        this.adapterCategoryUser = adapterCategoryUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        //Check null and empty parameter
        if(charSequence != null && charSequence.length() > 0) {
            //Change all to uppercase or lowercase
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();

            for(int i = 0; i < modelCategoriesList.size(); i++) {
                if(modelCategoriesList.get(i).getCategory().toUpperCase().contains(charSequence)) {
                    filteredModels.add(modelCategoriesList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else  {
            results.count = modelCategoriesList.size();
            results.values = modelCategoriesList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //Apply changes
        adapterCategoryUser.modelCategories = (ArrayList<ModelCategory>) filterResults.values;

        adapterCategoryUser.notifyDataSetChanged();
    }
}
