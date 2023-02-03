package com.example.thesis1_2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Select_Image extends AppCompatActivity {
    ImageView img;

    BitmapDrawable drawable;
    Bitmap bitmap;
    String imageString="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_select_image);
        TextView textView = findViewById(R.id.details);

        Button selectImage = (Button) findViewById(R.id.gallery);
        Button grayScale = (Button) findViewById(R.id.grayscale);
        grayScale.setVisibility(View.INVISIBLE);
        Button kMeans = (Button) findViewById(R.id.k_means);
        kMeans.setVisibility(View.INVISIBLE);

        img = (ImageView) findViewById(R.id.imageView);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        final Python py = Python.getInstance();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
                textView.setText("");
                grayScale.setVisibility(View.VISIBLE);
                kMeans.setVisibility(View.VISIBLE);
            }
        });

        grayScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawable = (BitmapDrawable) img.getDrawable();
                bitmap = drawable.getBitmap();

                textView.setText("GrayScale");

                imageString = getStringImage(bitmap);

                //call my python script
                PyObject pyo = py.getModule("grayscale");
                PyObject obj = pyo.callAttr("main", imageString);


                String str = obj.toString();
                byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);

                img.setImageBitmap(bmp);

            }
        });

        kMeans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                drawable = (BitmapDrawable) img.getDrawable();
                bitmap = drawable.getBitmap();
                imageString = getStringImage(bitmap);
                textView.setText("Calculating K Means . . . . ");

                //call my python script
                PyObject pyo = py.getModule("kmeans");
                PyObject obj = pyo.callAttr("main", imageString);
                String str = obj.toString();

                List<PyObject> results = obj.asList();
                byte data[] = android.util.Base64.decode(results.get(0).toString(), Base64.DEFAULT);
                String output = "K Means\nHotspot Percentage: " + results.get(1).toString() + "%";
                Toast.makeText(Select_Image.this, output, Toast.LENGTH_SHORT).show();

                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);

                img.setImageBitmap(bmp);
            }
        });
    }


    private  String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,boas);
        byte[] imageBytes = boas.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(selectedImage);
            img.setVisibility(View.VISIBLE);
        }
    }
}