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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.util.Prelude;

public class PrintActivity extends AppCompatActivity {

    // Service ev3
    EV3Service mService;
    EV3 ev3;
    boolean mBound = false;

    boolean[] bwImageArray;
    Bitmap bwImageSelectedBitmap;
    boolean[][] biDimensionalArray;
    private static final String TAG = "PrintActivity";
    public int array_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        findViewById(R.id.circleBar).setVisibility(View.INVISIBLE);

        Button printButton = findViewById(R.id.printButton);
        ImageView bitmapImageView = findViewById(R.id.convertedImageView);


        SetOverlay();
        GetObjectsFromOtherActivity();
        bitmapImageView.setImageBitmap(bwImageSelectedBitmap);

        //printButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::printArray)));


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

            CheckForBluetoothConnection();
            mBound = true;

            Prelude.trap(() -> ev3.run(PrintActivity.this::printArray));


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

    public void printArray(EV3.Api api) {
        //Intent i = new Intent(PrintActivity.this, OngoingPrintActivity.class);
        PrinterManager manager = new PrinterManager(api);
        manager.PrintImage(InstructionBuilder.BuildInstructionListFromBitmap(biDimensionalArray, array_size, array_size));
        //startActivity(i);
    }

    public void GetObjectsFromOtherActivity(){
        Intent intent = getIntent();
        bwImageSelectedBitmap = intent.getParcelableExtra("BitmapImage");
        array_size = intent.getIntExtra("Array_size", 0);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            bwImageArray = b.getBooleanArray("boolArray");
        }
        biDimensionalArray = BitmapConverter.unidimensionalToBidimensional(bwImageArray, array_size);
    }
    private void CheckForBluetoothConnection(){
        if(!mService.isBluetoothAvailable() || mService.isBrickNull()){
            Intent i = new Intent(PrintActivity.this, BluetoothErrorActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void SetOverlay(){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
    }
}
