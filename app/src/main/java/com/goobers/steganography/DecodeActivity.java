package com.goobers.steganography;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

public class DecodeActivity extends Activity {

    private static final String LOG_TAG = DecodeActivity.class.getSimpleName();

    private File image;
    private File decoded;
    private ImageView decodedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        decoded = new File(getCacheDir(), "temp.png");
        decodedView = (ImageView) findViewById(R.id.imageViewDecode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    public void decode(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
            }
            image = new File(selectedImagePath);
            decodedView.setImageBitmap(EncodeActivity.decodeBitmapScaledDown(decodedView, selectedImagePath));
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public static final String DECODED_FILE_PATH = "ENCODED FILE";

    public void nextPage(View v) {
        if (image == null) {
            Toast.makeText(getApplicationContext(), "You need to pick an image", Toast
                    .LENGTH_SHORT).show();
        } else {
            try {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_decode);
                progressBar.setProgress(0);
                int pink = R.color.fab_material_pink_500;
                progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(pink),
                        PorterDuff.Mode.SRC_IN);
                progressBar.getProgressDrawable().setColorFilter(getResources().getColor(pink),
                        PorterDuff.Mode.SRC_IN);
                new DecoderTask(this, progressBar).execute(image, decoded);
            } catch (OutOfMemoryError e) {
                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                        .LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
