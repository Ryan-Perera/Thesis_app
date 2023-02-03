package com.example.thesis1_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class Sample extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        textView = (TextView)findViewById(R.id.textView2);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this   ));
        }

        //this will start python

        //now create python instance
        Python py = Python.getInstance();

        //now create python object
        PyObject pyobj = py.getModule("helloworld"); //give python script name

        PyObject obj = pyobj.callAttr("main");

        // now set return

        textView.setText(obj.toString());
    }
}