package printer.ev3printer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final Button button = (Button) findViewById(R.id.imageSelectionButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri uri = data.getData();

                try {
                    Bitmap imageSelectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Bitmap bwImageSelectedBitmap = com.askjeffreyliu.floydsteinbergdithering.Utils.floydSteinbergDithering(imageSelectedBitmap);
                    BitmapConverter converter = new BitmapConverter();
                    boolean[] bwImageArray = converter.readBitmapPixelsAsBooleans(bwImageSelectedBitmap);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageBitmap(imageSelectedBitmap);

                    ImageView bwImageView = (ImageView) findViewById(R.id.bwImageView);
                    bwImageView.setImageBitmap(bwImageSelectedBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

}
