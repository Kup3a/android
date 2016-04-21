package ru.kazakov.task2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {

    private StartActivity startActivity = this;
    private AsyncTask mT;
    private long startTime;
    private String LIVING_TIME = "living_time";
    private String START_TIME = "start_time";
    private final int SLEEP_TIME = 2000;
    private HttpRequest download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (getActionBar() != null) {
            getActionBar().hide();
        }

        if (savedInstanceState == null) {
            startTime = System.currentTimeMillis();
            download = new HttpRequest(this);
            download.execute("http://mobevo.ext.terrhq.ru/shr/j/ru/technology.js");
            mT = new StartMain();
            mT.execute();
        } else {
            startTime = savedInstanceState.getLong(START_TIME);
            download = (HttpRequest)getLastCustomNonConfigurationInstance();
            if (savedInstanceState.getInt(LIVING_TIME) > SLEEP_TIME && download.getStatus().toString().equals("FINISHED")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                mT = new StartMain();
                mT.execute();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIVING_TIME, (int) (System.currentTimeMillis() - startTime));
        outState.putLong(START_TIME, startTime);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return download;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mT != null) {
            mT.cancel(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    class StartMain extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                int sec = 0;
                while (sec < 2 || !download.getStatus().toString().equals("FINISHED")) {
                    sec += 1;
                    TimeUnit.SECONDS.sleep(1);
                    if (isCancelled()) {
                        return null;
                    }
                }
                Intent intent = new Intent(startActivity, MainActivity.class);
                startActivity(intent);
                startActivity.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    private static class HttpRequest extends AsyncTask<String, Integer, String> {
        private WeakReference<StartActivity> mListener;

        public HttpRequest(StartActivity listener) {
            mListener = new WeakReference<StartActivity>(listener);
        }

        @Override
        protected String doInBackground(String... params) {
            if (params != null && params.length > 0) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        InputStream is = new BufferedInputStream(connection.getInputStream());
                        String html = Utils.readInputStream(is);
                        StringBuilder builder = new StringBuilder();
                        builder.append(html);
                        is.close();

                        try {
                            StartActivity stA = mListener.get();
                            // отрываем поток для записи
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stA.openFileOutput("technology", stA.MODE_PRIVATE)));
                            // пишем данные
                            bw.write(builder.toString());
                            // закрываем поток
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return builder.toString();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }
}
