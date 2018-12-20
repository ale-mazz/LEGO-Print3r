package printer.ev3printer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    boolean[] bwImageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            bwImageArray = b.getBooleanArray("boolArray");
        }


        final Button testButton = findViewById(R.id.testArrayButton1);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testArray(bwImageArray);
            }
        });

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
        System.out.println("number of black pixels:" + blackDots);
        System.out.println("number of white pixels:" + whiteDots);
    }
}