package asunfdiez.com.equilibra.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.List;

import asunfdiez.com.equilibra.models.InterventionLog;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.repositories.InterventionLogRepository;
import asunfdiez.com.equilibra.repositories.UserProfileRepository;

public class InterventionHistoryViewModel extends AndroidViewModel {
    private final LiveData<Boolean> hasLogs;
    private final LiveData<List<InterventionLog>> interventions;

    public InterventionHistoryViewModel(@NonNull Application application) {
        super(application);

        InterventionLogRepository logRepository =
                new InterventionLogRepository(application);
        LiveData<UserProfile> profile =
                new UserProfileRepository(application).getUserProfile();

        hasLogs = Transformations.switchMap(profile, user ->
                user == null
                        ? new MutableLiveData<>(false)
                        : logRepository.observeHasLogsForUser(user.getId()));

        interventions = Transformations.switchMap(profile, user -> {
            if (user != null) {
                return logRepository.observeByUserId(user.getId());
            }
            MutableLiveData<List<InterventionLog>> empty = new MutableLiveData<>();
            empty.setValue(Collections.emptyList());
            return empty;
        });
    }

    public LiveData<Boolean> getHasLogs() {
        return hasLogs;
    }

    public LiveData<List<InterventionLog>> getInterventions() {
        return interventions;
    }

    public void deleteLog(InterventionLog log) {
        InterventionLogRepository logRepository = new InterventionLogRepository(getApplication());
        logRepository.deleteInterventionLog(log);
    }
}