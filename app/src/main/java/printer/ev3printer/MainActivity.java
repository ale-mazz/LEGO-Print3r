package printer.ev3printer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.LightSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.util.Prelude;
import it.unive.dais.legodroid.lib.util.ThrowingConsumer;

public class MainActivity extends AppCompatActivity {

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


        Button startButton = findViewById(R.id.startButton);
        Button middleButton = findViewById(R.id.middleButton);
        Button stopEverythingButton = findViewById(R.id.stopButton);
        Button brakeButton = findViewById(R.id.brakeButton);
        Button leftButton = findViewById(R.id.leftButton);
        Button rightButton = findViewById(R.id.rightButton);
        Button upBackButton = findViewById(R.id.upBackMotorButton);
        Button downBackButton = findViewById(R.id.downBackMotorButton);
        Button brakeBackButtonMountain = findViewById(R.id.brakeBackButton);

        try {
            // Connect to EV3 (HAL9000) via Bluetooth
            EV3 ev3 = new EV3(new BluetoothConnection("HAL9000").connect());

            stopEverythingButton.setOnClickListener(v -> ev3.cancel());
            startButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::legoMain)));
            middleButton.setOnClickListener( v-> Prelude.trap(() -> ev3.run(this::legoStop)));
            brakeButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::unloadSheet)));
            leftButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::legoLeft)));
            rightButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::legoRight)));
            upBackButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::backMotorUp)));
            downBackButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::backMotorDown)));
            brakeBackButtonMountain.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::loadSheet)));

        } catch (IOException e) {
            Log.e(TAG, "Fatal error: cannot connect to HAL9000");
            e.printStackTrace();
        }
    }



    private void legoMain(EV3.Api api) {

        final String TAG = Prelude.ReTAG("legoMain");
        motor = api.getTachoMotor(EV3.OutputPort.A);
        try {
            applyMotor(TachoMotor::resetPosition);
            try {
                motor.setSpeed(-10);
                motor.start();
                Future<Float> speed = motor.getSpeed();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        } finally {
            //applyMotor(TachoMotor::stop);
        }
    }


    //FUNZIONI PER MUOVERE DESTRA SINISTRA IL MOTORE CON LA PENNA

    private void movePenMotorLeft(EV3.Api api){
        final String TAG = Prelude.ReTAG("legoLeft");
        motor = api.getTachoMotor(EV3.OutputPort.C);
        try{
            motor.setSpeed(-20);
            motor.start();
        }
        catch (IOException e){
            Log.e(TAG, "legoLeft: Cannot change to speed = -50!");
        }
    }
    private void movePenMotorRight(EV3.Api api){
        final String TAG = Prelude.ReTAG("legoRight");
        motor = api.getTachoMotor(EV3.OutputPort.C);
        try{
            motor.setSpeed(20);
            motor.start();
        }
        catch (IOException e){
            Log.e(TAG, "legoRight: Cannot change to speed = 50!");
        }
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

}
