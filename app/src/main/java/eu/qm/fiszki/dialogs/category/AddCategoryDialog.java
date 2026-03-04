package eu.qm.fiszki.dialogs.category;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.category.ValidationCategory;

public class AddCategoryDialog extends MaterialDialog.Builder {

    private CategoryRepository mCategoryRepository;
    private TextInputEditText mCategoryNameET;
    private MaterialAutoCompleteTextView mCategoryLangFrom;
    private MaterialAutoCompleteTextView mCategoryLangOn;
    private Activity mActivity;
    private ValidationCategory mValidationCategory;

    public AddCategoryDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.category_dialog_title);
        this.icon(context.getResources().getDrawable(R.drawable.ic_category_add));
        this.customView(R.layout.category_add_dialog, false);
        this.positiveText(R.string.category_positive_btn_text);
        this.positiveColor(mActivity.getResources().getColor(R.color.ColorPrimaryDark));
        this.onPositive(addCategoryBtn());
        init();
        autoDismiss(false);
        setAdapterToLang();
    }

    private void init() {
        mCategoryNameET = (TextInputEditText) customView.findViewById(R.id.add_category_dialog_et_name);
        mCategoryLangFrom = (MaterialAutoCompleteTextView) customView.findViewById(R.id.add_category_dialog_lang_from);
        mCategoryLangOn = (MaterialAutoCompleteTextView) customView.findViewById(R.id.add_category_dialog_lang_on);
        mCategoryRepository = new CategoryRepository(mActivity);
        mValidationCategory = new ValidationCategory(mActivity);
    }

    private void setAdapterToLang() {
        String[] countries = context.getResources().getStringArray(R.array.support_lang);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, countries);
        mCategoryLangFrom.setAdapter(adapter);
        mCategoryLangOn.setAdapter(adapter);
    }

    private MaterialDialog.SingleButtonCallback addCategoryBtn() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                addCategory(dialog);
            }
        };
    }

    private void addCategory(Dialog dialog){
        Category category = new Category();
        category.setCategory(mCategoryNameET.getText().toString().trim());
        category.setEntryByUser(true);
        category.setLangOn(mCategoryLangOn.getText().toString().trim());
        category.setLangFrom(mCategoryLangFrom.getText().toString().trim());

        if (mValidationCategory.validate(category)) {
            mCategoryRepository.addCategory(category);
            Toast.makeText(context, R.string.category_toast, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}
