package com.nex3z.tflitemnist;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class Classifier {
    private static final String LOG_TAG = Classifier.class.getSimpleName();

    private static final String MODEL_NAME = "converted_model.tflite";
//    private static final String MODEL_NAME = "mnist.tflite";

    private static final int BATCH_SIZE = 1;
    public static final int IMG_HEIGHT = 100;
    public static final int IMG_WIDTH = 80;
    private static final int NUM_CHANNEL = 1;
    private static final int NUM_CLASSES = 10;

    private final Interpreter.Options options = new Interpreter.Options();
    private final Interpreter mInterpreter;
    private final ByteBuffer mImageData;
    private final int[] mImagePixels = new int[IMG_HEIGHT * IMG_WIDTH];
    private final int[][] mResult = new int[1][10];

    public Classifier(Activity activity) throws IOException {
        mInterpreter = new Interpreter(loadModelFile(activity), options);
        mImageData = ByteBuffer.allocateDirect(
                4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL);
        mImageData.order(ByteOrder.nativeOrder());
    }

    public Result classify(Bitmap bitmap) {
//        bitmap = toGrayscale(bitmap);
        convertBitmapToByteBuffer(bitmap);
        long startTime = SystemClock.uptimeMillis();

//        Object[] inputs = {mImageData, 1};
//        Map<Integer, Object> outputs = new HashMap<>();
//        outputs.put(0, mResult);
//        mInterpreter.runForMultipleInputsOutputs(inputs, outputs);

        mInterpreter.run(mImageData, mResult);
        long endTime = SystemClock.uptimeMillis();
        long timeCost = endTime - startTime;
        Log.v(LOG_TAG, "classify(): result = " + Arrays.toString(mResult[0])
                + ", timeCost = " + timeCost);
        Result res = new Result(mResult[0], timeCost);
        Log.i(LOG_TAG, res.getResult());
        return res;
    }

//    private Bitmap toGrayscale(Bitmap bmpOriginal) {
//        int width, height;
//        height = bmpOriginal.getHeight();
//        width = bmpOriginal.getWidth();
//
//        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bmpGrayscale);
//        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        paint.setColorFilter(f);
//        c.drawBitmap(bmpOriginal, 0, 0, paint);
//        return bmpGrayscale;
//    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (mImageData == null) {
            return;
        }
        mImageData.rewind();

        bitmap.getPixels(mImagePixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < IMG_WIDTH; ++i) {
            for (int j = 0; j < IMG_HEIGHT; ++j) {
                int value = mImagePixels[pixel++];
//                mImageData.putFloat(convertPixel(value));
                mImageData.putInt(convertPixelInt(value));
            }
        }
    }

    private static float convertPixel(int color) {
        return (255 - (((color >> 16) & 0xFF) * 0.299f
                + ((color >> 8) & 0xFF) * 0.587f
                + (color & 0xFF) * 0.114f)) / 255.0f;
    }

    private static int convertPixelInt(int color) {
        return (int) (((color >> 16) & 0xFF) * 0.299f
                + ((color >> 8) & 0xFF) * 0.587f
                + (color & 0xFF) * 0.114f);
    }
}
