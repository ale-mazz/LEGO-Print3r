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

    private Bitmap imageSelectedBitmap;
    private Bitmap convertedImageBitmap;
    private Bitmap resizedImageBitmap;
    private Bitmap adjustedContrastBitmap;

    private boolean[] convertedImageBoolArray;
    private boolean[][] bidimensionalArray;
    private static int array_size = 40;
    private static int MAX_ARRAY_SIZE = 60;
    private static int MIN_ARRAY_SIZE = 30;
    private int brightness = 0;
    private static int MIN_BRIGHTNESS = -255;
    private static int MAX_BRIGHTNESS = 255;
    private int contrast = 1;
    private static int MIN_CONTRAST = 0;
    private static int MAX_CONTRAST = 10;

    //public ImageView convertedImageView;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //region UI item declarations
        TextView dimensionText = findViewById(R.id.dimensionTextView);
        TextView contrastText = findViewById(R.id.contrastSliderText);
        TextView brightnessText = findViewById(R.id.brightnessTextView);
        SeekBar dimensionSlider = findViewById(R.id.dimensionSeekbar);
        SeekBar brightnessSlider = findViewById(R.id.brightnessSeekBar);
        SeekBar contrastSlider = findViewById(R.id.contrastSeekBar);

        final ImageView helpImage = findViewById(R.id.helpPrintPreviewActivityButton);
        final Button printButton = findViewById(R.id.printButton);
        final Button calibrationButton = findViewById(R.id.calibrationButton);
        ImageView convertedImageView = findViewById(R.id.convertedImageView);
        //endregion

        helpImage.setOnClickListener(v -> StartPrintPreviewHelpActivity());
        calibrationButton.setOnClickListener(v -> StartCalibrationActivity());
        printButton.setOnClickListener(v -> GoToPrint());

        GetImageFromPreviousActivity();
        setImageView(convertedImageView);

        //region Sliders
        dimensionSlider.setMax(MAX_ARRAY_SIZE - MIN_ARRAY_SIZE);
        dimensionSlider.setProgress(array_size - MIN_ARRAY_SIZE);
        dimensionText.setText(String.valueOf("Dimensione: " + (array_size - MIN_ARRAY_SIZE)));
        dimensionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + MIN_ARRAY_SIZE;
                dimensionText.setText(String.valueOf("Dimensione: " + value));
                array_size = value;
                if (convertBitmapToFinal()) {
                    setImageView(convertedImageView);
                }
            }
        });

        brightnessSlider.setMax(MAX_BRIGHTNESS - MIN_BRIGHTNESS);
        brightnessSlider.setProgress(MAX_BRIGHTNESS);
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
                brightnessText.setText(String.valueOf("Luminosità: " + value));
                brightness = value;
                if (convertBitmapToFinal()) {
                    setImageView(convertedImageView);
                }
            }
        });

        contrastSlider.setMax(MAX_CONTRAST);
        contrastSlider.setProgress(MIN_CONTRAST + 1);
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
                contrastText.setText(String.valueOf("Contrasto: " + value));
                contrast = value;
                if (convertBitmapToFinal()) {
                    setImageView(convertedImageView);
                }
            }
        });
        //endregion
    }

    //region EV3Service
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
        Intent intent = new Intent(this, EV3Service.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop(){
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }
    //endregion

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

    public void setImageView(ImageView imageView) {
        imageView.setImageBitmap(convertedImageBitmap);
    }

    public void GetImageFromPreviousActivity(){
        Intent imageIntent = getIntent();
        String image_path = imageIntent.getStringExtra("imagePath");
        Uri fileUri = Uri.parse(image_path);
        try {
            System.out.println(fileUri.toString());
            imageSelectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bidimensionalArray = new boolean[array_size][array_size];
        convertBitmapToFinal();
    }

    public void SendBitmapAndArrayToNextActivity() {

        Bundle b = new Bundle();
        b.putBooleanArray("boolArray", convertedImageBoolArray);
        Intent i = new Intent(PrintPreviewActivity.this, PrintActivity.class);
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
        if(CheckForBluetoothConnection()){
            Intent i = new Intent(PrintPreviewActivity.this, CalibrationActivity.class);
            startActivity(i);
        }

    }
    private boolean CheckForBluetoothConnection(){
        if(!mService.isBluetoothAvailable() || mService.isBrickNull()){
            OpenBTErrorActivity();
            return false;
        } else {
            return true;
        }
    }

    private void GoToPrint(){
        if(CheckForBluetoothConnection()){
            SendBitmapAndArrayToNextActivity();
        }
    }

    private void OpenBTErrorActivity(){
        Intent i = new Intent(PrintPreviewActivity.this, BluetoothErrorActivity.class);
        startActivity(i);
        finish();
    }
}
