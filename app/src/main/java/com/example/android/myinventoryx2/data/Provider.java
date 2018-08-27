package com.example.android.myinventoryx2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.myinventoryx2.data.Contract.InventoryEntry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Person on 26/08/2018.
 */

public class Provider extends ContentProvider {

    private static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(Contract.INVENTORY_CONTENT_AUTHORITY, Contract.INVENTORY_PATH, INVENTORY);
        uriMatcher.addURI(Contract.INVENTORY_CONTENT_AUTHORITY, Contract.INVENTORY_PATH + "/#", INVENTORY_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.INVENTORY_CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.INVENTORY_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        if (checkContentValues(values)) {
            switch (match) {
                case INVENTORY:
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
                    if (id == -1) {
                        Log.e(LOG_TAG, "Failed to insert row for " + uri);
                        return null;
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, id);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateItem(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        int rowsUpdated = 0;
        if (checkContentValues(values)) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsUpdated;
    }

    private boolean checkContentValues(ContentValues values) {
        try {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Inventory requires a name");
            }

            Double price = values.getAsDouble(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Price must be a positive number");
            }

            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity must be a positive number");
            }

            String phoneNumber = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE);
            if (phoneNumber == null || !validatePhone(phoneNumber)) {
                throw new IllegalArgumentException("Phone must be entered and should be valid");
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePhone(String phoneNumber) {
        if (phoneNumber != null) {
            Pattern regexPattern = Pattern.compile("^[+]?[0-9]{10,13}$");
            Matcher regexMatcher = regexPattern.matcher(phoneNumber);
            return regexMatcher.matches();
        }
        return false;
    }

}
