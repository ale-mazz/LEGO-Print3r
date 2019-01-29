package printer.ev3printer;

import android.util.DebugUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.plugs.LightSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.TouchSensor;

public class PrinterManager {

    EV3.Api api;
    TachoMotor wheelMotor;
    TachoMotor penMotor;
    TachoMotor verticalMotor;
    LightSensor lightSensor;
    TouchSensor touchSensor;

    private static int loadingSheetSpeed = -60;
    private static int reflectedValueThreshold = 3;
    private static int verticalSpeed = 30;
    private static int verticalDistanceInTime = 10;

    //speeds
    private static int penMotorLeftRightSpeed = 20;
    private static int wheelMotorSpeed = 15;
    private static int penMotorStepsTime = 10;
    private static int wheelMotorStepsTime = 5;
    private static int verticalDotMove = 40;
    private static int verticalStepBuildOutAndIn = 15;


    private int numOfDots= 0;

    final String TAG = "PrinterManager";

    public PrinterManager(EV3.Api brick){
        api = brick;
        wheelMotor = brick.getTachoMotor(EV3.OutputPort.A);
        verticalMotor = brick.getTachoMotor(EV3.OutputPort.B);
        penMotor = brick.getTachoMotor(EV3.OutputPort.C);
        lightSensor = brick.getLightSensor(EV3.InputPort._2);
        touchSensor = brick.getTouchSensor(EV3.InputPort._1);
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
            System.out.println("Loading sheet: STARTED.");
            wheelMotor.setSpeed(loadingSheetSpeed);
            wheelMotor.start();
            while (condition){
                /*Future<Short> ambientValue = lightSensor.getAmbient();
                Short currentAmbientValue = ambientValue.get();
                Future<LightSensor.Color> colorValue = lightSensor.getColor();
                LightSensor.Color currentColor = colorValue.get(); */
                Future<Short> reflectedValue = lightSensor.getReflected();
                Short currentReflected = reflectedValue.get();
                /*System.out.println("'REFLECTED' letto: " + currentReflected);
                System.out.println("'COLORE' letto: " + currentColor.toString());
                System.out.println("'AMBIENT' letto: " + currentAmbientValue);*/
                if(currentReflected < reflectedValueThreshold){
                    wheelMotor.brake();
                    condition = false;
                }
            }
            wheelMotor.brake();
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
        try{
            wheelMotor.start();
            wheelMotor.setStepSpeed(-loadingSheetSpeed, 0, 1500, 0, true);

        }  catch (IOException e){
            Log.e(TAG, "Load sheet: lightSensor not working.");
        }
    }

