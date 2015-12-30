package eu.qm.fiszki.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.Alert;
import eu.qm.fiszki.R;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;
import eu.qm.fiszki.database.DBStatus;


public class MainActivity extends AppCompatActivity {

    static public SettingsActivity settings;
    static public DBAdapter myDb;
    static public DBStatus openDataBase;
    static public Alert alert;
    static public ItemAdapter flashCardList;
    static public ImageView emptyDBImage;
    static public TextView emptyDBText;
    static public Context context;
    static public ListView listView;
    static public FloatingActionButton fab;
    static public View[] selectedItem;
    static public int earlierPosition, selectPosition;
    static public ItemAdapter editedItem;
    static public Dialog dialog;
    static public EditText editOriginal;
    static public EditText editTranslate;
    static public Button dialogButton;
    public boolean[] clickedItem;
    public int rowId;
    public AlarmReceiver alarm;
    public Toolbar toolbar;
    Cursor deletedRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        settings = new SettingsActivity();
        alarm = new AlarmReceiver();
        alert = new Alert();
        openDataBase = new DBStatus();
        myDb = new DBAdapter(this);
        context = this;
        listView = (ListView) findViewById(R.id.listView);
        emptyDBImage = (ImageView) findViewById(R.id.emptyDBImage);
        emptyDBText = (TextView) findViewById(R.id.emptyDBText);
        emptyDBImage.setImageResource(R.drawable.emptydb);
        settings.context = this;
        settings.alarmIntent = new Intent(this, AlarmReceiver.class);
        settings.pendingIntent = PendingIntent.getBroadcast(this, 0, settings.alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        settings.manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        openDataBase.openDB(myDb);

        listViewSelect();
        toolbarMainActivity();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, AddWordActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listViewPopulate();
    }

