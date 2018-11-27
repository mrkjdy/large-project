package commrkjdylarge_project.github.stepwithfriends;

import java.util.List;

import io.reactivex.Flowable;

public class StepRepository implements IStepDataSource {
    private IStepDataSource mLocalDataSource;
    private static StepRepository mInstance;


    public StepRepository(IStepDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static StepRepository getInstance(IStepDataSource mLocalDataSource){
        if(mInstance == null)
            mInstance = new StepRepository(mLocalDataSource);

        return mInstance;
    }

    @Override
    public Flowable<List<Step>> getAllSteps() {
        return mLocalDataSource.getAllSteps();
    }

    @Override
    public Step getStepById(int userId) {
        return mLocalDataSource.getStepById(userId);
       // return new getAsyncTask(db).execute(userId);
    }


//    public Step getStepById(StepDatabase db, int userId){
//        return (new getAsyncTask(db, userId).execute());
//        //return mStep;
//        //mStep = new AsyncTask<>()
//        //return mLocalDataSource.getStepById(userId);
//    }

//    public Step temp(StepDatabase db, int id)
//    {
//        new getAsyncTask(db).execute(id);
//    }



    @Override
    public void insertStep(Step... steps) {
        mLocalDataSource.insertStep(steps);
    }

    @Override
    public void updateStep(Step... steps) {
        mLocalDataSource.updateStep(steps);
    }

    @Override
    public void deleteStep(Step step) {
        mLocalDataSource.deleteStep(step);
    }
}
