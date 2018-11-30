package commrkjdylarge_project.github.stepwithfriends;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "steps")
public class Step {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "numStep")
    private int numStep;

    @ColumnInfo(name = "points")
    private double point;

    @ColumnInfo(name = "calories")
    private double calories;

    @ColumnInfo(name = "mult")
    private double mult;

    public Step(){

    }

    public Step(int numStep) {
        this.numStep = numStep;
        this.calories = 0;
        this.mult = 1;
        this.point = 0;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getNumStep() {
        return numStep;
    }

    public void setNumStep(int numStep) {
        this.numStep = numStep;
    }

    public double getCalories() {
        return calories;
    }


    public double getMult() {
        return mult;
    }

    public void setMult(double mult) {
        this.mult = mult;
    }


    public void takeStep() {
        numStep++;
        calories = (numStep * 0.05);
        point += mult;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void stepReset(){
        numStep = 0;
        point = 0;
        mult = 1;
        calories = 0;
    }

    @Override
    public String toString() {
        return new StringBuilder("ID: " + id).append("\n\n").append("Steps taken: " + numStep).append("\n\n").append("Pts Earned: " + point)
                .append("\n\n").append("Calories Burned: " + calories).append("\n\n")
                .append("Current Multiplier: " + mult).toString();
    }
}
