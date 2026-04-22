package asunfdiez.com.equilibra.repositories;

import android.content.Context;

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

    public SavingGoal getFirstSavingGoalActiveByUserId(long userId) {
        return savingGoalDao.getFirstSavingGoalActiveByUserId(userId);
    }
}
