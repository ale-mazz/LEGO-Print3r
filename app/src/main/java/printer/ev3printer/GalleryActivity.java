package printer.ev3printer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap imageSelectedBitmap;
    public Bitmap bwImageSelectedBitmap;
    public Bitmap resizedBwImageSelectedBitmap;
    public boolean[] bwImageArray;
    public boolean[][] bidimensionalArray;
    public static int array_size = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        bidimensionalArray = new boolean[array_size][array_size];

        final Button selectImageButton = findViewById(R.id.imageSelectionButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        final Button testArrayButton = findViewById(R.id.testArrayButton);
        testArrayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                convertArray(bwImageArray);
                testArray(bidimensionalArray);
            }
        });

        final Button changeActivity = findViewById(R.id.changeActivity);
        changeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBooleanArray("boolArray", bwImageArray);
                Intent i = new Intent(GalleryActivity.this, TestActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap bwImageSelectedBitmap, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(bwImageSelectedBitmap, bitmapWidth, bitmapHeight, true);
    }

    /*public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                imageSelectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                resizedBwImageSelectedBitmap = getResizedBitmap(imageSelectedBitmap, array_size, array_size);
                bwImageSelectedBitmap = com.askjeffreyliu.floydsteinbergdithering.Utils.floydSteinbergDithering(resizedBwImageSelectedBitmap);

                BitmapConverter converter = new BitmapConverter();
                bwImageArray = converter.readBitmapPixelsAsBooleans(bwImageSelectedBitmap);

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(imageSelectedBitmap);

                ImageView bwImageView = findViewById(R.id.bwImageView);
                bwImageView.setImageBitmap(bwImageSelectedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void convertArray(boolean[] bwImageArray){
        int count = 0;

        for(int i = 0; i < array_size; i++){
            for (int j = 0; j < array_size; j++){
                bidimensionalArray[i][j] = bwImageArray[count];
                count++;
            }
        }
    }

    public void testArray(boolean[][] bidimensionalArray) {
        int blackDots = 0;
        int whiteDots = 0;
        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++){
                if (bidimensionalArray[i][j]) {
                    blackDots++;
                } else {
                    whiteDots++;
                }
            }
        }
        System.out.println();
        System.out.println("number of black pixels:" + blackDots);
        System.out.println("number of white pixels:" + whiteDots);
    }

}