    public void LoadSheetWithButton() throws IOException, ExecutionException, InterruptedException {

        boolean isPressed = false;
        boolean redButtonPressed;

        while (!isPressed){
            Log.d("LoadSheetButton: ","Checking button.");
            Future<Boolean> loadButtonPresses = touchSensor.getPressed();
            redButtonPressed = loadButtonPresses.get();
            if (redButtonPressed){
                System.out.println("Button pressed.");
                isPressed = true;
                LoadSheet();
            }
        }

/*        if (redButtonPressed){
            System.out.println("Button is pressed");
            wheelMotor.setSpeed(loadingSheetSpeed);
            wheelMotor.start();
            boolean condition = true;
            while(condition){
                Future<Short> reflectedValue = lightSensor.getReflected();
                Short currentReflected = reflectedValue.get();
                if(currentReflected < reflectedValueThreshold){
                    wheelMotor.brake();
                    condition = false;
                }
            }
        } */
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

    // Testing running status
    public void StepMoveTest(){
        int condition = 1000;
        boolean firstLoop = true;
        try{
            wheelMotor.start();
            wheelMotor.setStepSpeed(30, 0, 100, 0, true);
            while(condition > 0){
                Future<Float> speedValue = wheelMotor.getSpeed();
                Float currentSpeed = speedValue.get();
                System.out.println("Speed: " + currentSpeed);
                condition--;
                if(!firstLoop && currentSpeed == 0.0){
                    System.out.println("Speed == 0.0 - Exit while loop");
                    condition = 0;
                    wheelMotor.stop();
                }
                if(firstLoop){
                    firstLoop = false;
                }
            }
        } catch (IOException e){
            Log.e(TAG, "Wheelmotor cannot step");
        } catch (ExecutionException e){
            Log.e(TAG, "Wheelmotor: execution exception");
        } catch (InterruptedException e){
            Log.e(TAG, "Wheelmotor: interrupted exception");
        }
    }
    public void StepForwardWheel(int amount){
        try{
            for(int i = 0; i < amount; i++){
                wheelMotor.start();
                wheelMotor.setStepSpeed(-wheelMotorSpeed, 0, wheelMotorStepsTime, 0, true);
                wheelMotor.waitCompletion();
                wheelMotor.brake();
            }
        } catch (IOException e){
            Log.e(TAG, "Step wheel doesn't work");
        }
    }
    public void StepBackwardWheel(){
        try{
            wheelMotor.start();
            wheelMotor.setStepSpeed(wheelMotorSpeed, 0, wheelMotorStepsTime, 0, true);
            wheelMotor.waitCompletion();
            wheelMotor.brake();
        } catch (IOException e){
            Log.e(TAG, "Step wheel doesn't work");
        }
    }

    public void StepRight(int amount){
        try {
            for(int i = 0; i < amount; i++) {

                penMotor.start();
                penMotor.setStepSpeed(penMotorLeftRightSpeed, 0, penMotorStepsTime, 0, true);
                penMotor.waitCompletion();
                penMotor.brake();
            }
        } catch (IOException e){
            Log.e(TAG, "Step pen motor Right doesn't respond");
        }
    }
    public void StepLeft(int amount){
        try {
            for(int i = 0; i < amount; i++) {
                penMotor.start();
                penMotor.setStepSpeed(-penMotorLeftRightSpeed, 0, penMotorStepsTime, 0, true);
                penMotor.waitCompletion();
                penMotor.brake();
            }
        } catch (IOException e){
            Log.e(TAG, "Step pen motor Left doesn't respond");
        }
    }

    public void Dot(){
        try {
            verticalMotor.start();
            verticalMotor.setStepPower(- verticalSpeed,verticalStepBuildOutAndIn,verticalDotMove, verticalStepBuildOutAndIn, true);
            verticalMotor.waitCompletion();
            //System.out.println(verticalMotor.getPosition().get());
            verticalMotor.setStepPower(verticalSpeed,verticalStepBuildOutAndIn,verticalDotMove -2, verticalStepBuildOutAndIn, true);
            verticalMotor.waitCompletion();
        } catch (IOException e){
            Log.e(TAG, "Not dotting");
        }
    }

    public void DotDown(){
        try{
            verticalMotor.waitUntilReady();
            verticalMotor.start();
            verticalMotor.setStepSpeed(- verticalSpeed,verticalStepBuildOutAndIn,verticalDotMove, verticalStepBuildOutAndIn, true);
            verticalMotor.waitCompletion();
        } catch (IOException e){
            Log.e(TAG, "Problems dotting down.");
        } catch (ExecutionException e){
            Log.e(TAG,"Ex excep in dotdown");
        } catch(InterruptedException e){
            Log.e(TAG,"Int excep in dotdown");
        }

    }
    public void DotUp(){
        try{
            verticalMotor.waitUntilReady();
            verticalMotor.start();
            verticalMotor.setStepSpeed(verticalSpeed,verticalStepBuildOutAndIn,verticalDotMove, verticalStepBuildOutAndIn, true);
            verticalMotor.waitCompletion();
        } catch (IOException e){
            Log.e(TAG, "Problems dotting up.");
        } catch (ExecutionException e){
            Log.e(TAG,"Ex excep in dotup");
        } catch(InterruptedException e){
            Log.e(TAG,"Int excep in dotup");
        }
    }


    public void ConvertInstructionToAction(PrinterInstruction instruction){
        switch (instruction.getDirection()){
            case FORWARD:
                StepForwardWheel(instruction.getAmount());
                break;
            case RIGHT:
                StepRight(instruction.getAmount());
                break;
            case POINT:
                //Dot();
                DotDown();
                DotUp();
                break;
            case LEFT:
                StepLeft(instruction.getAmount());
                break;
        }
    }

    public void PrintImage(List<PrinterInstruction> list){
        if(list.size() > 0){
            for (PrinterInstruction instruction : list) {
                ConvertInstructionToAction(instruction);
            }
        }
        //DotUp();
        UnloadSheet();

    }
}
