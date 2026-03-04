package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.DescribableKt;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.LocalSharedPreferences;
import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class ClearDataBase extends PrimaryDrawerItem {

    private final Activity mActivity;
    private LocalSharedPreferences localSharedPreferences;

    public ClearDataBase(final Activity activity) {
        this.mActivity = activity;
        localSharedPreferences = new LocalSharedPreferences(activity);

        NameableKt.withName(this, R.string.drawer_clear_name);
        IconableKt.withIcon(this, R.drawable.broom);
        DescribableKt.withDescription(this, R.string.drawer_clear_sub);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.alert_clear_database_settings)
                    .setPositiveButton(R.string.button_action_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteDbRows();
                            new CategoryRepository(mActivity).addSystemCategory();
                        }
                    })
                    .setNegativeButton(R.string.button_action_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).show();
            return false;
        });
    }

    private void deleteDbRows() {
        FlashcardRepository flashcardRepository = new FlashcardRepository(mActivity.getBaseContext());
        CategoryRepository categoryRepository = new CategoryRepository(mActivity.getBaseContext());
        AlarmReceiver alarm = new AlarmReceiver();

        flashcardRepository.deleteFlashcards(flashcardRepository.getAllFlashcards());
        categoryRepository.deleteCategories(categoryRepository.getAllCategory());
        alarm.close(mActivity.getBaseContext());
        localSharedPreferences.setNotificationPosition(0);
        localSharedPreferences.setNotificationStatus(0);
    }
}
