package commrkjdylarge_project.github.stepwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private StartToWalk startToWalk;
    private StartToActivity startToActivity;

    public interface StartToWalk {
        void startClicked();
    }

    public interface StartToActivity {
        void startClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        startToWalk = (StartToWalk) getParentFragment();
        startToActivity = (StartToActivity) getActivity();
    }

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton start = (ImageButton) getView().findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alt = new AlertDialog.Builder(getContext());
                alt.setMessage("Do you want to start a Session?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Join or create session
                                ((SWFApp) getActivity().getApplication()).joinSession();

                                startToWalk.startClicked();
                                startToActivity.startClicked();

                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.sessionFrame, new SessionFragment());
                                fragmentTransaction.commit();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alt.create();
                alert.show();
            }
        });

    }
}
