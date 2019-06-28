package ai.ll.tflitemnist;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.schokoladenbrown.Smooth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_prediction)
    TextView mTvPrediction;
    @BindView(R.id.tv_probability)
    TextView mTvProbability;
    @BindView(R.id.tv_timecost)
    TextView mTvTimeCost;

    @BindView(R.id.signature_pad)
    SignaturePad pad;


    private Classifier mClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.btn_detect)
    void onDetectClick() {
        if (mClassifier == null) {
            Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
            return;
        }

        Bitmap origImage = pad.getSignatureBitmap();
        saveImg(origImage, "orig.png");
        Bitmap padImage;

//        Bitmap padImage = Bitmap.createScaledBitmap(
//                origImage, Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT, true);


        long startTime = SystemClock.uptimeMillis();
        padImage = Bitmap.createScaledBitmap(
                origImage, Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT, true);
        long timeCost = SystemClock.uptimeMillis() - startTime;
        Log.v(LOG_TAG, "official rescale timeCost = " + timeCost);

        startTime = SystemClock.uptimeMillis();
        padImage = Smooth.rescale(origImage, Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT, Smooth.Algo.BILINEAR);
        timeCost = SystemClock.uptimeMillis() - startTime;
        Log.v(LOG_TAG, "rescale timeCost = " + timeCost);
        saveImg(padImage, "sample.png");

        Result result = mClassifier.classify(padImage);

        renderResult(result);
    }

    File saveImg(Bitmap img, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("saved_data", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            img.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath;
    }

    @OnClick(R.id.btn_clear)
    void onClearClick() {
        pad.clear();
        mTvPrediction.setText(R.string.empty);
        mTvProbability.setText(R.string.empty);
        mTvTimeCost.setText(R.string.empty);
    }

    private void init() {
        try {
            mClassifier = new Classifier(this);
        } catch (IOException e) {
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e);
        }
        Button next = (Button) findViewById(R.id.detect_back);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    private void renderResult(Result result) {
        mTvPrediction.setText(result.getResult());
//        mTvProbability.setText(String.valueOf(result.getProbability()));
        mTvTimeCost.setText(String.format(getString(R.string.timecost_value),
                result.getTimeCost()));
    }

}
