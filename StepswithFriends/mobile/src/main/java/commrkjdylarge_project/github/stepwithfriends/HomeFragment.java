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
    String dailyGoal;
    private Handler handler = new Handler();
    TextView percent;
    TextView stepsTxtView;
    TextView calories;
    final ProgressBar mProgress = getView().findViewById(R.id.circularProgressbar);


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



        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        percent = getView().findViewById(R.id.tv);
        stepsTxtView = getView().findViewById(R.id.stepTextView);
        calories = getView().findViewById(R.id.calorieText);

        // TODO: get user info and update the user layout
        JSONObject usr = ((SWFApp) getActivity().getApplication()).getUserData("User");
        System.out.println();

        try
        {
            percent.setText(usr.get("dailyGoal").toString());
        } catch (Exception e) {}



        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus < 100) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);

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
        String step = args.getString("val1");
        stepsTxtView.setText(step);

    }

    public void putPercent(Bundle args){
        int percent = args.getInt("val1");
        mProgress.setProgress(percent);

    }
}
