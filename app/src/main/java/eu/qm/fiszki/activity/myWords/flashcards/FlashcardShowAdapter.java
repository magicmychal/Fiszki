package eu.qm.fiszki.activity.myWords.flashcards;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.listeners.flashcard.FlashcardClick;
import eu.qm.fiszki.listeners.flashcard.FlashcardLongClick;
import eu.qm.fiszki.listeners.flashcard.FlashcardStatisticClick;
import eu.qm.fiszki.model.flashcard.Flashcard;

/**
 * Created by tm on 15.07.16.
 */
public class FlashcardShowAdapter extends RecyclerView.Adapter<FlashcardShowAdapter.ViewHolder> {

    private ArrayList<Flashcard> mArrayList;
    private Activity mActivity;

    public FlashcardShowAdapter(Activity activity, ArrayList<Flashcard> arrayList) {
        mArrayList = arrayList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcards_show_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Flashcard flashcard = mArrayList.get(position);

        holder.mWord.setText(flashcard.getWord());
        holder.mTranslation.setText(flashcard.getTranslation());
        setStatistic(holder,flashcard);

        holder.mMain.setOnClickListener(new FlashcardClick(mActivity, flashcard));
        holder.mMain.setOnLongClickListener(new FlashcardLongClick(mActivity, flashcard));
        holder.mProcent.setOnClickListener(new FlashcardStatisticClick(mActivity,flashcard));

        if (SelectedFlashcardsSingleton.isFlashcard(flashcard)){
            holder.mMain.setBackgroundColor(mActivity.getResources().getColor(R.color.SelecteddColor));
        }else{
            if(new NightModeController(mActivity).getStatus()==0) {
                holder.mMain.setBackgroundColor(mActivity.getResources().getColor(R.color.White));
            }else{
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = mActivity.getTheme();
                theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true);
                @ColorInt int color = typedValue.data;
                holder.mMain.setBackgroundColor(color);
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

    private void setStatistic(ViewHolder holder,Flashcard flashcard){
        int procent = makeProcent(flashcard);
        holder.mProcent.setText(procent+"%");
        if(procent<=100 && procent>=65){
            holder.mProcent.setTextColor(mActivity.getResources().getColor(R.color.statistic_100_65));
        }else if (procent<65 && procent>=35){
            holder.mProcent.setTextColor(mActivity.getResources().getColor(R.color.statistic_65_35));
        }else{
            holder.mProcent.setTextColor(mActivity.getResources().getColor(R.color.statistic_35_0));
        }
    }

    private int makeProcent(Flashcard flashcard){
        int sum = flashcard.getStaticPass() + flashcard.getStaticFail();
        if(sum==0){
            return 0;
        }else{
            return (int)((flashcard.getStaticPass()*100f) / sum);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mMain;
        private TextView mTranslation;
        private TextView mWord;
        private TextView mProcent;

        public ViewHolder(View itemView) {
            super(itemView);
            mMain = (RelativeLayout) itemView.findViewById(R.id.mainCard);
            mWord = (TextView) itemView.findViewById(R.id.flashcard_word);
            mProcent = (TextView) itemView.findViewById(R.id.flashcard_procent);
            mTranslation = (TextView) itemView.findViewById(R.id.flashcard_translate);
        }
    }
}