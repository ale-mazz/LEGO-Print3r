package printer.ev3printer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private EV3Service mService;
    private boolean mBound = false;

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;

    public ImageView galleryImageView;
    public static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final Button openGalleryButton = findViewById(R.id.imageSelectionButton);
        final Button convertButton = findViewById(R.id.convertButton);
        final ImageView helpImageButton = findViewById(R.id.helpGalleryActivityButton);

        galleryImageView = findViewById(R.id.normalImageView);

        helpImageButton.setOnClickListener(v -> OpenGalleryActivityHelp());
        openGalleryButton.setOnClickListener(v -> OpenGallery());
        convertButton.setOnClickListener(v -> SendBitmapToNextActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if selected image path is correct and store it to uri variable
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                //Select image on the selected URI and store it into bitmap
                imageSelectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                setImageView();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //region EV3Service connection
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, EV3Service.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    //endregion

    public void setImageView() {
        galleryImageView.setImageBitmap(imageSelectedBitmap);
    }
    public void SendBitmapToNextActivity() {
        if (uri != null)
        {
            Bundle b = new Bundle();
            Intent i = new Intent(GalleryActivity.this, PrintPreviewActivity.class);
            //Store variables into passed bundle
            i.putExtra("imagePath", uri.toString());
            i.putExtras(b);
            startActivity(i);
        }
        else {
            StyleableToast.makeText(this, "Selezionare un'immagine prima di procedere.", R.style.galleryToast).show();
        }

    }
    public void OpenGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void OpenGalleryActivityHelp(){
        Intent i = new Intent(GalleryActivity.this, GalleryActivityHelp.class);
        startActivity(i);
    }
}
