package com.example.josen.v5_step_sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private ListView lstStep;
    List<Step> stepList = new ArrayList<>();
    ArrayAdapter adapter;
    static Step cStep;
    boolean created = false;
    // sensors
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private Button btnMult1;
    private Button btnMult2;

    //database
    private CompositeDisposable compositeDisposable;
    private StepRepository stepRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compositeDisposable = new CompositeDisposable();
        lstStep = (ListView)findViewById(R.id.lstSteps);
        btnMult1 = (Button) findViewById(R.id.btnMult1);
        btnMult2 = (Button) findViewById(R.id.btnMult2);

        // Adapter
        adapter = new ArrayAdapter(this, R.layout.list_rows, R.id.info, stepList);
        registerForContextMenu(lstStep);
        lstStep.setAdapter(adapter);

        // Database
        StepDatabase stepDatabase = StepDatabase.getInstance(this);
        stepRepository = StepRepository.getInstance(StepDataSouce.getInstance(stepDatabase.stepDao()));
        new getAsyncTask(stepDatabase).execute();

        // load all data
        loadData();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        btnMult1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cStep.setMult(1);
                updateStep(cStep);
            }
        });

        btnMult2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cStep.setMult(2);
                updateStep(cStep);
            }
        });


    }


//    @Override
//    protected void onStart() {
//        super.onStart();  // Always call the superclass method first
//        Toast.makeText(getApplicationContext(), "onStart called", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();  // Always call the superclass method first
//        Intent mStepsIntent = new Intent(getApplicationContext(), StepService.class);
//        startService(mStepsIntent);
//        //Toast.makeText(getApplicationContext(), "onStop called", Toast.LENGTH_LONG).show();
//    }


    private static class getAsyncTask extends AsyncTask<Void, Void, Step> {

        private StepDatabase db;

        getAsyncTask(StepDatabase db) {
            this.db = db;
        }

        @Override
        protected Step doInBackground(Void... params) {
            try{
                StepDao dao = db.stepDao();
                cStep = dao.getStepById(0);
            }
            catch (Exception e){
                System.out.println(e);
            }

            return cStep;
        }

    }

    private void loadData() {
        Disposable disposable = stepRepository.getAllSteps()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Step>>() {
                    @Override
                    public void accept(List<Step> users) throws Exception {
                        onGetAllUserSuccess(users);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        compositeDisposable.add(disposable);
    }

    private void onGetAllUserSuccess(List<Step> users) {
        stepList.clear();
        stepList.addAll(users);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        takeStep();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void takeStep() {
        if(created){
            cStep.takeStep();
            updateStep(cStep);
        }
        created = true;
    }

    private void updateStep(final Step step) {
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                stepRepository.updateStep(step);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                loadData(); //refresh data
                            }
                        }

                );

        compositeDisposable.add(disposable);
    }
}
