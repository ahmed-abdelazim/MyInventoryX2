package com.example.android.myinventoryx2;
import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.content.CursorLoader;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryx2.data.Contract;

public class Editor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int LOADER_ID = 102;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_ENABLE_READ = 1002;

    private Uri currentItemUri;

    private EditText nameEditText;
    private EditText supplierNameEditText;
    private EditText supplierMailEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private ImageView itemImage;
    private Uri itemImageUri;
    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;
    private TextView orderMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        currentItemUri = getIntent().getData();

        if (currentItemUri != null) {
            setTitle(R.string.details_edit_item);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            setTitle(R.string.details_add_item);
        }

        nameEditText = findViewById(R.id.name_edit_text);
        supplierNameEditText = findViewById(R.id.supplier_edit_text);
        supplierMailEditText = findViewById(R.id.supplier_mail_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        itemImage = findViewById(R.id.item_image);
        increaseQuantityButton = findViewById(R.id.increase_button);
        decreaseQuantityButton = findViewById(R.id.decrease_button);
        orderMoreView = findViewById(R.id.order_more_view);

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer currentCount = 0;
                if (!TextUtils.isEmpty(quantityEditText.getText())) {
                    currentCount = Integer.parseInt(quantityEditText.getText().toString().trim());
                }
                currentCount++;
                quantityEditText.setText(String.valueOf(currentCount));
            }
        });

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(quantityEditText.getText())) {
                    Integer currentCount = Integer.parseInt(quantityEditText.getText().toString().trim());
                    if (currentCount > 0) {
                        currentCount--;
                        quantityEditText.setText(String.valueOf(currentCount));
                    } else {
                        Toast.makeText(Editor.this, R.string.toast_item_quantity_negative, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(Editor.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == 0) {
                    startImagePicker();
                } else {
                    ActivityCompat.requestPermissions(Editor.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ENABLE_READ);
                }
            }
        });

        orderMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, supplierNameEditText.getText().toString());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_more_item, nameEditText.getText().toString()));
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Editor.this, R.string.no_mail_client_installed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ENABLE_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker();
            } else {
                Toast.makeText(this, R.string.error_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, currentItemUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            String itemImageUri = data.getString(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_IMAGE_URI));
            String itemName = data.getString(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_NAME));
            String supplierName = data.getString(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME));
            final String supplierEmail = data.getString(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_MAIL));
            final Integer quantity = data.getInt(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_QUANTITY));
            Double price = data.getDouble(data.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_PRICE));

            if (itemImageUri != null) {
                itemImage.setImageURI(Uri.parse(itemImageUri));
            }

            nameEditText.setText(itemName);
            supplierNameEditText.setText(supplierName);
            supplierMailEditText.setText(supplierEmail);
            quantityEditText.setText(String.valueOf(quantity));
            priceEditText.setText(String.valueOf(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.getText().clear();
        supplierNameEditText.getText().clear();
        supplierMailEditText.getText().clear();
        quantityEditText.getText().clear();
        priceEditText.getText().clear();
        itemImage.setImageResource(R.drawable.no_product);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveItem();
                return true;
            case R.id.menu_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.details_menu_item_delete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getContentResolver().delete(currentItemUri, null, null);
                finish();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveItem() {
        if (showError()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Contract.InventoryEntry.COLUMN_ITEM_NAME, nameEditText.getText().toString().trim());
        values.put(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, supplierNameEditText.getText().toString().trim());
        values.put(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_MAIL, supplierMailEditText.getText().toString().trim());
        if (!TextUtils.isEmpty(quantityEditText.getText())) {
            values.put(Contract.InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(quantityEditText.getText().toString().trim()));
        }
        values.put(Contract.InventoryEntry.COLUMN_ITEM_PRICE, Double.parseDouble(priceEditText.getText().toString().trim()));
        if (itemImageUri != null) {
            values.put(Contract.InventoryEntry.COLUMN_ITEM_IMAGE_URI, itemImageUri.toString());
        }

        if (currentItemUri == null) {
            Uri newItemUri = getContentResolver().insert(Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI, values);
            if (newItemUri != null) {
                Toast.makeText(this, R.string.toast_item_added_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.toast_item_error_adding, Toast.LENGTH_SHORT).show();
            }
        } else {
            int updatedRows = getContentResolver().update(currentItemUri, values, null, null);
            if (updatedRows > 0) {
                Toast.makeText(this, R.string.toast_item_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.toast_item_error_update, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean showError() {
        int errors = 0;
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError(getString(R.string.error_required));
            errors++;
        } else if (TextUtils.isEmpty(supplierMailEditText.getText())) {
            supplierMailEditText.setError(getString(R.string.error_required));
            errors++;
        } else if (TextUtils.isEmpty(priceEditText.getText())) {
            priceEditText.setError(getString(R.string.error_required));
            errors++;
        } else if (TextUtils.isEmpty(supplierNameEditText.getText())) {
            supplierNameEditText.setError(getString(R.string.error_required));
            errors++;
        } else if (TextUtils.isEmpty(quantityEditText.getText()) || Integer.parseInt(quantityEditText.getText().toString().trim()) < 0) {
            quantityEditText.setError(getString(R.string.error_required));
            errors++;
        } else if (itemImage.getDrawable() == null) {
            itemImage.setImageResource(R.drawable.no_product);
            errors++;
        }
        return errors > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data == null && data.getData() == null) {
                Toast.makeText(this, R.string.toast_image_error_retrieve, Toast.LENGTH_SHORT).show();
                return;
            }
            itemImageUri = data.getData();
            itemImage.setImageURI(data.getData());
        }
    }

}
