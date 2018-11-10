package commrkjdylarge_project.github.stepwithfriends;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ProgressBar progress1, progress2;
    int step = 0;


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


    }
}
