package printer.ev3printer;

import java.util.ArrayList;

// TODO: Migliorare l'algoritmo di stampa, che non faccia avanti indietro ogni volta


public class InstructionBuilder {
    /**
     * Given a boolean array with its size, this method returns a list of Printer Instruction
     * @param array The boolean array
     * @param width
     * @param height
     * @return
     */
    public static ArrayList<PrinterInstruction> BuildInstructionListFromBitmap(boolean[][] array, int width, int height){
        ArrayList<PrinterInstruction> list = new ArrayList<>();
        int emptyLeft = 0;
        int rightAmount = 0;
        boolean amIADot = false;
        for (int y = 0; y<height; y++) {
                for (int x = 0; x < width; x++) {
                    if (array[y][x]) {
                        amIADot = true;
                    }
                    if (emptyLeft > 0 && amIADot) {
                        list.add(new PrinterInstruction(PrinterInstruction.Direction.RIGHT, emptyLeft));
                        rightAmount += emptyLeft;
                        emptyLeft = 0;
                    }
                    if (amIADot) {
                        list.add(new PrinterInstruction(PrinterInstruction.Direction.POINT, 1));
                        emptyLeft++;
                        amIADot = false;
                    } else {
                        emptyLeft++;
                    }
                }
                if (rightAmount > 0) {
                    list.add(new PrinterInstruction(PrinterInstruction.Direction.LEFT, rightAmount));
                    rightAmount = 0;
                }
                emptyLeft = 0;
                list.add(new PrinterInstruction(PrinterInstruction.Direction.FORWARD, 1));
            }
        return list;
    }


}
