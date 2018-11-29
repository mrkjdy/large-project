package commrkjdylarge_project.github.stepwithfriends;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.pm.ActivityInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private FrameLayout mainFrame;
    private SettingsFragment settingsFrame;
    private HomeFragment homeFrame;
    private WalkFragment walkFrame;
    private LeaderboardFragment leaderboardFrame;

    // alex stuff
    List<Step> stepList = new ArrayList<>();
    static Step cStep;
    boolean created = false;
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private CompositeDisposable compositeDisposable;
    private StepRepository stepRepository;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrame = findViewById(R.id.main_frame);
        settingsFrame = new SettingsFragment();
        homeFrame = new HomeFragment();
        walkFrame = new WalkFragment();
        leaderboardFrame = new LeaderboardFragment();

        setFragment(homeFrame);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.action_home:
                        setFragment(homeFrame);
                        break;
                    case R.id.action_settings:
                        setFragment(settingsFrame);
                        break;
                    case R.id.action_walk:
                        if(isServicesOK()) {
                            setFragment(walkFrame);
                        }
                        break;
                    case R.id.action_leaderboard:
                        setFragment(leaderboardFrame);
                        break;
                }
                return true ;
            }
        });

        switch (getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                }
                break;
        }

        // alex stuff
        compositeDisposable = new CompositeDisposable();
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


    }

    private void setFragment(android.support.v4.app.Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    // Make sure google play services is available, need to verify this
    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // Step counter stuff dont touch

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

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
        getSteps();
        //adapter.notifyDataSetChanged();
    }

    public void getSteps(){

            String steps = "" + cStep.getNumStep();
            Bundle args = new Bundle();
            args.putString("steps",steps);
            homeFrame.putArgument(args);
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
