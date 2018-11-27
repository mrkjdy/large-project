package commrkjdylarge_project.github.stepwithfriends;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {

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

        Button stop = (Button) getView().findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionToWalk.stopClicked();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.sessionFrame, new StartFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
