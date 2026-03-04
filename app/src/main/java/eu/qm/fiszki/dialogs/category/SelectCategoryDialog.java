package eu.qm.fiszki.dialogs.category;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.settings.ChoosenCategoryAdapter;

/**
 * Created by tm on 11.07.16.
 */
public class SelectCategoryDialog extends MaterialDialog.Builder {


    public SelectCategoryDialog(@NonNull Context context, ArrayList<Category> categoryToPopulate) {
        super(context);
        this.title(R.string.drawer_select_name);
        this.customView(R.layout.category_choose, false);
        this.positiveText(R.string.button_action_ok);

        //populate listview
        ListView listView = (ListView) customView.findViewById(R.id.choose_category_lv);
        ChoosenCategoryAdapter choosenCategoryAdapter = new ChoosenCategoryAdapter(context,
                R.layout.category_choose_adapter, categoryToPopulate);
        listView.setAdapter(choosenCategoryAdapter);

        //limit of height
        if (listView.getAdapter().getCount() >= 6) {
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) listView.getLayoutParams();
            lp.height = 1000;
            listView.setLayoutParams(lp);
        }
    }


}
