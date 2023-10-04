package com.example.myapplicationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import com.example.myapplicationtest.databinding.ActivityMainBinding;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.content.res.Resources;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'myapplicationtest' library on application startup.
    static {
        System.loadLibrary("myapplicationtest");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        binding.sampleText.setText(process());
        doSomething();
        displayImage();
    }

    public String processInJava(){
        Log.d("Main", String.format("[ message ] processInJava"));
        return "Process In Java";
    }

    public native String process();

    public void parameterToJava(int result){
        Log.d("Main", String.format("[ message ] parameterToJava"));
        Log.d("Main", String.format("[ message ] processInJava %d", result));
        TextView textView = (TextView) findViewById(R.id.sample_text2);
        textView.setText(Integer.toString(result));
    }

    public native void doSomething();

    public byte[] getImage(){
        Log.d("Main", String.format("[ message ] getImage"));

        Resources resources = getResources();
        Drawable drawable = resources.getDrawable(R.drawable.apple);
        Bitmap bitmap = drawable2Bitmap(drawable);
        return bitmap2Bytes(bitmap);
    }

    public void androidDisplayImage(byte[] raw){
        Log.d("Main", String.format("[ message ] androidDisplayImage"));

        // Display the Drawable in an ImageView
        ImageView imageView = findViewById(R.id.imageView1);
        Bitmap bitmap = bytes2Bitmap(raw);
        imageView.setImageDrawable(bitmap2Drawable(bitmap));
    }

    public native void displayImage();

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        return null;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        @SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }

    /**
     * A native method that is implemented by the 'myapplicationtest' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}