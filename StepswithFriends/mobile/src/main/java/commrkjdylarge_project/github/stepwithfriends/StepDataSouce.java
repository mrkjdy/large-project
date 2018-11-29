package commrkjdylarge_project.github.stepwithfriends;

import java.util.List;

import io.reactivex.Flowable;

public class StepDataSouce implements IStepDataSource {

    private StepDao stepDao;
    private static StepDataSouce mInstance;


    public StepDataSouce(StepDao stepDao) {
        this.stepDao = stepDao;
    }

    public static StepDataSouce getInstance(StepDao stepDao){
        if(mInstance == null){
            mInstance = new StepDataSouce(stepDao);
        }

        return mInstance;
    }

    @Override
    public Flowable<List<Step>> getAllSteps() {
        return stepDao.getAllSteps();
    }

    @Override
    public Step getStepById(int userId){
        return stepDao.getStepById(userId);
    }

    @Override
    public void insertStep(Step... steps) {
        stepDao.insertStep(steps);
    }

    @Override
    public void updateStep(Step... steps) {
        stepDao.updateStep(steps);
    }

    @Override
    public void deleteStep(Step step) {
        stepDao.deleteStep(step);
    }
}
