package pro.salebot.mobileclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SteveRay on 01.03.2017.
 */

public class DataBaseParams extends SQLiteOpenHelper {

    public static final String KEY_LOGIN = "KEY_LOGIN";
    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_ID_PROJECT = "KEY_ID_PROJECT";
    public static final String KEY_ID_NOTIFICATION = "KEY_ID_NOTIFICATION";
    public static final String KEY_RUN_NOTIF = "KEY_RUN_NOTIF";

    private static final int TABLE_VERSION = 1;
    private static final String TABLE_NAME = "dbsettings";

    private static final String ID = "_id";
    private static final String NAME = "keynamedb";
    private static final String VALUE = "keyvaluedb";

    public DataBaseParams(Context context) {
        super(context, TABLE_NAME, null, TABLE_VERSION);
    }

    //todo Создание базы
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "("+
                ID+" integer primary key, "+
                NAME+" text, "+
                VALUE+" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public void setKey(String key, String value) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DataBaseParams.VALUE, value);

            Cursor cursor = db.rawQuery("SELECT * FROM " + DataBaseParams.TABLE_NAME + " WHERE " + DataBaseParams.NAME + "='" + key + "'", null);

            //если запись есть то обновляем, если нет то добавляем
            if (cursor.moveToFirst()) {
                db.update(DataBaseParams.TABLE_NAME, cv, DataBaseParams.NAME + "='" + key + "'", null);
            } else {
                cv.put(DataBaseParams.NAME, key);
                db.insert(DataBaseParams.TABLE_NAME, null, cv);
            }

            cursor.close();
            db.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void setKey(String key, int value) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DataBaseParams.VALUE, String.valueOf(value));

            Cursor cursor = db.rawQuery("SELECT * FROM " + DataBaseParams.TABLE_NAME + " WHERE " + DataBaseParams.NAME + "='" + key + "'", null);

            //если запись есть то обновляем, если нет то добавляем
            if (cursor.moveToFirst()) {
                db.update(DataBaseParams.TABLE_NAME, cv, DataBaseParams.NAME + "='" + key + "'", null);
            } else {
                cv.put(DataBaseParams.NAME, key);
                db.insert(DataBaseParams.TABLE_NAME, null, cv);
            }

            cursor.close();
            db.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public String getKey(String key) {

        Cursor cursor = null;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + DataBaseParams.TABLE_NAME + " WHERE " + DataBaseParams.NAME + "='" + key + "';", null);

            String typeEnter;
            if (cursor.moveToFirst()) {
                typeEnter = cursor.getString(cursor.getColumnIndex(DataBaseParams.VALUE));
                cursor.close();
                return typeEnter;
            } else {
                cursor.close();
                return null;
            }

        } catch (SQLiteDatabaseLockedException e) {
            if (cursor != null) {
                cursor.close();
            }
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteKey (String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+DataBaseParams.TABLE_NAME+" where "+DataBaseParams.NAME+"='"+key+"';");
    }
}