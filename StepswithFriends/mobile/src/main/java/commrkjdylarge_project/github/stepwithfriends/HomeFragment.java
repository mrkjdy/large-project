package commrkjdylarge_project.github.stepwithfriends;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        //Use this to add buttons/values/etc
        //Use getView().findViewById();
        //Refer to SettingsFragment for an example
        final AsyncHttpClient client = ((SWFApp) getActivity().getApplication()).getClient();
        Button testButton = getView().findViewById(R.id.button_test);
        final TextView testText = getView().findViewById(R.id.text_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testText.setText(((SWFApp) getActivity().getApplication()).getUserData("User").toString());
            }
        });
    }

}
