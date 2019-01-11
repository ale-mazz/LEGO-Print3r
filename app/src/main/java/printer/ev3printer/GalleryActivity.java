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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;
    public Bitmap convertedImageBitmap;
    public Bitmap resizedImageBitmap;
    public boolean[] convertedImageBoolArray;
    public boolean[][] bidimensionalArray;
    public static int array_size = 40;
    public static int MAX_VALUE = 60;
    public static int MIN_VALUE = 30;

    public ImageView galleryImageView;
    public ImageView convertedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bidimensionalArray = new boolean[array_size][array_size];

        final Button selectImageButton = findViewById(R.id.imageSelectionButton);
        final Button testArrayButton = findViewById(R.id.testArrayButton);
        final Button changeActivity = findViewById(R.id.changeActivity);

        galleryImageView = findViewById(R.id.normalImageView);
        convertedImageView = findViewById(R.id.convertedImageView);

        TextView textViewSlider = findViewById(R.id.textViewSlider);

        SeekBar dimensionSlider = findViewById(R.id.seekbar);
        dimensionSlider.setMax(MAX_VALUE - MIN_VALUE);
        //slider overrides for dimension choice
        dimensionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + MIN_VALUE;
                textViewSlider.setText(String.valueOf("Dimensione: " + value));
                array_size = value;
                if (convertBitmapToFinal()) {
                    setImageView();
                }
            }
        });

        // Bottone per entrare in galleria
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenGallery();
            }
        });

        // Cambia in activity per mandare in stampa
        changeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendBitmapAndArrayToNextActivity();
            }
        });
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
                convertBitmapToFinal();
                setImageView();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // FUNZIONI
    public Bitmap getResizedBitmap(Bitmap bwImageSelectedBitmap, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(bwImageSelectedBitmap, bitmapWidth, bitmapHeight, true);
    }

    public boolean convertBitmapToFinal() {

        if (imageSelectedBitmap != null) {
            //Faccio il resize della bitmap
            resizedImageBitmap = getResizedBitmap(imageSelectedBitmap, array_size, array_size);
            //Uso l'algoritmo di FS per convertire l'immagine
            convertedImageBitmap = com.askjeffreyliu.floydsteinbergdithering.Utils.floydSteinbergDithering(resizedImageBitmap);

            BitmapConverter converter = new BitmapConverter();
            convertedImageBoolArray = converter.readBitmapPixelsAsBooleans(convertedImageBitmap);
            return true;
        } else {
            return false;
        }
    }

    public void setImageView() {
        galleryImageView.setImageBitmap(imageSelectedBitmap);
        convertedImageView.setImageBitmap(convertedImageBitmap);
    }


    public void SendBitmapAndArrayToNextActivity(){
        Bundle b = new Bundle();
        b.putBooleanArray("boolArray", convertedImageBoolArray);
        Intent i = new Intent(GalleryActivity.this, TestActivity.class);
        i.putExtra("BitmapImage", convertedImageBitmap);
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
