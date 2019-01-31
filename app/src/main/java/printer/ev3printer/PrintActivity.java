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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.util.Prelude;

public class PrintActivity extends AppCompatActivity {

    // Service EV3
    EV3Service mService;
    EV3 ev3;
    boolean mBound = false;

    boolean[] bwImageArray;
    Bitmap bwImageSelectedBitmap;
    TextView statusText;
    ProgressBar circleBar;
    ImageView bitmapImageView;
    Button homeButton;

    boolean[][] biDimensionalArray;
    public int array_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        bitmapImageView = findViewById(R.id.convertedImageView);
        statusText = findViewById(R.id.printStatusText);
        circleBar = findViewById(R.id.circleBar);
        homeButton = findViewById(R.id.homeButton);
        homeButton.setVisibility(View.GONE);

        SetOverlay();
        GetObjectsFromOtherActivity();
        bitmapImageView.setImageBitmap(bwImageSelectedBitmap);
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
        PrinterManager manager = new PrinterManager(api);
        boolean sheetPresent = manager.IsSheetLoaded();
        if(sheetPresent){
            runOnUiThread(() -> OnPrintingUI());
            boolean printed = manager.PrintImage(InstructionBuilder.BuildInstructionListFromBitmap(biDimensionalArray, array_size, array_size));
            if(printed){
                runOnUiThread(() -> OnSuccessUI());
            }
        } else {
            runOnUiThread(() -> OnSheetNotPresentUI());
        }
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

    public void OnSuccessUI(){
        circleBar.setVisibility(View.GONE);
        statusText.setText(String.valueOf("SUCCESSO"));
        bitmapImageView.setImageResource(R.drawable.success);
        homeButton.setVisibility(View.VISIBLE);
        bitmapImageView.setBackgroundResource(R.drawable.solidWhite);
    }

    public void OnSheetNotPresentUI(){
        homeButton.setVisibility(View.GONE);
        statusText.setText(String.valueOf("NON C'E' IL FOGLIO"));
        circleBar.setVisibility(View.GONE);
    }

    public void OnPrintingUI(){
        homeButton.setVisibility(View.GONE);
        statusText.setText(String.valueOf("STAMPA IN CORSO"));
        circleBar.setVisibility(View.VISIBLE);
    }

}
