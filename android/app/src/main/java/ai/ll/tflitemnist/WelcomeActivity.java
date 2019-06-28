package ai.ll.tflitemnist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ButterKnife.bind(this);
        init();

    }
    private void init(){
        Button goDetect = (Button) findViewById(R.id.welcom_detect);
        goDetect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button goWrite = (Button) findViewById(R.id.welcom_write);
        goWrite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), UploadActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

}
