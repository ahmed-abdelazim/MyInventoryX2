package com.example.android.myinventoryx2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.myinventoryx2.data.Contract.InventoryEntry;

/**
 * Created by Person on 26/08/2018.
 */

public class DbHelper extends SQLiteOpenHelper {


    private static final String INVENTORY_DB_NAME = "inventory.db";

    private static final int INVENTORY_DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, INVENTORY_DB_NAME, null, INVENTORY_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_PRICE + " DOUBLE NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_IMAGE_URI + " TEXT" + ");";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

