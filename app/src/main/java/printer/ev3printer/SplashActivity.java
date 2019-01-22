package printer.ev3printer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent mySuperIntent = new Intent(SplashActivity.this, GalleryActivity.class);
            startActivity(mySuperIntent);
            finish();
        }, SPLASH_TIME);
    }
}