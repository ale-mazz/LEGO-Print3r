package printer.ev3printer;

import android.graphics.Bitmap;

public class BitmapConverter {

    /**
     * Returns a copy of the data in the bitmap. Each value is a packed int representing a Color.
     * The returned colors are non-premultiplied ARGB values in the sRGB color space.
     * @param bitmap Bitmap from which to read the pixels.
     */
    private int[] getPixelsFromBitMap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        return pixels;
    }

    /**
     * Given an array of colors as non-premultiplied ARGB values in the sRGB color space, returns
     * a boolean array with the same size where
     * • result[i] == true iff array[i] represents a black pixel
     * • result[i] == false iff array[i] represents a white pixel
     * @param array array of colors as non-premultiplied ARGB values in the sRGB color space to be
     *              converted.
     */
    private boolean[] getBooleanValuesFromIntArray(int[] array) {
        boolean[] booleanValues = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            // Read the lowest byte of each 32-bit pixel.
            // If it's less than 128 it means it's a black pixel, otherwise it's white.
            booleanValues[i] = ((array[i] & 0x80) >> 7) == 0;
        }
        return booleanValues;
    }

    /**
     * Given a bitmap, reads the pixels of that bitmap and returns a boolean array with size
     * width * height where:
     * • result[i] == true iff array[i] represents a black pixel
     * • result[i] == false iff array[i] represents a white pixel
     * @param bitmap Bitmap which pixels should be converted into a boolean array.
     */
    public boolean[] readBitmapPixelsAsBooleans(Bitmap bitmap) {
        return getBooleanValuesFromIntArray(getPixelsFromBitMap(bitmap));
    }

    /**
     * Given a single dimention boolean array and its size this method returns a bidimentional boolean array
     * of size [array_size][array_size]
     * @param bwImageArray The boolean array to convert to two dimention boolean array
     * @param array_size The size of the new array
     */

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
    /*
    public static boolean[][] deleteAlonePoint(boolean[][] originalArray, int array_size, int aloneDistance){
        boolean[][] newArray = new boolean[array_size][array_size];
        newArray = originalArray;
        if(aloneDistance>0){
            for(int x=0; x<array_size; x++){
                for(int y=0; y<array_size;y++){
                    for(int i = x-aloneDistance; i < a){}
                }
            }
        }
        return newArray;

    }*/

}