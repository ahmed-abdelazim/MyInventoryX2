package com.example.android.myinventoryx2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Person on 26/08/2018.
 */

public final class Contract {

    public static final String INVENTORY_CONTENT_AUTHORITY = "com.example.android.myinventoryx2";
    public static final Uri INVENTORY_BASE_CONTENT_URI = Uri.parse("content://" + INVENTORY_CONTENT_AUTHORITY);
    public static final String INVENTORY_PATH = "inventory";

    private Contract() {
    }

    public static class InventoryEntry implements BaseColumns {
        public static final String INVENTORY_CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + INVENTORY_CONTENT_AUTHORITY + "/" + INVENTORY_PATH;

        public static final String INVENTORY_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + INVENTORY_CONTENT_AUTHORITY + "/" + INVENTORY_PATH;

        public static final Uri INVENTORY_ENTRY_CONTENT_URI = Uri.withAppendedPath(INVENTORY_BASE_CONTENT_URI, INVENTORY_PATH);

        public static final String TABLE_NAME = "inventory";

        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_ITEM_SUPPLIER_MAIL = "supplier_mail";
        public static final String COLUMN_ITEM_IMAGE_URI = "image_uri";


    }
}
