package printer.ev3printer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.util.Prelude;

public class TestActivity extends AppCompatActivity {

    boolean[] bwImageArray;
    Bitmap bwImageSelectedBitmap;
    boolean[][] biDimensionalArray;
    private static final String TAG = Prelude.ReTAG("MainActivity");
    public int array_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button printButton = findViewById(R.id.printButton);
        Button cancelButton = findViewById(R.id.cancelPrintButton);

        Intent intent = getIntent();
        bwImageSelectedBitmap = intent.getParcelableExtra("BitmapImage");
        array_size = intent.getIntExtra("Array_size", 0);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            bwImageArray = b.getBooleanArray("boolArray");
        }

        ImageView bitmapImageView = findViewById(R.id.convertedImageView);
        bitmapImageView.setImageBitmap(bwImageSelectedBitmap);


        try {
            EV3 ev3 = new EV3(new BluetoothConnection("HAL9000").connect());

            printButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::printArray)));
            cancelButton.setOnClickListener(v -> Prelude.trap(() -> ev3.cancel()));

            biDimensionalArray = BitmapConverter.unidimensionalToBidimensional(bwImageArray, array_size);

        } catch (IOException e) {
            Log.e(TAG, "Fatal error: cannot connect to HAL9000");
            e.printStackTrace();
        }

    }


    public void printArray(EV3.Api api) {
        PrinterManager manager = new PrinterManager(api);
        manager.PrintImage(InstructionBuilder.BuildInstructionListFromBitmap(biDimensionalArray, array_size, array_size));

    }
}