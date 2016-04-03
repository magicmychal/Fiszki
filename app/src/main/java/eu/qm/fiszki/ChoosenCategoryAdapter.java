package eu.qm.fiszki;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import eu.qm.fiszki.model.Category;
import eu.qm.fiszki.model.CategoryManagement;

/**
 * Created by bgood on 2016-04-03.
 */
public class ChoosenCategoryAdapter extends ArrayAdapter<Category> {

    ArrayList arrayList;
    LayoutInflater inflater;
    Context context;
    int rLayout;
    CheckBox checkBox;
    CategoryManagement categoryRepository;

    public ChoosenCategoryAdapter(Context context, int resource, ArrayList<Category> arrayList) {
        super(context, resource, arrayList);
        this.arrayList = arrayList;
        this.context = context;
        this.rLayout = resource;
        categoryRepository = new CategoryManagement(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Category category = (Category) arrayList.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(rLayout, parent, false);
        checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
        checkBox.setText(category.getCategory());

        // here conditions

        return convertView;
    }

}

