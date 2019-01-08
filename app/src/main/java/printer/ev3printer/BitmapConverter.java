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

    //public Bitmap ConvertImageToBNW(Bitmap imageToConvert){

    //}

}