package eu.qm.fiszki;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.qm.fiszki.model.Category;
import eu.qm.fiszki.model.CategoryManagement;

/**
 * Created by mBoiler on 19.02.2016.
 */
public class CategorySpinnerManagement {

    ArrayAdapter<String> dataAdapter;
    private Spinner spinner;
    private Context context;
    private CategoryManagement categoryManagement;
    private EditText categoryName;
    private Button addCategoryButton;

    public CategorySpinnerManagement(Spinner spinner, Context context) {
        this.spinner = spinner;
        this.context = context;
        categoryManagement = new CategoryManagement(context);
    }

    public void populateSpinner() {
        ArrayList<Category> categories = categoryManagement.getAllCategory();
        List<String> list = new ArrayList<String>();
        int x = 0;
        do {
            if (categories.get(x).getId() == 1) {
                list.add(context.getString(R.string.add_new_word_no_category));
            } else if (categories.get(x).getId() == 2) {
                list.add(context.getString(R.string.add_new_word_add_category));
            } else {
                list.add(categories.get(x).getCategory());
            }
            x++;
        } while (x != categories.size());
        dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(dataAdapter);
    }


    public void selectedSpinner(final Activity activity) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.layout_dialog_add_category);
                    dialog.setTitle(R.string.main_activity_dialog_edit_item);

                    addCategoryButton = (Button) dialog.findViewById(R.id.addCategoryButton);
                    categoryName = (EditText) dialog.findViewById(R.id.categoryName);

                    categoryName.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager)
                                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(categoryName, 0);
                        }
                    }, 50);

                    addCategoryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Category category = new Category(categoryName.getText().toString());
                            categoryManagement.addCategory(category);
                            Toast.makeText(context,
                                    context.getString(R.string.add_new_word_category_toast),
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            populateSpinner();
                            spinner.setSelection(dataAdapter.getPosition(categoryName.getText().toString()));
                        }
                    });

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int getSelectedCategoryID() {
        if (spinner.getSelectedItemPosition() == 0) {
            return 1;
        }
        String categoryNameFromSpinner = spinner.getSelectedItem().toString();
        Category category = categoryManagement.getCategoryByName(categoryNameFromSpinner);
        return category.getId();
    }
}
