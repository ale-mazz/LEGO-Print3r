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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;

public class PrintPreviewActivity extends AppCompatActivity {


    //region Variables
    EV3Service mService;
    EV3 ev3;
    boolean mBound;

    public Bitmap imageSelectedBitmap;
    public Bitmap convertedImageBitmap;
    public Bitmap resizedImageBitmap;
    public Bitmap adjustedContrastBitmap;

    public boolean[] convertedImageBoolArray;
    public boolean[][] bidimensionalArray;
    public static int array_size = 40;
    public static int MAX_VALUE = 60;
    public static int MIN_VALUE = 30;
    private int brightness = 0;
    private static int MIN_BRIGHTNESS = -255;
    private static int MAX_BRIGHTNESS = 255;
    private int contrast = 1;
    private static int MIN_CONTRAST = 0;
    private static int MAX_CONTRAST = 10;
    public ImageView convertedImageView;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView textViewSlider = findViewById(R.id.textViewSlider);
        SeekBar dimensionSlider = findViewById(R.id.seekbar);
        SeekBar brightnessSlider = findViewById(R.id.brightnessBar);
        SeekBar contrastSlider = findViewById(R.id.contrastBar);

        final ImageView help = findViewById(R.id.helpPrintPreviewActivityButton);
        final Button printButton = findViewById(R.id.printButton);
        final Button caliberButton = findViewById(R.id.calibrationButton);

        help.setOnClickListener(v -> StartPrintPreviewHelpActivity());
        caliberButton.setOnClickListener(v -> StartCalibrationActivity());
        printButton.setOnClickListener(v -> SendBitmapAndArrayToNextActivity());

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
        brightnessSlider.setMax(500);
        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + MIN_BRIGHTNESS;
                brightness = value;
                if (convertBitmapToFinal()) {
                    setImageView();
                }
            }
        });
        contrastSlider.setMax(MAX_CONTRAST);
        contrastSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress;
                contrast = value;
                if (convertBitmapToFinal()) {
                    setImageView();
                }
            }
        });
    }

    // Passaggio EV3Service
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            ev3 = mService.GetBrick();
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
        // Creo intent per EV3Service
        Intent intent = new Intent(this, EV3Service.class);
        // Crea il bind con il service oppure crea prima il service se non presente
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop(){
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    //FUNZIONI UTILIZZATE

    public Bitmap getResizedBitmap(Bitmap bwImageSelectedBitmap, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(bwImageSelectedBitmap, bitmapWidth, bitmapHeight, true);
    }

    public boolean convertBitmapToFinal() {

        if (imageSelectedBitmap != null) {
            BitmapConverter converter = new BitmapConverter();
            //Faccio il resize della bitmap
            resizedImageBitmap = getResizedBitmap(imageSelectedBitmap, array_size, array_size);

            adjustedContrastBitmap = BitmapConverter.changeBitmapContrastBrightness(resizedImageBitmap, contrast, brightness);
            //Uso l'algoritmo di FS per convertire l'immagine
            convertedImageBitmap = com.askjeffreyliu.floydsteinbergdithering.Utils.floydSteinbergDithering(adjustedContrastBitmap);


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

    public void StartPrintPreviewHelpActivity(){
        Intent i = new Intent(PrintPreviewActivity.this, PrintPreviewActivityHelp.class);
        startActivity(i);
    }

    public void StartCalibrationActivity(){
        Intent i = new Intent(PrintPreviewActivity.this, CalibrationActivity.class);
        startActivity(i);
    }

    public void printArray(EV3.Api api) {
        PrinterManager manager = new PrinterManager(api);
        manager.PrintImage(InstructionBuilder.BuildInstructionListFromBitmap(bidimensionalArray, array_size, array_size));
    }


}
