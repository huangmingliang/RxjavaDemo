package com.hml.example.rxjavademo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private ImageView imageView;
    private ProgressDialog pd;
    private String url = "http://nbct01.baidupcs.com/file/3a077e36a04c28bd7c5978e2434010df?bkt=p3-14003a077e36a04c28bd7c5978e2434010dfbc96e85300000002d837&fid=3009446438-250528-446248723778433&time=1466406467&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-gkbcbAHYcEP3zoBrOAwqxw5cIWU%3D&to=nbhb&fm=Nin,B,T,t&sta_dx=0&sta_cs=0&sta_ft=jpg&sta_ct=0&fm2=Ningbo,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=14003a077e36a04c28bd7c5978e2434010dfbc96e85300000002d837&sl=82509903&expires=8h&rt=sh&r=633061326&mlogid=3980593846408581392&vuk=3009446438&vbdid=2722452116&fin=11.jpg&fn=11.jpg&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=3980593846408581392&dp-callid=0.1.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        pd = new ProgressDialog(context);
        imageView=(ImageView) findViewById(R.id.imageView);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.e(TAG, "current thread_1:" + Thread.currentThread().getName().toString());
                subscriber.onNext(url);
                subscriber.onCompleted();
            }
        })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "current thread_2:" + Thread.currentThread().getName().toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        Log.e(TAG, "current thread_3:" + Thread.currentThread().getName().toString());
                        return getImageBitmap(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError");
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.e(TAG, "current thread_4:" + Thread.currentThread().getName().toString());
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    private Bitmap getImageBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
