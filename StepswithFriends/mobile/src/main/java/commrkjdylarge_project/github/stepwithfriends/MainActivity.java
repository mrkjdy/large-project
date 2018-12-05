package commrkjdylarge_project.github.stepwithfriends;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

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

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SessionFragment.SessionToActivity, StartFragment.StartToActivity {

    // bonuses  and back allow Vars
    private int backAllow = 1;
    private int bonus1 = 1000;
    private int bonus2 = 500;
    private int bonus3 = 1500;
    private int bonusFlag1 = 0;
    private int bonusFlag2 = 0;
    private int bonusFlag3 = 0;
    private int dailyGoal = 0;
    private int totalPoints = 0;

    // layout vars
    private FrameLayout mainFrame;
    private SettingsFragment settingsFrame;
    private HomeFragment homeFrame;
    private WalkFragment walkFrame;
    private LeaderboardFragment leaderboardFrame;
    private BottomNavigationView bottomNavigationView;

    // Step Detector Vars
    List<Step> stepList = new ArrayList<>();
    static Step mStep;
    boolean databaseCreated = false;
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private CompositeDisposable compositeDisposable;
    private StepRepository stepRepository;
    // error code
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Used Fragments
        mainFrame = findViewById(R.id.main_frame);
        settingsFrame = new SettingsFragment();
        homeFrame = new HomeFragment();
        walkFrame = new WalkFragment();
        leaderboardFrame = new LeaderboardFragment();
        setFragment(1);

        // Local Database Vars
        compositeDisposable = new CompositeDisposable();
        StepDatabase stepDatabase = StepDatabase.getInstance(this);
        stepRepository = StepRepository.getInstance(StepDataSouce.getInstance(stepDatabase.stepDao()));
        new getAsyncTask(stepDatabase).execute();
        // load all data
        loadData();

        // Step Detector Sensor Var
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.action_home:
                        setFragment(1);
                        break;
                    case R.id.action_settings:
                        setFragment(4);
                        break;
                    case R.id.action_walk:
                        if(isServicesOK()) {
                           setFragment(2);
                        }
                        break;
                    case R.id.action_leaderboard:
                        setFragment(3);
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

        // retrieves User data from remote database
        JSONObject usr = ((SWFApp) getApplication()).getUserData("User");
        try
        {
            dailyGoal = Integer.parseInt(usr.get("dailyGoal").toString());
            totalPoints = Integer.parseInt(usr.get("total_points").toString());
        } catch (Exception e) {}

        Toast.makeText(this, "" + totalPoints, Toast.LENGTH_SHORT).show();

    }


    private void setFragment(int position){
        android.support.v4.app.FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();

        switch(position) {
            case 1:
                if(fragmentManager.findFragmentByTag("one") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.main_frame, homeFrame, "one").commit();
                }
                if(fragmentManager.findFragmentByTag("two") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
                }
                if(fragmentManager.findFragmentByTag("three") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("three")).commit();
                }
                if(fragmentManager.findFragmentByTag("four") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("four")).commit();
                }
                break;
            case 2:
                if(fragmentManager.findFragmentByTag("two") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("two")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.main_frame, walkFrame, "two").commit();
                }
                if(fragmentManager.findFragmentByTag("one") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("one")).commit();
                }
                if(fragmentManager.findFragmentByTag("three") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("three")).commit();
                }
                if(fragmentManager.findFragmentByTag("four") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("four")).commit();
                }
                break;
            case 3:
                if(fragmentManager.findFragmentByTag("three") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.main_frame, leaderboardFrame, "three").commit();
                }
                if(fragmentManager.findFragmentByTag("one") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("one")).commit();
                }
                if(fragmentManager.findFragmentByTag("two") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
                }
                if(fragmentManager.findFragmentByTag("four") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("four")).commit();
                }
                break;
            case 4:
                if(fragmentManager.findFragmentByTag("four") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("four")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.main_frame, settingsFrame, "four").commit();
                }
                if(fragmentManager.findFragmentByTag("one") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("one")).commit();
                }
                if(fragmentManager.findFragmentByTag("two") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
                }
                if(fragmentManager.findFragmentByTag("three") != null){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("three")).commit();
                }
                break;
        }

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


    // Sensor Detection Functions
    @Override
    public void onSensorChanged(SensorEvent event) {
        takeStep();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Accesses Local Database without locking UI thread
    private static class getAsyncTask extends AsyncTask<Void, Void, Step> {

        private StepDatabase db;

        getAsyncTask(StepDatabase db) {
            this.db = db;
        }

        @Override
        protected Step doInBackground(Void... params) {
            try{
                StepDao dao = db.stepDao();
                mStep = dao.getStepById(0);
            }
            catch (Exception e){
                System.out.println(e);
            }

            return mStep;
        }
    }

    // loads step data to be displayed
    private void loadData() {
        Disposable disposable = stepRepository.getAllSteps()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Step>>() {
                    @Override
                    public void accept(List<Step> steps) throws Exception {
                        onGotStepSuccessfully(steps);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        compositeDisposable.add(disposable);
    }

    // support method after verifying step retrieval
    private void onGotStepSuccessfully(List<Step> steps) {
        stepList.clear();
        stepList.addAll(steps);
        getSteps();
    }


    public void getSteps(){
        Bundle args = new Bundle();
        int steps = mStep.getNumStep();
        double cal = mStep.getCalories();
        double points = mStep.getPoint();
        double percent = round(((double) steps / (double) dailyGoal) * 100);
        int bonus = 0;


        if(steps >= dailyGoal){
            bonus += bonus1;

            if(bonusFlag1 == 0){
                bonusFlag1 = 1;
                Toast.makeText(this, "Hit first bonus goal! Added 1000 points!", Toast.LENGTH_SHORT).show();
            }
        }

        if(steps >= (dailyGoal + 25)){
            bonus += bonus2;

            if(bonusFlag2 == 0){
                bonusFlag2 = 1;
                Toast.makeText(this, "Hit Second bonus goal! Added 500 points!", Toast.LENGTH_SHORT).show();
            }
        }

        if(steps >= (dailyGoal + 50)){
            bonus += bonus3;

            if(bonusFlag3 == 0){
                bonusFlag3 = 1;
                Toast.makeText(this, "Hit third bonus goal! Added 1,500 points!", Toast.LENGTH_SHORT).show();
            }
        }


        args.putInt("steps",steps);
        args.putDouble("calories",cal);
        args.putDouble("Points",points);
        args.putDouble("percent", percent);
        args.putInt("bonus", bonus);
        args.putInt("dailyGoal", dailyGoal);
        homeFrame.putArgument(args);
        ((SWFApp) getApplication()).setValues(steps, points);
    }



    private void takeStep() {
        if(databaseCreated){
            mStep.takeStep();
            updateStep(mStep);
        }
        databaseCreated = true;
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

    @Override
    public void onBackPressed() {
        if(backAllow == 1) {
            super.onBackPressed();
        } else {

        }
    }

    public void startClicked() {
        backAllow = 0;
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void stopClicked() {
        backAllow =1;
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void onDestroy() {
        super.onDestroy();

        mStep.stepReset();

    }
}