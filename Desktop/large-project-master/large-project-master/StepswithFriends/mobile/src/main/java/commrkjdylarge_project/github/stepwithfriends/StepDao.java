package commrkjdylarge_project.github.stepwithfriends;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface StepDao {

    @Query("SELECT * FROM steps WHERE id =:userId")
    Step getStepById(int userId);

    @Query("SELECT * FROM steps")
    Flowable<List<Step>> getAllSteps();

    @Insert
    void insertStep(Step... steps);

    @Update
    void updateStep(Step... steps);

    @Delete
    void deleteStep(Step step);
}
