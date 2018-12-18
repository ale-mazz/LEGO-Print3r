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
    private static int verticalSpeed = 60;
    private static int verticalDistanceInTime = 300;
    private static int penMotorLeftRightSpeed = 20;

    final String TAG = "PrinterManager";

    public PrinterManager(EV3.Api brick){
        api = brick;
        wheelMotor = brick.getTachoMotor(EV3.OutputPort.A);
        verticalMotor = brick.getTachoMotor(EV3.OutputPort.B);
        penMotor = brick.getTachoMotor(EV3.OutputPort.C);
        lightSensor = brick.getLightSensor(EV3.InputPort._2);
    }


    // Pen motor functions

    public void KeepGoingRight(){
        try {
            penMotor.setSpeed(penMotorLeftRightSpeed);
            penMotor.start();
        } catch (IOException e){
            Log.e(TAG, "Can't go right.");
        }
    }
    public void KeepGoingLeft(){
        try {
            penMotor.setSpeed(-penMotorLeftRightSpeed);
            penMotor.start();
        } catch (IOException e){
            Log.e(TAG, "Can't go left.");
        }
    }


    // Wheel motor functions

    public void LoadSheet(){
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
            Log.e(TAG, "Load sheet: lightSensor not working");
        } catch (ExecutionException e){
            Log.e(TAG, "Load sheet: execution exception");
        } catch (InterruptedException e){
            Log.e(TAG, "Load sheet: interrupted exception");
        }
    }
    public void UnloadSheet(){
        boolean condition = true;
        try {
            wheelMotor.start();
            wheelMotor.setSpeed(-1 * loadingSheetSpeed);
            while (condition){
                Future<Short> ambientValue = lightSensor.getAmbient();
                Short currentAmbientValue = ambientValue.get();
                if(currentAmbientValue > lightSensorAmbientThreshold){
                    System.out.println("THERE IS NO SHEET");
                    wheelMotor.stop();
                    condition = false;
                }
            }
        }  catch (IOException e){
            Log.e(TAG, "Load sheet: lightSensor not working");
        } catch (ExecutionException e){
            Log.e(TAG, "Load sheet: execution exception");
        } catch (InterruptedException e){
            Log.e(TAG, "Load sheet: interrupted exception");
        }
    }

    // Vertical motor functions

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

    // Stop motors functions

    public void StopVerticalMotor(){
        try {
            verticalMotor.stop();
        } catch (IOException e){
            Log.e(TAG, "Cannot stop vertical motor");
        }
    }
    public void StopPenMotor(){
        try{
            penMotor.stop();
        } catch (IOException e){
            Log.e(TAG, "Cannot stop pen motor");
        }
    }
    public void StopWheelMotor(){
        try {
            wheelMotor.stop();
        } catch (IOException e){
            Log.e(TAG, "Cannot stop wheel motor");
        }
    }
}
