package printer.ev3printer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.LightSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.util.Prelude;
import it.unive.dais.legodroid.lib.util.ThrowingConsumer;

public class MainActivity extends Activity {


    EV3Service mService;
    boolean mBound = false;
    EV3 ev3;
    BluetoothConnection btConnection;
    String legoBrickName = "HAL9000";

    private static final String TAG = Prelude.ReTAG("MainActivity");
    @Nullable
    private TachoMotor motor;
    private LightSensor lightSensor;


    private void applyMotor(@NonNull ThrowingConsumer<TachoMotor, Throwable> f) {
        if (motor != null)
            Prelude.trap(() -> f.call(motor));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button stopEverythingButton = findViewById(R.id.stopButton);
        Button stopWheelsButton = findViewById(R.id.stopWheelMotorButton);
        Button startButton = findViewById(R.id.startButton);

        // Pen motor buttons
        Button penKeepLeftButton = findViewById(R.id.penMotorKeepMovingLeftButton);
        Button penKeepRightButton = findViewById(R.id.penMotorKeepMovingRightButton);
        Button stopPenMotorButton = findViewById(R.id.stopPenMotorButton);

        // Vertical motor buttons
        Button stepUpVerticalButton = findViewById(R.id.stepUpVerticalMotorButton);
        Button stepDownVerticalButton = findViewById(R.id.stepDownVerticalMotorButton);
        Button stopVerticalMotorButton = findViewById(R.id.stopVerticalMotorButton);

        // Wheel motor buttons
        Button loadSheetButton = findViewById(R.id.loadSheetButton);
        Button unloadSheetButton = findViewById(R.id.unloadSheetButton);

        Button testSpeedButton = findViewById(R.id.testSpeedStatusButton);

        // TEST steps buttons
        Button stepForwardButton = findViewById(R.id.stepForwardButton);
        Button stepBackwardButton = findViewById(R.id.stepBackwardButton);
        Button stepRightButton = findViewById(R.id.stepRightButton);
        Button stepLeftButton = findViewById(R.id.stepLeftButton);
        Button dotButton = findViewById(R.id.dotButton);


        //try {
            // Connect to EV3 (HAL9000) via Bluetooth


            stopEverythingButton.setOnClickListener(v ->  mService.PrintBrickStatus());


            // Pen motor buttons
            penKeepLeftButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::keepMovePenMotorLeft)));
            penKeepRightButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::keepMovePenMotorRight)));
            stopPenMotorButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stopPenMotor)));

            // Vertical motor buttons
            stepUpVerticalButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::verticalMotorUp)));
            stepDownVerticalButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::verticalMotorDown)));
            stopVerticalMotorButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stopVerticalMotor)));

            // Wheel motor buttons
            loadSheetButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::loadSheet)));
            unloadSheetButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::unloadSheet)));
            stopWheelsButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stopWheelMotor)));
            testSpeedButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::testSpeed)));

            // Step buttons
            stepForwardButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stepForward)));
            stepBackwardButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stepBackward)));
            stepLeftButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stepLeft)));
            stepRightButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::stepRight)));
            dotButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::dot)));
            startButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::testDot)));




        /*} catch (IOException e) {
            Log.e(TAG, "Fatal error: cannot connect to HAL9000");
            e.printStackTrace();
        } */
    }
    @Override
    public void onStart(){
        super.onStart();
        // Bind to EV3 Service
        Intent intent = new Intent(this, EV3Service.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            if(!mService.isBrickNull()){
                ev3 = mService.GetBrick();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onStop(){
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    //FUNZIONI PER MUOVERE DESTRA SINISTRA IL MOTORE CON LA PENNA

    private void keepMovePenMotorLeft(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.KeepGoingLeft();
    }
    private void keepMovePenMotorRight(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.KeepGoingRight();
    }

    // FUNZIONI PER ALZARE/ABBASSARE IL VERTICAL MOTOR

    private void verticalMotorDown(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.LowerPen();
    }

    private void verticalMotorUp(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.RaisePen();
    }

    //FUNZIONI PER STOPPARE I MOTORI

    private void stopPenMotor(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.StopPenMotor();
    }
    private void stopVerticalMotor(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.StopVerticalMotor();
    }
    private void stopWheelMotor(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.StopWheelMotor();
    }

    //FUNZIONI DI CARICO/SCARICO DEL FOGLIO

    private void loadSheet(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.LoadSheet();
    }
    private void unloadSheet(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.UnloadSheet();
    }

    private void testSpeed(EV3.Api api){
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.StepMoveTest();
    }

    private void stepForward(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.StepForwardWheel(1);
    }

    private void stepBackward(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.StepBackwardWheel();
    }

    private void stepLeft(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.StepLeft(1);
    }

    private void stepRight(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.StepRight(1);
    }

    private void dot(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.DotDown();
        manager.DotUp();
    }

    private void testDot(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);

    }
}
