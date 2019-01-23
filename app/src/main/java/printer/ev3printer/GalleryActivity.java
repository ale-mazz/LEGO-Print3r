package printer.ev3printer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;
    public ImageView galleryImageView;
    public static Uri uri;

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

    // FUNCTIONS

    public void setImageView() {
        galleryImageView.setImageBitmap(imageSelectedBitmap);
    }

    public void SendBitmapToNextActivity() {
        Bundle b = new Bundle();
        Intent i = new Intent(GalleryActivity.this, PrintPreviewActivity.class);
        //Store variables into passed bundle
        i.putExtra("imagePath", uri.toString());
        i.putExtras(b);
        startActivity(i);
    }

    public void OpenGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
}
