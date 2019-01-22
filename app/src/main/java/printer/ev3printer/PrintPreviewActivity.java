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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class PrintPreviewActivity extends AppCompatActivity {

    public Bitmap imageSelectedBitmap;
    public Bitmap convertedImageBitmap;
    public Bitmap resizedImageBitmap;
    public boolean[] convertedImageBoolArray;
    public boolean[][] bidimensionalArray;
    public static int array_size = 40;
    public static int MAX_VALUE = 60;
    public static int MIN_VALUE = 30;
    public ImageView convertedImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        String image_path = intent.getStringExtra("imagePath");
        Uri fileUri = Uri.parse(image_path);
        try {
            System.out.println(fileUri.toString());
            imageSelectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        convertedImageView = findViewById(R.id.convertedImageView);
        bidimensionalArray = new boolean[array_size][array_size];
        convertBitmapToFinal();
        setImageView();

        TextView textViewSlider = findViewById(R.id.textViewSlider);

        SeekBar dimensionSlider = findViewById(R.id.seekbar);

        Button printButton = findViewById(R.id.printButton);

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
        printButton.setOnClickListener(v -> SendBitmapAndArrayToNextActivity());

    }

    //FUNZIONI UTILIZZATE

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
        convertedImageView.setImageBitmap(convertedImageBitmap);
    }

    public void SendBitmapAndArrayToNextActivity() {
        Bundle b = new Bundle();
        b.putBooleanArray("boolArray", convertedImageBoolArray);
        Intent i = new Intent(PrintPreviewActivity.this, PrintActivity.class);
        // Inserisco variabili all'interno del bundle da passare
        i.putExtra("BitmapImage", convertedImageBitmap);
        i.putExtra("Array_size", array_size);
        i.putExtras(b);

        startActivity(i);
    }


}
