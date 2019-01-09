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
    public boolean[] bwImageArray;
    public boolean[][] bidimensionalArray;
    public static int array_size = 40;
    public static int MAX_VALUE = 60;
    public static int MIN_VALUE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bidimensionalArray = new boolean[array_size][array_size];

        final Button selectImageButton = findViewById(R.id.imageSelectionButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        final Button testArrayButton = findViewById(R.id.testArrayButton);
        testArrayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                convertArray(bwImageArray);
                testArray(bidimensionalArray);
            }
        });

        final Button changeActivity = findViewById(R.id.changeActivity);
        changeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBooleanArray("boolArray", bwImageArray);
                Intent i = new Intent(GalleryActivity.this, TestActivity.class);
                i.putExtra("BitmapImage", convertedImageBitmap);
                i.putExtras(b);
                startActivity(i);
            }
        });

        TextView textViewSlider = findViewById(R.id.textViewSlider);


        SeekBar sb = findViewById(R.id.seekbar);
        //sb.setProgress(30);
        //sb.setMax(60);
        sb.setMax(MAX_VALUE - MIN_VALUE);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int value = progress + MIN_VALUE;
                textViewSlider.setText(String.valueOf("Dimensione: " + value));
                array_size = value;


            }
        });
    }

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
            bwImageArray = converter.readBitmapPixelsAsBooleans(convertedImageBitmap);

            return true;
        } else {
            return false;
        }
    }

    public void setImageView() {

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(imageSelectedBitmap);

        ImageView bwImageView = findViewById(R.id.bwImageView);
        bwImageView.setImageBitmap(convertedImageBitmap);
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


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void convertArray(boolean[] bwImageArray){
        int count = 0;

        for(int i = 0; i < array_size; i++){
            for (int j = 0; j < array_size; j++){
                bidimensionalArray[i][j] = bwImageArray[count];
                count++;
            }
        }
    }

    public void testArray(boolean[][] bidimensionalArray) {
        int blackDots = 0;
        int whiteDots = 0;
        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++){
                if (bidimensionalArray[i][j]) {
                    blackDots++;
                } else {
                    whiteDots++;
                }
            }
        }
        System.out.println();
        System.out.println("number of black pixels:" + blackDots);
        System.out.println("number of white pixels:" + whiteDots);
    }

}
