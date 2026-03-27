package asunfdiez.com.equilibra.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import asunfdiez.com.equilibra.models.UserProfile;

public class OnboardingController {
    private static final String TAG = "OnboardingController";
    private static final String PREFS_NAME = "equilibra_prefs";
    private static final String KEY_DATA = "onboarding_data";
    private static final String KEY_COMPLETED = "onboarding_completed";

    private final SharedPreferences prefs;

    public OnboardingController(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context no puede ser nulo");
        }
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveOnboardingData(UserProfile data) {
        if (data == null) {
            throw new IllegalArgumentException("UserProfile no puede ser nulo");
        }
        try {
            JSONObject jsonData = convertToJson(data);
            prefs.edit()
                    .putString(KEY_DATA, jsonData.toString())
                    .putBoolean(KEY_COMPLETED, true)
                    .apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error guardando datos de onboarding", e);
            throw new RuntimeException("Error guardando datos de onboarding", e);
        }
    }

    public UserProfile loadOnboardingData() {
        String json = prefs.getString(KEY_DATA, null);
        if (json == null) {
            return null;
        }
        try {
            return convertFromJson(new JSONObject(json));
        } catch (JSONException e) {
            Log.e(TAG, "Error cargando datos de onboarding", e);
            return null;
        }
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(KEY_COMPLETED, false);
    }

    public void clearOnboardingData() {
        prefs.edit()
                .remove(KEY_DATA)
                .remove(KEY_COMPLETED)
                .apply();
    }

    private JSONObject convertToJson(UserProfile data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("emotion", data.getEmotion() != null ? data.getEmotion() : "");
        json.put("mainConcern", data.getMainConcern() != null ? data.getMainConcern() : "");
        json.put("timeAvailable", data.getTimeAvailable() != null ? data.getTimeAvailable() : "");
        json.put("savingGoal", data.getSavingGoal() != null ? data.getSavingGoal() : "");
        json.put("savingAmount", data.getSavingAmount() != null ? data.getSavingAmount() : "");
        json.put("savingDate", data.getSavingDate() != null ? data.getSavingDate() : "");
        json.put("completedAt", data.getCompletedAt());
        return json;
    }

    private UserProfile convertFromJson(JSONObject json) throws JSONException {
        UserProfile data = new UserProfile();
        //data.setEmotion(json.optString("emotion", ""));
        data.setMainConcern(json.optString("mainConcern", ""));
        data.setTimeAvailable(json.optString("timeAvailable", ""));
        data.setSavingGoal(json.optString("savingGoal", ""));
        data.setSavingAmount(json.optString("savingAmount", ""));
        data.setSavingDate(json.optString("savingDate", ""));
        return data;
    }
}
