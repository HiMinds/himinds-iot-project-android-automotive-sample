package com.example.myapplication;

import android.car.diagnostic.FloatSensorIndex;
import android.car.hardware.cabin.CarCabinManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.ImageLinearGauge;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.Speedometer;

import android.car.diagnostic.CarDiagnosticManager;
import android.car.Car;
import android.car.diagnostic.CarDiagnosticEvent;
import android.car.diagnostic.CarDiagnosticManager.OnDiagnosticEventListener;
import android.graphics.Color;
import android.os.Bundle;
import android.car.hardware.CarSensorManager;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Himinds-DEMO";
    private Car mCarApi;
    private CarDiagnosticManager mDiagnosticManager;
    //private OnDiagnosticEventListener mLiveListener;
    private DiagnosticListener mDiagnosticListener;
    private  AwesomeSpeedometer awesomeSpeedometer;
    private AwesomeSpeedometer awesomeSpeedometerrpm;
    private static Context appcontext;



    class DiagnosticListener implements CarDiagnosticManager.OnDiagnosticEventListener {

        @Override
        public void onDiagnosticEvent(CarDiagnosticEvent carDiagnosticEvent) {
           awesomeSpeedometer.speedTo(carDiagnosticEvent.getSystemFloatSensor(FloatSensorIndex.VEHICLE_SPEED));
           awesomeSpeedometerrpm.speedTo(carDiagnosticEvent.getSystemFloatSensor(FloatSensorIndex.ENGINE_RPM));
           // Context context = MainActivity.getAppContext();
           // awesomeSpeedometer = MainActivity.getAwesomeSpeedometer();
            //awesomeSpeedometer.speedTo(70);
            Log.v(TAG, "Received Car Diagnostic Event: " + carDiagnosticEvent.toString());

        }
    }



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       
         awesomeSpeedometer= (AwesomeSpeedometer) findViewById(R.id.awesomeSpeedometer);
         awesomeSpeedometerrpm = (AwesomeSpeedometer) findViewById(R.id.awesomeSpeedometer3);


   
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            initCarApi();
        }


     
        //speedometer.speedTo(50);
        awesomeSpeedometer.speedTo(0);
        awesomeSpeedometerrpm.speedTo(0);
      

    FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initCarApi() {
        if (mCarApi != null && mCarApi.isConnected()) {
            mCarApi.disconnect();
            mCarApi = null;
        }
        mCarApi = Car.createCar(this, mConnectionListener);
        mCarApi.connect();
       
       
    }

    @Override
    protected void onStart() {
        super.onStart();
      // resumeDiagnosticManager();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
       //resumeDiagnosticManager();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
       // pauseDiagnosticManager();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCarApi != null) {
            mCarApi.disconnect();
        }
        Log.i(TAG, "onDestroy");
    }

    public Car getCar() {
        return mCarApi;
    }

    public CarDiagnosticManager getDiagnosticManager() {
        return mDiagnosticManager;
   }

    public  AwesomeSpeedometer getAwesomeSpeedometer() {
        return awesomeSpeedometer;
    }

    public static Context getAppContext() {
        return appcontext;
    }
    private final ServiceConnection mConnectionListener =
            new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    //assertMainThread();
                    Log.i(TAG, "car service disconnected");

                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    Log.i(TAG, "car service connected");
                    //assertMainThread();
                   // mConnectionWait.release();

                    try {
                        mDiagnosticManager =
                                (CarDiagnosticManager)mCarApi.getCarManager(Car.DIAGNOSTIC_SERVICE);
                        //if (mLiveListener != null) {
                        Log.i(TAG, "in resumedidonostic");
                        if (mDiagnosticListener == null) {
                            Log.i(TAG, "in resumedidonostic1");
                            mDiagnosticListener = new DiagnosticListener();
                        }

                        mDiagnosticManager.registerListener(mDiagnosticListener,
                                CarDiagnosticManager.FRAME_TYPE_LIVE,
                                CarSensorManager.SENSOR_RATE_NORMAL);
                        Log.i(TAG, "in resumedidonostic2");

                      //
                        //}
                        //  if (mFreezeListener != null) {
                        //    mDiagnosticManager.registerListener(mFreezeListener,
                        //          CarDiagnosticManager.FRAME_TYPE_FREEZE,
                        //        CarSensorManager.SENSOR_RATE_NORMAL);
                        //}
                    } catch (android.car.CarNotConnectedException e) {
                        Log.e(TAG, "Car not connected or not supported", e);
                        e.printStackTrace();

                    }
                }
            };

    private void resumeDiagnosticManager() {
        if (mDiagnosticListener == null) {
            mDiagnosticListener = new DiagnosticListener();
        }

        try {
            mDiagnosticManager.registerListener(mDiagnosticListener,
                    CarDiagnosticManager.FRAME_TYPE_LIVE,
                    CarSensorManager.SENSOR_RATE_NORMAL);
            Log.i(TAG, "in resumedidonostic4");

        }
        catch(android.car.CarNotConnectedException e) {
            Log.e(TAG, "register listener failed", e);
            e.printStackTrace();

        }
    }

    private void pauseDiagnosticManager() {
        if (mDiagnosticManager != null) {
            if (mDiagnosticListener != null) {
                mDiagnosticManager.unregisterListener(mDiagnosticListener);
            }
            //if (mFreezeListener != null) {
              //  mDiagnosticManager.unregisterListener(mFreezeListener);
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
