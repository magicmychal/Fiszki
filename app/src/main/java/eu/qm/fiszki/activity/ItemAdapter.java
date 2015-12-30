package eu.qm.fiszki.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;

import eu.qm.fiszki.R;

public class ItemAdapter extends CursorAdapter
{
    DBAdapter newAdapter;
    MainActivity newActivity;
    TextView word, translation, rowId;
    String wordString, translationString;

    public ItemAdapter(Context context ,Cursor c, DBAdapter myDB, MainActivity mainActivity)
    {
        super(context, c);
        this.newAdapter = myDB;
        this.newActivity = mainActivity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        word = (TextView) view.findViewById(R.id.word);
        translation = (TextView) view.findViewById(R.id.translation);

        wordString = cursor.getString(cursor.getColumnIndexOrThrow(DBModel.KEY_WORD));
        translationString = cursor.getString(cursor.getColumnIndexOrThrow(DBModel.KEY_TRANSLATION));

        if (cursor.getInt(3) == 5)
        {
            view.setBackgroundResource(R.color.LearningGreen);
        }

        word.setText(wordString);
        translation.setText(translationString);
    }
}
