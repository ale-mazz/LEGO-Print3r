package printer.ev3printer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;

    public ImageView galleryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        final Button selectImageButton = findViewById(R.id.imageSelectionButton);
        final Button convertActivity = findViewById(R.id.convertButton);

        galleryImageView = findViewById(R.id.normalImageView);


        // Bottone per entrare in galleria
        selectImageButton.setOnClickListener(v -> OpenGallery());

        // Cambia in activity per mandare in stampa
        convertActivity.setOnClickListener(v -> SendBitmapAndArrayToNextActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Controllo se il percorso dell'immagine selezionata è corretto e in quel caso lo passo dentro uri
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                //Seleziono effettivamente l'immagine dal percorso selezionato e la passo dentro un bitmap
                imageSelectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                setImageView();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // FUNZIONI


    public void setImageView() {
        galleryImageView.setImageBitmap(imageSelectedBitmap);
    }




    public void OpenGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
}
