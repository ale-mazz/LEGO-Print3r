package printer.ev3printer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.util.Prelude;

public class PrintActivity extends AppCompatActivity {

    // Service ev3
    EV3Service mService;
    EV3 ev3;
    boolean mBound = false;

    boolean[] bwImageArray;
    Bitmap bwImageSelectedBitmap;
    boolean[][] biDimensionalArray;
    private static final String TAG = Prelude.ReTAG("MainActivity");
    public int array_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        findViewById(R.id.circleBar).setVisibility(View.INVISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button printButton = findViewById(R.id.printButton);

        Intent intent = getIntent();
        bwImageSelectedBitmap = intent.getParcelableExtra("BitmapImage");
        array_size = intent.getIntExtra("Array_size", 0);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            bwImageArray = b.getBooleanArray("boolArray");
        }

        ImageView bitmapImageView = findViewById(R.id.convertedImageView);
        bitmapImageView.setImageBitmap(bwImageSelectedBitmap);



        printButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::printArray)));
        //printButton.setOnClickListener(v -> findViewById(R.id.circleBar).setVisibility(View.VISIBLE));

        biDimensionalArray = BitmapConverter.unidimensionalToBidimensional(bwImageArray, array_size);

    }

    //region EV3Service connection
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
    //endregion


    public void printArray(EV3.Api api) {
        PrinterManager manager = new PrinterManager(api);
        manager.PrintImage(InstructionBuilder.BuildInstructionListFromBitmap(biDimensionalArray, array_size, array_size));
    }
}
