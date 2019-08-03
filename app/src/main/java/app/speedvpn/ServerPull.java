package app.speedvpn;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServerPull extends AsyncTask<String, Integer, String> {


    public static final String URL_GET_IP = "";
    public static final String URL_GET_SERVERS = "";
    public static final String TASK_GET_IP = "GET_IP";
    public static final String TASK_GET_IP_MAIN = "GET_IP_MAIN";
    public static final String TASK_GET_SERVERS = "GET_SERVERS";
    public static final String S_MAIN_ACTIVITY = "MAIN_ACTIVITY";
    public static final String S_lOADING_ACTIVITY = "LOADING_ACTIVITY";



    public String TASK = "";

    private String Source;


    public ServerPull(String s){
        Source = s;
    }


    protected String doInBackground(String... urls) {
        URL url = null;
        try {
            url = new URL(urls[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TASK = urls[1];
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = "", line;

        try {
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return content;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        if(result == null || result=="") {
            //new LoadingActivity().Show("Faced Problem! Check Internet Connection!");
        }else {
            if(TASK.equals(TASK_GET_IP)){
                LoadingActivity.loadingActivity.oNGetIP(result);
//                if(Source.equals(S_lOADING_ACTIVITY)){
//                    LoadingActivity.loadingActivity.oNGetIP(result);
//                }else if(Source.equals(S_MAIN_ACTIVITY)){
//                   // MainActivity.mainActivity.oNGetIP(result);
//                }

            }else if(TASK.equals(TASK_GET_SERVERS)){
                LoadingActivity.loadingActivity.oNGetServers(result);
            }else if(TASK.equals(TASK_GET_IP_MAIN)){
                MainActivity.mainActivity.oNGetIP(result);

            }
        }
    }
}