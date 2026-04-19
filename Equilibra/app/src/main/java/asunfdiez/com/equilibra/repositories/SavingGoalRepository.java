package asunfdiez.com.equilibra.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import asunfdiez.com.equilibra.database.AppDatabase;
import asunfdiez.com.equilibra.database.SavingGoalDao;
import asunfdiez.com.equilibra.models.SavingGoal;

public class SavingGoalRepository {
    private final SavingGoalDao savingGoalDao;

    public SavingGoalRepository(Context context) {
        this.savingGoalDao = AppDatabase.getInstance(context).savingGoalDao();
    }

    public SavingGoal getActiveSavingGoalSync(long userId) {
        return savingGoalDao.getFirstSavingGoalActiveByUserId(userId);
    }
}
