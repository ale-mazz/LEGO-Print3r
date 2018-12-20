package printer.ev3printer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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


        final Button testButton = findViewById(R.id.printButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //printArray(bwImageArray);
            }
        });

    }

    public void printArray() {

    }
}