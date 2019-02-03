package com.gearback.methods;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class HttpPostRequest extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;
    private WeakReference<Activity> context;
    private Methods methods = new Methods();
    private final TaskListener taskListener;

    public HttpPostRequest(Activity activity, TaskListener listener) {
        this.context = new WeakReference<Activity>(activity);
        taskListener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (context.get() != null) {
            if (!methods.isInternetAvailable(context.get())) {
                cancel(true);
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String result;
        String inputLine;
        String urlParameters = strings[1];
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int postDataLength = postData.length;

        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            connection.setUseCaches(false);
            connection.getOutputStream().write(postData);

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        }
        catch(IOException e){
            e.printStackTrace();
            result = "";
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if(this.taskListener != null) {
            this.taskListener.onFinished(s);
        }
    }

    public interface TaskListener {
        public void onFinished(String result);
    }
}