package commrkjdylarge_project.github.stepwithfriends;

import java.util.List;

import io.reactivex.Flowable;

public interface IStepDataSource {

    Flowable<List<Step>> getAllSteps();
    Step getStepById(int userId);
    void insertStep(Step... steps);
    void updateStep(Step... steps);
    void deleteStep(Step step);
}
