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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private static final String TAG = "Leaderboard";

    //vars
    private ArrayList<String> mUsrNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mUsrScores = new ArrayList<>();

    private View rootView;
    private RecyclerView recyclerView;
    private ListAdapter adapter;

    private TextView userName;
    private TextView userScore;
    private TextView userRank;
    private CircleImageView userImage;

    public String searchString = "";

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_leaderboard, container, false);

        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_lead);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userName = rootView.findViewById(R.id.tv_usr_name2);
        userScore = rootView.findViewById(R.id.tv_usr_score2);
        userImage = rootView.findViewById(R.id.usr_image2);
        userRank = rootView.findViewById(R.id.tv_lead_pos2);

        // TODO: get user info and update the user layout

        initImageBitMaps(0);

        adapter = new ListAdapter(mImages, mUsrNames, mUsrScores, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        //Use this to add buttons/values/etc
        //Use getView().findViewById();
        //Refer to SettingsFragment for an example
        final Button searcherBtn = getView().findViewById(R.id.search_cmd);
        final EditText searchBox = getView().findViewById(R.id.search_box);

        final Button worldBtn = getView().findViewById(R.id.bt_world);
        worldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initImageBitMaps(0);
                adapter.setData(mImages, mUsrNames, mUsrScores, getActivity());
                Toast.makeText(getActivity(), "Loaded World", Toast.LENGTH_SHORT).show();
            }
        });

        final Button friendBtn = getView().findViewById(R.id.bt_friends);
        friendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initImageBitMaps(1);
                adapter.setData(mImages, mUsrNames, mUsrScores, getActivity());
                Toast.makeText(getActivity(), "Loaded Friends", Toast.LENGTH_SHORT).show();
            }
        });

        final Button searchBtn = getView().findViewById(R.id.bt_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Search Button", Toast.LENGTH_SHORT).show();
                worldBtn.setVisibility(View.GONE);
                friendBtn.setVisibility(View.GONE);
                searchBtn.setVisibility(View.GONE);
                searchBox.setVisibility(View.VISIBLE);
                searcherBtn.setVisibility(View.VISIBLE);
            }
        });

        searcherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldBtn.setVisibility(View.VISIBLE);
                friendBtn.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                searchString = searchBox.getText().toString();
                searchBox.setVisibility(View.GONE);
                searcherBtn.setVisibility(View.GONE);

                searchBox.setText("");
                Toast.makeText(getActivity(), "Searching", Toast.LENGTH_SHORT).show();
                initImageBitMaps(2);
                searchString = "";
                adapter.setData(mImages, mUsrNames, mUsrScores, getActivity());
            }
        });
    }

    private void initImageBitMaps(int mode) // mode 0: world; mode 1: friends; mode 3: search;
    {

        mImages.clear();
        mUsrNames.clear();
        mUsrScores.clear();
        Log.d(TAG, "initImageBitMaps: preparing bitmaps");

        // TODO: here we would get the info from the leaderboar and make it into the entries
        // and use the boolean on the method to determine if to get all users or just friends

        switch(mode)
        {
            case 0:
                JSONArray object = ((SWFApp) getActivity().getApplication()).getTop100("global");

                for (int i = 0; i < object.length(); i++)
                {
                    try {
                        mImages.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
                        JSONObject temp = (JSONObject) object.get(i);
                        mUsrNames.add(temp.get("login").toString());
                        mUsrScores.add(temp.get("total_points").toString());
                    } catch (Exception e) {}
                }

                break;
            case 1:
                mImages.add("https://i.redd.it/0h2gm1ix6p501.jpg");
                mUsrNames.add("Mahahual");
                mUsrScores.add("999922");

                mImages.add("https://i.redd.it/k98uzl68eh501.jpg");
                mUsrNames.add("Frozen Lake");
                mUsrScores.add("999911");

                mImages.add("https://i.redd.it/glin0nwndo501.jpg");
                mUsrNames.add("White Sands Desert");
                mUsrScores.add("999900");
                break;
            case 2:
                mImages.add("https://i.redd.it/obx4zydshg601.jpg");
                mUsrNames.add("Austrailia");
                mUsrScores.add("999888");

                mImages.add("https://i.imgur.com/ZcLLrkY.jpg");
                mUsrNames.add("Washington");
                mUsrScores.add("999777");
                break;
            default:
                break;
        }
    }
}
