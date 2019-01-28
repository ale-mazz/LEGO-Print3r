package printer.ev3printer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.util.Prelude;

public class CalibrationActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button penUp = findViewById(R.id.arrowUp);
        Button penDown = findViewById(R.id.arrowDown);
        Button penLeft = findViewById(R.id.arrowLeft);
        Button penRight = findViewById(R.id.arrowRight);
        Button dot = findViewById(R.id.dotButton);

        try {
            EV3 ev3 = new EV3(new BluetoothConnection("HAL9000").connect());

            penUp.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::verticalMotorUp)));
            penDown.setOnClickListener(v->Prelude.trap(() -> ev3.run(this::verticalMotorDown)));
            dot.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::dot)));

            penLeft.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        try {
                            ev3.run(this::goLeft);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        try {
                            ev3.run(this::stopPenMotor);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }

                private void goLeft(EV3.Api api) {
                    PrinterManager manager = new PrinterManager(api);
                    manager.KeepGoingLeft();
                }

                private void stopPenMotor (EV3.Api api){
                    PrinterManager manager = new PrinterManager(api);
                    manager.StopPenMotor();
                }
            });

            penRight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        try {
                            ev3.run(this::goRight);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        try {
                            ev3.run(this::stopPenMotor);
                        } catch (EV3.AlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }

                private void goRight(EV3.Api api) {
                    PrinterManager manager = new PrinterManager(api);
                    manager.KeepGoingRight();
                }

                private void stopPenMotor (EV3.Api api){
                    PrinterManager manager = new PrinterManager(api);
                    manager.StopPenMotor();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
