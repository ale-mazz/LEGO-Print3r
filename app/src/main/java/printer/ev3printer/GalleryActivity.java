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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        final Button button = findViewById(R.id.imageSelectionButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        final Button button1 = findViewById(R.id.testArrayButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testArray(bwImageArray);
            }
        });

        final Button changeActivity = findViewById(R.id.changeActivity);
        changeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBooleanArray("boolArray", bwImageArray);
                Intent i2 = new Intent(GalleryActivity.this, TestActivity.class);
                i2.putExtras(b);
                startActivity(i2);
            }
        });



    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                imageSelectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                resizedBwImageSelectedBitmap = getResizedBitmap(imageSelectedBitmap, 60);
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

    public void testArray(boolean[] bwImageArray) {
        int blackDots = 0;
        int whiteDots = 0;
        for (int i = 0; i < bwImageArray.length; i++) {
            if (bwImageArray[i]) {
                blackDots++;
            } else {
                whiteDots++;
            }
        }
        System.out.println();
        System.out.println(blackDots);
        System.out.println(whiteDots);
    }

}
