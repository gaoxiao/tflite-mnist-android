package com.nex3z.tflitemnist;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

//        if (mFpvPaint.isEmpty()) {
//            Toast.makeText(this, R.string.please_write_a_digit, Toast.LENGTH_SHORT).show();
//            return;
//        }

//        Bitmap image = mFpvPaint.exportToBitmap(
//                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT);
//        Result result = mClassifier.classify(image);

        Bitmap padImage = pad.getSignatureBitmap();
        saveImg(padImage, "orig.png");
        padImage = Bitmap.createScaledBitmap(
                padImage, Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT, false);
//        padImage = scaleCenterCrop(
//                padImage, Classifier.IMG_HEIGHT, Classifier.IMG_WIDTH);
        saveImg(padImage, "sample.png");
        Result result = mClassifier.classify(padImage);

        renderResult(result);
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        //get the resulting size after scaling
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        //figure out where we should translate to
        float dx = (newWidth - scaledWidth) / 2;
        float dy = (newHeight - scaledHeight) / 2;

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);
        canvas.drawBitmap(source, matrix, null);
        return dest;
    }


    void saveImg(Bitmap img, String name) {
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
    }

    private void renderResult(Result result) {
        mTvPrediction.setText(result.getResult());
//        mTvProbability.setText(String.valueOf(result.getProbability()));
        mTvTimeCost.setText(String.format(getString(R.string.timecost_value),
                result.getTimeCost()));
    }

}
