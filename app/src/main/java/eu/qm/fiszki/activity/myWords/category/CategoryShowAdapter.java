package eu.qm.fiszki.activity.myWords.category;

import android.app.Activity;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import eu.qm.fiszki.R;
import eu.qm.fiszki.dialogs.category.EditAndDeleteCategoryDialog;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton;
import eu.qm.fiszki.activity.myWords.flashcards.FlashcardsActivity;

/**
 * Created by tm on 15.07.16.
 */
public class CategoryShowAdapter extends RecyclerView.Adapter<CategoryShowAdapter.ViewHolder> {

    private ArrayList<Category> mArrayList;
    private Activity mActivity;

    public CategoryShowAdapter(Activity activity, ArrayList<Category> arrayList) {
        mArrayList = arrayList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_show_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Category category = mArrayList.get(position);

        holder.mName.setText(category.getCategory());

        setLanguageText(holder, category);

        holder.mMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryManagerSingleton.setCurrentCategoryId(category.getId());
                mActivity.startActivity(new Intent(mActivity, FlashcardsActivity.class));
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditAndDeleteCategoryDialog(mActivity, category).show();
            }
        });
    }

    private void setLanguageText(ViewHolder holder, Category category) {
        String langFrom = category.getLangFrom();
        String langOn = category.getLangOn();

        if (langFrom == null && langOn == null) {
            holder.mLang.setText(R.string.category_no_lang);
        } else {
            if (langFrom.isEmpty() && langOn.isEmpty()) {
                holder.mLang.setText(R.string.category_no_lang);
            } else {
                if (langFrom.isEmpty()) {
                    langFrom = mActivity.getString(R.string.category_no_lang);
                }
                if (langOn.isEmpty()) {
                    langOn = mActivity.getString(R.string.category_no_lang);
                }
                holder.mLang.setText(langFrom + " -> " + langOn);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mMain;
        private TextView mName;
        private TextView mLang;
        private ImageButton mEditBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mMain = (CardView) itemView.findViewById(R.id.placeCard);
            mLang = (TextView) itemView.findViewById(R.id.category_lang);
            mName = (TextView) itemView.findViewById(R.id.category_name);
            mEditBtn = (ImageButton) itemView.findViewById(R.id.editBtn);
        }
    }
}