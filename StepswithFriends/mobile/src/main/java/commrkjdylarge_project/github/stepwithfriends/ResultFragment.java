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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    private ResultToWalk resultToWalk;

    public interface ResultToWalk {
        void endClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resultToWalk = (ResultToWalk) getParentFragment();

    }

    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = getArguments();
        String time = b.getString("Time");

        TextView timeView = (TextView) getView().findViewById(R.id.timeView);
        timeView.setText(time);

        Button end = (Button) getView().findViewById(R.id.endButton);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultToWalk.endClicked();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.sessionFrame, new StartFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
