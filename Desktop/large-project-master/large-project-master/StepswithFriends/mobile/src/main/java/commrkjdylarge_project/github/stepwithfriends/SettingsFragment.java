package commrkjdylarge_project.github.stepwithfriends;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Button logoutButton = getView().findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogoutPopup.class));
            }
        });

        Button infoButton = getView().findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InfoPopup.class)); //popup or fragment
            }
        });

        Button notificationButton = getView().findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationPopup.class)); //popup or fragment
            }
        });

        final Switch publicSwitch = getView().findViewById(R.id.public_switch);
        publicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Set public statuc on leaderboard
                    if(!((SWFApp) getActivity().getApplication()).updateUserData("isPrivate", false,"User")) {
                        publicSwitch.setChecked(false);
                    }
                } else {
                    // Set private status on leaderboard
                    if(!((SWFApp) getActivity().getApplication()).updateUserData("isPrivate", true,"User")) {
                        publicSwitch.setChecked(true);
                    }
                }
            }
        });

        Switch locationSwitch = getView().findViewById(R.id.location_switch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Turn location on
                } else {
                    // Turn location off
                }
            }
        });
    }

}
