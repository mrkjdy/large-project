package commrkjdylarge_project.github.stepwithfriends;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {

    private Chronometer chronometer;
    private SessionToWalk sessionToWalk;

    public interface SessionToWalk {
        void stopClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sessionToWalk = (SessionToWalk) getParentFragment();
    }

    public SessionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chronometer = getView().findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());;
        chronometer.start();

        final Bundle args = new Bundle();

        ImageButton stop = (ImageButton) getView().findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                sessionToWalk.stopClicked();

                args.putString("Time", chronometer.getText().toString());
                ResultFragment resultFragment = new ResultFragment();
                resultFragment.setArguments(args);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.sessionFrame, resultFragment);
                fragmentTransaction.commit();
            }
        });
    }
}
