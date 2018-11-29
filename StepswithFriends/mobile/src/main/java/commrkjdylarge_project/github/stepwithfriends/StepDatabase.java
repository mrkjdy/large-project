package com.example.josen.v5_step_sensor;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

@Database(entities = Step.class, version = 1)
public abstract class StepDatabase extends RoomDatabase{
    public abstract StepDao stepDao();
    public static StepDatabase mInstance;

    public static StepDatabase getInstance(Context context){
        if (mInstance == null) {
            mInstance = buildDatabase(context);
        }
        return mInstance;
    }

    private static StepDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, StepDatabase.class,"step_database")
                .addCallback(new Callback() {

                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance(context).stepDao().insertStep(new Step(0));
                            }
                        });
                    }
                })
                .build();


    }


}
