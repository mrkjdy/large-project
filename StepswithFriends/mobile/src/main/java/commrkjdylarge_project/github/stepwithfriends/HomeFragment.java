package commrkjdylarge_project.github.stepwithfriends;


import android.os.Bundle;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "Home";
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView percentTxtView;
    TextView stepsTxtView;
    TextView calorieTxtView;
    TextView pointsTxtView;
    TextView milesTxtView;
    TextView bonusTxtView;
    ProgressBar mProgress;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Use this to add buttons/values/etc
        //Use getView().findViewById();
        //Refer to SettingsFragment for an example

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circle);

        mProgress = getView().findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);


        percentTxtView = getView().findViewById(R.id.tv);
        stepsTxtView = getView().findViewById(R.id.stepTextView);
        calorieTxtView = getView().findViewById(R.id.calorieText);
        pointsTxtView = getView().findViewById(R.id.scoreText);
        milesTxtView = getView().findViewById(R.id.milesText);
        bonusTxtView = getView().findViewById(R.id.bonusText);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stubf
                while (pStatus < 100) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //mProgress.setProgress(pStatus);

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(16); //thread will take approx 3 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void putArgument(Bundle args){
        int step = args.getInt("steps");
        double cal =  args.getDouble("calories");
        double pts = args.getDouble("Points");
        double percent = args.getDouble("percent");
        double miles = ((double) step / (double )2000);
        int dailyGoal = args.getInt("dailyGoal");
        int bonus = args.getInt("bonus");


        stepsTxtView.setText("" + step);
        percentTxtView.setText("/" + dailyGoal);
        calorieTxtView.setText(""+(int)cal);
        milesTxtView.setText(String.format( "%.2f", miles ));
        pointsTxtView.setText("" +(int) pts);
        bonusTxtView.setText("" + bonus);
        mProgress.setProgress((int) percent);
    }

}
