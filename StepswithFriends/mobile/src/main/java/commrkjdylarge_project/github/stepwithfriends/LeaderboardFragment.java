package commrkjdylarge_project.github.stepwithfriends;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private static final String TAG = "Leaderboard";

    //vars
    private ArrayList<String> mUsrNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mUsrScores = new ArrayList<>();

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_leaderboard, container, false);

        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_lead);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initImageBitMaps();

        ListAdapter adapter = new ListAdapter(mImages, mUsrNames, mUsrScores, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        //Use this to add buttons/values/etc
        //Use getView().findViewById();
        //Refer to SettingsFragment for an example
    }

    private void initImageBitMaps()
    {
        Log.d(TAG, "initImageBitMaps: preparing bitmaps");

        // TODO: here we would get the info from the leaderboar and make it into the entries


        // TODO: delete untill the End comment after the database has been incorporated -this section is just for testing-
        mImages.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mUsrNames.add("Havasu Falls");
        mUsrScores.add("999999");

        mImages.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mUsrNames.add("Trondheim");
        mUsrScores.add("999967");

        mImages.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mUsrNames.add("Portugal");
        mUsrScores.add("999944");

        mImages.add("https://i.redd.it/j6myfqglup501.jpg");
        mUsrNames.add("Rocky Mountain");
        mUsrScores.add("999933");

        mImages.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mUsrNames.add("Mahahual");
        mUsrScores.add("999922");

        mImages.add("https://i.redd.it/k98uzl68eh501.jpg");
        mUsrNames.add("Frozen Lake");
        mUsrScores.add("999911");

        mImages.add("https://i.redd.it/glin0nwndo501.jpg");
        mUsrNames.add("White Sands Desert");
        mUsrScores.add("999900");

        mImages.add("https://i.redd.it/obx4zydshg601.jpg");
        mUsrNames.add("Austrailia");
        mUsrScores.add("999888");

        mImages.add("https://i.imgur.com/ZcLLrkY.jpg");
        mUsrNames.add("Washington");
        mUsrScores.add("999777");
        // End
    }
}
