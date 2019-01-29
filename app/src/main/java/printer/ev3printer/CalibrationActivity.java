package printer.ev3printer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.util.Prelude;

public class CalibrationActivity extends AppCompatActivity {

    EV3 ev3;
    boolean mBound;
    EV3Service mService;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent i = new Intent(CalibrationActivity.this, CalibrationActivityHelp.class);
        startActivity(i);

        Button penUp = findViewById(R.id.arrowUp);
        Button penDown = findViewById(R.id.arrowDown);
        Button penLeft = findViewById(R.id.arrowLeft);
        Button penRight = findViewById(R.id.arrowRight);
        Button dot = findViewById(R.id.dotButton);

            //moves the pen one step up when the button is pressed
            penUp.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::verticalMotorUp)));
            //move the pen one step down when the button is pressed
            penDown.setOnClickListener(v->Prelude.trap(() -> ev3.run(this::verticalMotorDown)));
            //test dot to calibrate the printer
            dot.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::dot)));

            //keeps moving the pen left while the button is pressed, then stops when it's released

            penLeft.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){  //if the button is being pressed
                        try {
                            ev3.run(CalibrationActivity.this::goLeft);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP){  //if it gets released
                        try {
                            ev3.run(CalibrationActivity.this::stopPenMotor);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }

            });

            //keeps moving the pen right while the button is pressed, then stops when it's released

            penRight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {             //if the button is pressed
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        try {
                            ev3.run(CalibrationActivity.this::goRight);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }                                                           //if it gets released
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        try {
                            ev3.run(CalibrationActivity.this::stopPenMotor);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            ev3 = mService.GetBrick();
            mBound = true;

            /*EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            ev3 = mService.GetBrick();
            try{
                if(!ev3.isCancelled())
                    System.out.println("I'm calling the ev3 method.");
                ev3.run(CalibrationActivity.this::loadSheet);
            } catch (EV3.AlreadyRunningException e){
                Log.e("EV3", "EV3 task is already running.");
            }*/

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

    private void goRight(EV3.Api api) {
        PrinterManager manager = new PrinterManager(api);
        manager.KeepGoingRight();
    }

    private void goLeft(EV3.Api api) {
        PrinterManager manager = new PrinterManager(api);
        manager.KeepGoingLeft();
    }

    private void stopPenMotor (EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.StopPenMotor();
    }

    private void verticalMotorUp(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.RaisePen();
    }

    private void verticalMotorDown(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.LowerPen();
    }

    private void dot(EV3.Api api){
        PrinterManager manager = new PrinterManager(api);
        manager.Dot();
    }

    private void loadSheet(EV3.Api api) {
        Log.d("Load sheet method: ","Loading SHEET started.");
        PrinterManager printerManager = new PrinterManager(api);
        try{
            printerManager.LoadSheetWithButton();
        } catch(IOException e){

        } catch (ExecutionException e){

        } catch(InterruptedException e){

        }
    }
}
