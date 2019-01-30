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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;

public class GalleryActivity extends AppCompatActivity {

    private EV3Service mService;
    private boolean mBound = false;

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;

    public ImageView galleryImageView;
    public static Uri uri;
    public boolean image_selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button selectImageButton = findViewById(R.id.imageSelectionButton);

        final Button convertButton = findViewById(R.id.convertButton);
        final ImageView help = findViewById(R.id.helpGalleryActivityButton);


        galleryImageView = findViewById(R.id.normalImageView);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(GalleryActivity.this, GalleryActivityHelp.class);
                startActivity(i);
            }
        });


        // Bottone per entrare in galleria

        //Enter gallery
        selectImageButton.setOnClickListener(v -> OpenGallery());

        //Enter PrintPreviewActivity
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
        if (image_selected)
        {
            Bundle b = new Bundle();
            Intent i = new Intent(GalleryActivity.this, PrintPreviewActivity.class);
            //Store variables into passed bundle
            i.putExtra("imagePath", uri.toString());
            i.putExtras(b);
            startActivity(i);
        }
        else
        {
            Toast.makeText(getBaseContext(), "Scegliere un'immagine" , Toast.LENGTH_SHORT ).show();
        }

    }
    public void OpenGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        image_selected = true;
    }

}
