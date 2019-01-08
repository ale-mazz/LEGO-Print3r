package printer.ev3printer;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static int lightSensorAmbientThresholdForLoading = 1;
    private static int reflectedValueThreshold = 3;
    private static int lightSensorAmbientThresholdForUnloading = 0;
    private static int verticalSpeed = 20;
    private static int verticalDistanceInTime = 10;

    //speeds
    private static int penMotorLeftRightSpeed = 15;
    private static int wheelMotorSpeed = 15;
    private static int penMotorStepsTime = 10;
    private static int wheelMotorStepsTime = 5;
    private static int verticalDotMove = 100;


    private int numOfDots= 0;

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
        boolean condition = true;
        try {
            wheelMotor.start();
            wheelMotor.setSpeed(-loadingSheetSpeed);
            while (condition){
                //Future<Short> ambientValue = lightSensor.getAmbient();
                //Short currentAmbientValue = ambientValue.get();
                Future<Short> reflectedValue = lightSensor.getReflected();
                Short currentReflected = reflectedValue.get();
                //System.out.println("Valore letto in espulsione: " + currentAmbientValue);
                if(currentReflected > reflectedValueThreshold){
                    System.out.println("THERE IS NO SHEET");
                    wheelMotor.stop();
                    condition = false;
                }
            }
            wheelMotor.stop();
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
            verticalMotor.setStepSpeed(- verticalSpeed,0,verticalDotMove, 0, true);
            verticalMotor.waitCompletion();
            numOfDots += 1;
            System.out.println("Cazzi dot: " + numOfDots);
            verticalMotor.setStepSpeed(verticalSpeed,0,verticalDotMove, 0, true);
            verticalMotor.waitCompletion();
            verticalMotor.brake();
        } catch (IOException e){
            Log.e(TAG, "Not dotting");
        }
    }

    public void TestMultiDot() {
        ArrayList<PrinterInstruction> list = new ArrayList<PrinterInstruction>();
        list.add(new PrinterInstruction(PrinterInstruction.Direction.LEFT, 5));
        list.add(new PrinterInstruction(PrinterInstruction.Direction.POINT, 1));
        list.add(new PrinterInstruction(PrinterInstruction.Direction.FORWARD, 15));
        list.add(new PrinterInstruction(PrinterInstruction.Direction.POINT,1));

        for (PrinterInstruction instruction: list) {
            ConvertInstructionToAction(instruction);
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
                Dot();
                break;
            case LEFT:
                StepLeft(instruction.getAmount());
                break;
        }

    }

    public void PrintImage(List<PrinterInstruction> list){
        for (PrinterInstruction instruction : list) {
            ConvertInstructionToAction(instruction);
        }
    }
    // public ArrayList<PrinterInstruction> ConvertArrayToInstructions(boolean[] imageArray){ }

}
