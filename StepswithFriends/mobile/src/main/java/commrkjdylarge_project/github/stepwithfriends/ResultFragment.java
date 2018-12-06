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

import org.w3c.dom.Text;


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
        String step = b.getString("Step");
        String mile = b.getString("Mile");
        String score = b.getString("Score");
        String multiplier = b.getString("Multiplier");

        TextView timeView = (TextView) getView().findViewById(R.id.timeResult);
        TextView stepView = (TextView) getView().findViewById(R.id.stepResult);
        TextView mileView = (TextView) getView().findViewById(R.id.milesResult);
        TextView scoreView = (TextView) getView().findViewById(R.id.scoreResult);
        TextView multiplierView = (TextView) getView().findViewById(R.id.multiplierResult);

        timeView.setText(time);
        stepView.setText(step);
        mileView.setText(mile);
        scoreView.setText(score);
        multiplierView.setText(multiplier);

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
