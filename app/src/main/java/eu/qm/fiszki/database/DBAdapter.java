package eu.qm.fiszki.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertRow(String word, String translate, int priority) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBModel.KEY_WORD, word);
        initialValues.put(DBModel.KEY_TRANSLATION, translate);
        initialValues.put(DBModel.KEY_PRIORITY, priority);
        return db.insert(DBModel.DATABASE_TABLE, null, initialValues);
    }

    public long insertRow(int id, String word, String translate, int priority) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBModel.KEY_ROWID, id);
        initialValues.put(DBModel.KEY_WORD, word);
        initialValues.put(DBModel.KEY_TRANSLATION, translate);
        initialValues.put(DBModel.KEY_PRIORITY, priority);
        return db.insert(DBModel.DATABASE_TABLE, null, initialValues);
    }

    public void deleteAll(String table){
        db.execSQL("delete from "+ table);
    }

    public long updateRow(String settingName , int status) {
        ContentValues values = new ContentValues();
        values.put(DBModel.SETTINGS_STATUS, status);
        return db.update(DBModel.SETTINGS_TABLE, values,
                DBModel.SETTINGS_NAME + "= " + "'" + settingName + "'", null);
    }


    public long updateFlashcardPriority(int id, int ptiotity){
        ContentValues values = new ContentValues();
        values.put(DBModel.KEY_PRIORITY, ptiotity);
        return db.update(DBModel.DATABASE_TABLE, values, DBModel.KEY_ROWID + "=" + id, null);}

    public long updateAdapter(int id , String word, String translate) {
        ContentValues values = new ContentValues();
        values.put(DBModel.KEY_WORD, word);
        values.put(DBModel.KEY_TRANSLATION, translate);
        return db.update(DBModel.DATABASE_TABLE, values, DBModel.KEY_ROWID + "= " + "'" + id + "'", null);
    }

    public long deleteRecord(int id) {
        String where = DBModel.KEY_ROWID+" = "+id;
        return db.delete(DBModel.DATABASE_TABLE, where, null);
    }

    public boolean getRowValue(String column, String text) {
        Cursor c = db.query(false, DBModel.DATABASE_TABLE, DBModel.ALL_KEYS,
                column +"="+ "'"+text+"'", null, null, null, null, null);
        if (c.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }

    public int intRowValue(String column, String text){
        String where = column + "=" + "'" + text + "'";
        Cursor c = db.query(true, DBModel.SETTINGS_TABLE, DBModel.ALL_KEYS_SETTINGS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        int value = c.getInt(c.getColumnIndex(DBModel.SETTINGS_STATUS));
        return value;
    }

    public Cursor getAllRows() {
        Cursor c = db.query(true, DBModel.DATABASE_TABLE, DBModel.ALL_KEYS,
                null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllRowsPriority(int priority){
        Cursor c = db.query(true, DBModel.DATABASE_TABLE,DBModel.ALL_KEYS,
                DBModel.KEY_PRIORITY + "=" + priority, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRandomRowWithpriority(int priority) {
        Cursor c = db.query(true, DBModel.DATABASE_TABLE, DBModel.ALL_KEYS,
                DBModel.KEY_PRIORITY + "=" + priority, null, null, null, "RANDOM()", "1");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRandomRow() {
        Cursor c = db.query(true, DBModel.DATABASE_TABLE, DBModel.ALL_KEYS,
                null, null, null, null, "RANDOM()", "1");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRow(int id) {
        String where = DBModel.KEY_ROWID+" = "+id;
        Cursor c = db.query(true, DBModel.DATABASE_TABLE, DBModel.ALL_KEYS,
                where, null, null, null, null, "1");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DBModel.DATABASE_NAME, null, DBModel.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DBModel.DATABASE_CREATE_SQL);
            _db.execSQL(DBModel.SETTINGS_CREATE_SQL);
            _db.execSQL(DBModel.FILL_SETTINGS_SQL);
            _db.execSQL(DBModel.SECOND_FILL_SETTINGS_SQL);
            _db.execSQL(DBModel.THIRD_Fill_SETTINGS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
           _db.execSQL("ALTER TABLE " + DBModel.DATABASE_TABLE +
                       " ADD COLUMN " + DBModel.KEY_PRIORITY + " INTEGER");
           _db.execSQL("UPDATE " + DBModel.DATABASE_TABLE +
                       " SET " + DBModel.KEY_PRIORITY + " = 1 " +
                       " WHERE " + DBModel.KEY_PRIORITY + " IS NULL");
        }
    }
}

