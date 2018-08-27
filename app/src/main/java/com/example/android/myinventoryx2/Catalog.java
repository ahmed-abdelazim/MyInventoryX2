package com.example.android.myinventoryx2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.myinventoryx2.data.Contract;

public class Catalog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID = 1;

    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton floatingActionButton = findViewById(R.id.add_item_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Catalog.this, Editor.class);
                startActivity(intent);
            }
        });

        ListView itemsList = findViewById(R.id.items_list);
        View emptyView = findViewById(R.id.empty_view);
        itemsList.setEmptyView(emptyView);

        adapter = new Adapter(this, null);
        itemsList.setAdapter(adapter);

        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri itemUri = ContentUris.withAppendedId(Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI, id);
                Intent intent = new Intent(Catalog.this, Editor.class);
                intent.setData(itemUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Contract.InventoryEntry._ID,
                Contract.InventoryEntry.COLUMN_ITEM_NAME,
                Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE,
                Contract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                Contract.InventoryEntry.COLUMN_ITEM_PRICE,
                Contract.InventoryEntry.COLUMN_ITEM_IMAGE_URI};
        return new CursorLoader(this, Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_items:
                insertDummyItem();
                return true;
            case R.id.action_add_item:
                Intent intent = new Intent(this, Editor.class);
                startActivity(intent);
                return true;
            case R.id.action_delete_all_items:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyItem() {
        String dummyName = getString(R.string.dummy_item_name);
        int dummyQuantity = 50;
        int dummyPrice = 100;
        String dummyEmail = getString(R.string.dummy_supplier_phone);
        String dummySupplier = getString(R.string.dummy_supplier_name);
        ContentValues values = new ContentValues();
        values.put(Contract.InventoryEntry.COLUMN_ITEM_NAME, dummyName);
        values.put(Contract.InventoryEntry.COLUMN_ITEM_QUANTITY, dummyQuantity);
        values.put(Contract.InventoryEntry.COLUMN_ITEM_PRICE, dummyPrice);
        values.put(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE, dummyEmail);
        values.put(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, dummySupplier);

        Uri addUri = getContentResolver().insert(Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI, values);
        long newRowId = ContentUris.parseId(addUri);
        if (newRowId != -1) {
            Toast.makeText(this, R.string.toast_item_added_successfully, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.inventory_menu_delete_all_items);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getContentResolver().delete(Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI, null, null);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