    @Override
    public void onResume() {
        super.onResume();
        listViewPopulate();
        toolbarMainActivity();
        if (myDb.getAllRows().getCount() > 0) {
            emptyDBImage.setVisibility(View.INVISIBLE);
            emptyDBText.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(earlierPosition == -1) {
            return false;
        } else {
            getMenuInflater().inflate(R.menu.menu_selected_mainactivity, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
            fab.setVisibility(View.VISIBLE);
            clickedItem[selectPosition] = false;
            selectedItem[selectPosition].setSelected(false);
            toolbarMainActivity();
        }
        return true;
    }

    public void listViewPopulate() {
        sync();
        if (myDb.getAllRows().getCount() > 0) {
            flashCardList = new ItemAdapter(this, myDb.getAllRows(), myDb, this);
            listView.setAdapter(flashCardList);
        }
     }

    public void listViewSelect() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog_edit);
        dialog.setTitle(R.string.main_activity_dialog_edit_item);

        editOriginal = (EditText) dialog.findViewById(R.id.editOrginal);
        editTranslate = (EditText) dialog.findViewById(R.id.editTranslate);
        dialogButton = (Button) dialog.findViewById(R.id.editButton);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                rowId = (int) id;
                selectPosition = position;
                editedItem = (ItemAdapter) parent.getAdapter();
                editOriginal.setText(editedItem.getCursor().getString(1));
                editTranslate.setText(editedItem.getCursor().getString(2));

                if (!clickedItem[selectPosition] && earlierPosition == -1) {
                    selectedItem[selectPosition] = view;
                    clickedItem[selectPosition] = true;
                    selectedItem[selectPosition].setBackgroundColor(getResources().getColor(R.color.pressed_color));
                    selectedItem[selectPosition].setSelected(true);
                    fab.setVisibility(View.INVISIBLE);
                    earlierPosition = selectPosition;
                    toolbarSelected();
                } else if (!clickedItem[selectPosition]) {
                    selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
                    clickedItem[earlierPosition] = false;
                    selectedItem[earlierPosition].setSelected(false);
                    selectedItem[selectPosition] = view;
                    clickedItem[selectPosition] = true;
                    selectedItem[selectPosition].setBackgroundColor(getResources().getColor(R.color.pressed_color));
                    selectedItem[selectPosition].setSelected(true);
                    fab.setVisibility(View.INVISIBLE);
                    earlierPosition = selectPosition;
                    toolbarSelected();
                } else {
                    selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
                    fab.setVisibility(View.VISIBLE);
                    clickedItem[selectPosition] = false;
                    selectedItem[selectPosition].setSelected(false);
                    toolbarMainActivity();
                }
            }
        });
    }

    public void toolbarMainActivity() {
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setBackgroundResource(R.color.ColorPrimary);
        toolbar.setNavigationIcon(null);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.settings) {
                            Intent goSettings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(goSettings);
                            finish();
                        } else if (id == R.id.examMode) {
                            if (myDb.getAllRows().getCount() > 0) {
                                Intent goLearningMode = new Intent(MainActivity.this, ExamModeActivity.class);
                                startActivity(goLearningMode);
                            } else {
                                alert.buildAlert(getString(R.string.alert_title_fail), getString(R.string.alert_learningmode_emptybase), getString(R.string.button_action_ok), MainActivity.this);
                            }
                        } else if(id == R.id.learningMode){
                            if (myDb.getAllRows().getCount() > 0) {
                                Intent goLearningMode = new Intent(MainActivity.this, LearningModeActivity.class);
                                startActivity(goLearningMode);
                            } else {
                                alert.buildAlert(getString(R.string.alert_title_fail), getString(R.string.alert_learningmode_emptybase), getString(R.string.button_action_ok), MainActivity.this);
                            }
                        }
                        return true;
                    }
                });
        toolbar.dismissPopupMenus();
    }

    public void toolbarSelected() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.main_activity_title_seleced_record));
        toolbar.setBackgroundResource(R.color.seleced_Adapter);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.editRecord) {
                            listViewEdit();
                        } else if (id == R.id.deleteRecord) {
                            listViewDelete();
                        } else if (id == android.R.id.home) {
                            selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
                            fab.setVisibility(View.VISIBLE);
                            clickedItem[selectPosition] = false;
                            selectedItem[selectPosition].setSelected(false);
                            toolbarMainActivity();
                        }
                        return true;
                    }
                });
        toolbar.dismissPopupMenus();
    }

    public void listViewEdit() {
        editOriginal.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(editOriginal, 0);
            }
        }, 50);
        editOriginal.setSelection(editOriginal.getText().length());

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editOriginal.getText().toString().isEmpty() || editTranslate.getText().toString().isEmpty()) {
                    alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onEmptyFields), getString(R.string.button_action_ok), MainActivity.this);
                } else {
                    if (myDb.getRow(rowId).getString(1).equals(editOriginal.getText().toString())) {
                        myDb.updateAdapter(rowId, editOriginal.getText().toString(),
                                editTranslate.getText().toString());
                        selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
                        fab.setVisibility(View.VISIBLE);
                        clickedItem[selectPosition] = false;
                        selectedItem[selectPosition].setSelected(false);
                        listViewPopulate();
                        dialog.dismiss();
                        toolbarMainActivity();
                    } else {
                        if (myDb.getRowValue(DBModel.KEY_WORD, editOriginal.getText().toString())) {
                            alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onRecordExist), getString(R.string.button_action_ok), MainActivity.this);
                            editOriginal.requestFocus();
                        } else {
                            myDb.updateAdapter(rowId, editOriginal.getText().toString(),
                                    editTranslate.getText().toString());
                            //selectedItem[earlierPosition].setBackgroundColor(getResources().getColor(R.color.default_color));
                            fab.setVisibility(View.VISIBLE);
                            clickedItem[selectPosition] = false;
                            //selectedItem[selectPosition].setSelected(false);
                            listViewPopulate();
                            dialog.dismiss();
                            toolbarMainActivity();
                        }
                    }
                }
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public void listViewDelete() {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(getString(R.string.alert_title));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(Html.fromHtml(getString(R.string.alert_delete_record)));
        alertDialog.setButton(getString(R.string.button_action_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDb.deleteRecord(rowId);
                deletedRow = editedItem.getCursor();
                if (myDb.getAllRows().getCount() > 0) {
                    listViewPopulate();
                    toolbarMainActivity();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), getString(R.string.snackbar_returnword_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_returnword_button), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDb.insertRow(deletedRow.getInt(0), deletedRow.getString(1),
                                            deletedRow.getString(2), deletedRow.getInt(3));
                                    listViewPopulate();
                                }
                            });
                    snackbar.show();
                    fab.setVisibility(View.VISIBLE);
                } else {
                    emptyDBImage.setVisibility(View.VISIBLE);
                    emptyDBText.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    myDb.updateRow(settings.notificationStatus, 0);
                    myDb.updateRow(settings.notificationPosition, 0);
                    alarm.close(settings.manager, context, settings.pendingIntent);
                    toolbarMainActivity();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), getString(R.string.snackbar_returnword_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_returnword_button), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDb.insertRow(deletedRow.getInt(0), deletedRow.getString(1),
                                            deletedRow.getString(2), deletedRow.getInt(3));
                                    emptyDBImage.setVisibility(View.INVISIBLE);
                                    emptyDBText.setVisibility(View.INVISIBLE);
                                    listView.setVisibility(View.VISIBLE);
                                    listViewPopulate();
                                }
                            });
                    snackbar.show();
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
        alertDialog.setButton2(getString(R.string.button_action_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void sync() {
        earlierPosition = -1;
        int x = myDb.getAllRows().getCount();
        selectedItem = new View[x+1];
        clickedItem = new boolean[x+1];
        Arrays.fill(clickedItem, Boolean.FALSE);
    }
}

