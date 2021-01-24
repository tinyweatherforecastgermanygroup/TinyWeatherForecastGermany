package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class TextForecastContentProvider extends ContentProvider {

    static final String AUTHORITY = "de.kaffeemitkoffein.tinyweatherforecastgermany.texts";
    static final String DATASERVICE = "textforecasts";
    static final String URL_TEXTDATA = "content://" + AUTHORITY + "/" + DATASERVICE;
    static final Uri URI_TEXTDATA = Uri.parse(URL_TEXTDATA);

    private TextForecastContentProvider.TextForecastDatabaseHelper textForecastDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        textForecastDatabaseHelper = new TextForecastContentProvider.TextForecastDatabaseHelper(getContext().getApplicationContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        sqLiteDatabase = textForecastDatabaseHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.query(TextForecastContentProvider.TextForecastDatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        sqLiteDatabase = textForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
        sqLiteDatabase.insert(TextForecastContentProvider.TextForecastDatabaseHelper.TABLE_NAME,null,contentValues);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int i = 0;
        sqLiteDatabase = textForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
        i = sqLiteDatabase.delete(TextForecastContentProvider.TextForecastDatabaseHelper.TABLE_NAME,selection,selectionArgs);
        return i;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        sqLiteDatabase = textForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
        return sqLiteDatabase.update(TextForecastContentProvider.TextForecastDatabaseHelper.TABLE_NAME,contentValues,selection,selectionArgs);
    }

    public static class TextForecastDatabaseHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "textforecasts";
        public static final String TABLE_NAME = "tables";
        public static final String KEY_id = "id";
        public static final String KEY_identifier = "identifier";
        public static final String KEY_content = "content";
        public static final String KEY_title = "title";
        public static final String KEY_subtitle = "subtitle";
        public static final String KEY_issued_text = "issued_text";
        public static final String KEY_type = "type";
        public static final String KEY_issued = "issued";
        public static final String KEY_polled = "polled";
        public static final String KEY_outdated = "outdated";

        public static final String SQL_COMMAND_CREATE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_id + " INTEGER PRIMARY KEY ASC,"
                + KEY_identifier + " TEXT,"
                + KEY_content + " TEXT,"
                + KEY_title + " TEXT,"
                + KEY_subtitle + " TEXT,"
                + KEY_issued_text + " TEXT,"
                + KEY_type + " INTEGER,"
                + KEY_issued + " INTEGER,"
                + KEY_polled+ " INTEGER,"
                + KEY_outdated + " INTEGER"
                + ");";

        public static final String SQL_COMMAND_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public TextForecastDatabaseHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_COMMAND_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_COMMAND_DROP_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    private int booleanToInt(boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }

    private boolean intToBoolean(int i) {
        if (i == 0) {
            return false;
        }
        return true;
    }

    public ContentValues getContentValuesFromTextForecast(TextForecast textForecast) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TextForecastDatabaseHelper.KEY_identifier,textForecast.identifier);
        contentValues.put(TextForecastDatabaseHelper.KEY_content,textForecast.content);
        contentValues.put(TextForecastDatabaseHelper.KEY_title,textForecast.title);
        contentValues.put(TextForecastDatabaseHelper.KEY_subtitle,textForecast.subtitle);
        contentValues.put(TextForecastDatabaseHelper.KEY_issued_text,textForecast.issued_text);
        contentValues.put(TextForecastDatabaseHelper.KEY_type,textForecast.type);
        contentValues.put(TextForecastDatabaseHelper.KEY_issued,textForecast.issued);
        contentValues.put(TextForecastDatabaseHelper.KEY_polled,textForecast.polled);
        contentValues.put(TextForecastDatabaseHelper.KEY_outdated,textForecast.outdated);
        return contentValues;
    }

    public TextForecast getTextForecastFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            TextForecast textForecast = new TextForecast();
            textForecast.identifier = c.getString(c.getColumnIndex(TextForecastDatabaseHelper.KEY_identifier));
            textForecast.content = c.getString(c.getColumnIndex(TextForecastDatabaseHelper.KEY_content));
            textForecast.title = c.getString(c.getColumnIndex(TextForecastDatabaseHelper.KEY_title));
            textForecast.subtitle = c.getString(c.getColumnIndex(TextForecastDatabaseHelper.KEY_subtitle));
            textForecast.issued_text = c.getString(c.getColumnIndex(TextForecastDatabaseHelper.KEY_issued_text));
            textForecast.type = c.getInt(c.getColumnIndex(TextForecastDatabaseHelper.KEY_type));
            textForecast.issued = c.getLong(c.getColumnIndex(TextForecastDatabaseHelper.KEY_issued));
            textForecast.polled = c.getLong(c.getColumnIndex(TextForecastDatabaseHelper.KEY_polled));
            textForecast.outdated = c.getInt(c.getColumnIndex(TextForecastDatabaseHelper.KEY_outdated))>0;
            return textForecast;
        }
    }

    public void writeTextForecast(Context c,TextForecast textForecast){
        ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
        contentResolver.insert(TextForecastContentProvider.URI_TEXTDATA,getContentValuesFromTextForecast(textForecast));
    }

}
