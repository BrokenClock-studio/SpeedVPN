package app.speedvpn;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.infradead.libopenconnect.LibOpenConnect;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import app.speedvpn.core.OpenConnectManagementThread;
import app.speedvpn.core.ProfileManager;
import app.speedvpn.fragments.FeedbackFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoadingActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;


    private ServerPull serverPull;
    private String CurrentIP = "";
    private String JSON_Servers = "";
    private LibOpenConnect mOC;

    private static final int version = 1;


    public static LoadingActivity loadingActivity;


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            finish();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);
        loadingActivity = this;
        serverPull = new ServerPull(ServerPull.S_lOADING_ACTIVITY);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                CallForIp();
            }
        }, 1000);


    }


    public void CallForIp(){

            if(iSConnected()){
                serverPull.execute(ServerPull.URL_GET_IP , ServerPull.TASK_GET_IP);
            }// else CallForIp();

    }

    public boolean iSConnected()
    {


        /*
        ConnectivityManager connectivityManager;
        boolean connected = false;

        try {
            connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            connected = false;
        }

        if(connected){
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("") && ipAddr != null;

            } catch (Exception e) {
                return false;
            }
        }else{
            return false;
        }

        */


//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            return false;
//        }
        String command = "ping -c 1 google.com";
        try {
            Boolean b = (Runtime.getRuntime().exec(command).waitFor() == 0);
            return b;
        } catch (InterruptedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    public void oNGetIP(String ip){

        CurrentIP = ip;
        serverPull = new ServerPull(ServerPull.S_lOADING_ACTIVITY);
        serverPull.execute(ServerPull.URL_GET_SERVERS , ServerPull.TASK_GET_SERVERS);
    }

    public void oNGetServers(String servers){
        JSON_Servers = servers;
        DataManager data = new DataManager();
        data = new Gson().fromJson(JSON_Servers, DataManager.class);
        if(data.Version != version){
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return;
        }

        DataManager.username = data.User_Name;
        DataManager.password = data.Password;
        DataManager.Server_NameS = data.Server_Names;
        PrefManager prefManager = new PrefManager(this, PrefManager.PRF_APP_DATA,PrefManager.MODE_READ);
        boolean connected = prefManager.ReadBool(PrefManager.KEY_CONNECTION_STATE);
        int connectionIndex = prefManager.ReadInt(PrefManager.KEY_CONNECTION_INDEX);

        //prefManager = new PrefManager(this, PrefManager.PRF_SERVER,PrefManager.MODE_WRITE);
       // Set<String> IPs = new HashSet<String>();
       // Set<String> Server_Names = new HashSet<String>();
        ProfileManager.mProfiles.clear();
        DataManager.Server_UUIDS = new String[data.Server_IPs.length];

        for ( int j=0; j<data.Server_IPs.length; j++  /*  String s: data.Server_IPs*/) {

            FeedbackFragment.recordProfileAdd(this);
            String s = data.Server_IPs[j];
            DataManager.Server_UUIDS[j] = s;
            //IPs.add(s);
        }
//        for (int j=0; j<data.Server_Names.length; j++ /* String s: data.Server_Names */) {
//            Server_Names.add(data.Server_Names[j]);
//        }

//        prefManager.SaveStringSet(PrefManager.KEY_SERVER_IPS,IPs);
//        prefManager.SaveStringSet(PrefManager.KEY_SERVER_NAMES,Server_Names);

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("spinnerIndex", connectionIndex);
        intent.putExtra("CrntIp", CurrentIP);
        intent.putExtra("CrntState", connected);
        startActivity(intent);
        finish();

        //operations

    }



    public void  Show( String s){

        Toast.makeText( getBaseContext(), s, Toast.LENGTH_SHORT).show();

    }





    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
