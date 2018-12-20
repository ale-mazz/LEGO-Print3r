package printer.ev3printer;

import java.util.ArrayList;

// TODO: Migliorare l'algoritmo di stampa, che non faccia avanti indietro ogni volta

public class InstructionBuilder {
    public static ArrayList<PrinterInstruction> BuildInstructionListFromBitmap(boolean[][] array, int width, int height){
        ArrayList<PrinterInstruction> list = new ArrayList<PrinterInstruction>();
        int emptyLeft = 0;
        int rightAmount = 0;
        boolean amIADot = false;
        for (int y = 0; y<height; y++){
            for (int x = 0; x < width; x++){
                if(array[y][x] == true){
                    amIADot = true;
                }
                if(emptyLeft > 0 && amIADot){
                    list.add(new PrinterInstruction(PrinterInstruction.Direction.RIGHT, emptyLeft));
                    rightAmount+= emptyLeft;
                    emptyLeft = 0;
                }
                if(amIADot){
                    list.add(new PrinterInstruction(PrinterInstruction.Direction.POINT, 1));
                    emptyLeft ++;
                    amIADot = false;
                } else {
                    emptyLeft++;
                }
            }
            //devo tornare indietro di rightAmount
            if(rightAmount > 0) {
                list.add(new PrinterInstruction(PrinterInstruction.Direction.LEFT, rightAmount));
                rightAmount = 0;
            }
            emptyLeft = 0;
            list.add(new PrinterInstruction(PrinterInstruction.Direction.FORWARD, 1));
        }
        return list;
    }

    public static boolean[][] unidimensionalToBidimensional(boolean[] bwImageArray, int array_size){
        int count = 0;
        boolean[][] bidimensionalArray = new boolean[array_size][array_size];

        for(int i = 0; i < array_size; i++){
            for (int j = 0; j < array_size; j++){
                bidimensionalArray[i][j] = bwImageArray[count];
                count++;
            }
        }

        return bidimensionalArray;
    }
}
