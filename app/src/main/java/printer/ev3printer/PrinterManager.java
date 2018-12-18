package printer.ev3printer;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.plugs.LightSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;

public class PrinterManager {

    EV3.Api api;
    TachoMotor wheelMotor;
    TachoMotor penMotor;
    TachoMotor verticalMotor;
    LightSensor lightSensor;

    private static int loadingSheetSpeed = -60;
    private static int lightSensorAmbientThreshold = 3;
    private static int verticalSpeed = 40;
    private static int verticalDistanceInTime = 300;

    final String TAG = "PrinterManager";

    public PrinterManager(EV3.Api brick){
        api = brick;
        wheelMotor = brick.getTachoMotor(EV3.OutputPort.A);
        verticalMotor = brick.getTachoMotor(EV3.OutputPort.B);
        penMotor = brick.getTachoMotor(EV3.OutputPort.C);
        lightSensor = brick.getLightSensor(EV3.InputPort._2);
    }

    // Funzione per caricare il foglio
    public void LoadSheet(String TAG){
        boolean condition = true;
        try {
            wheelMotor.start();
            wheelMotor.setSpeed(loadingSheetSpeed);
            while (condition){
                Future<Short> ambientValue = lightSensor.getAmbient();
                Short currentAmbientValue = ambientValue.get();
                if(currentAmbientValue < lightSensorAmbientThreshold){
                    wheelMotor.brake();
                    condition = false;
                }
            }
            System.out.println("FINITO");
        } catch (IOException e){
            Log.e(TAG, "lightSensor not working");
        } catch (ExecutionException e){
            Log.e(TAG, "execution exception");
        } catch (InterruptedException e){
            Log.e(TAG, "interrupted exception");
        }
    }

    public void UnloadSheet(String TAG){
        boolean condition = true;
        try {
            wheelMotor.start();
            wheelMotor.setSpeed(-1 * loadingSheetSpeed);
            while (condition){
                Future<Short> ambientValue = lightSensor.getAmbient();
                Short currentAmbientValue = ambientValue.get();
                if(ambientValue.get() > lightSensorAmbientThreshold){
                    System.out.println("THERE IS NO SHEET");
                    wheelMotor.stop();
                    condition = false;
                }
            }
        }  catch (IOException e){
            Log.e(TAG, "lightSensor not working");
        } catch (ExecutionException e){
            Log.e(TAG, "execution exception");
        } catch (InterruptedException e){
            Log.e(TAG, "interrupted exception");
        }
    }
    public void LowerPen(){
        try {
            verticalMotor.start();
            verticalMotor.setStepSpeed(- verticalSpeed,0,verticalDistanceInTime, 0, true);
        } catch (IOException e){
            Log.e(TAG, "Cannot Lower Pen");
        }
    }
    public void RaisePen(){
        try {
            verticalMotor.start();
            verticalMotor.setStepSpeed(verticalSpeed, 0, verticalDistanceInTime, 0, true);
        } catch (IOException e){
            Log.e(TAG, "Cannot Raise Pen");
        }
    }
}
