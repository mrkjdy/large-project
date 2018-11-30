package commrkjdylarge_project.github.stepwithfriends;


import android.os.Bundle;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    int pStatus = 0;
    private Handler handler = new Handler();
    TextView percent;
    TextView stepsTxtView;


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

        final ProgressBar mProgress = getView().findViewById(R.id.circularProgressbar);


        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        percent = getView().findViewById(R.id.tv);
        stepsTxtView = getView().findViewById(R.id.stepTextView);

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
                            percent.setText(pStatus + "%");

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
}
