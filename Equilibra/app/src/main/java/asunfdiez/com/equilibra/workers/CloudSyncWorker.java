package asunfdiez.com.equilibra.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import asunfdiez.com.equilibra.repositories.CloudSyncRepository;
import asunfdiez.com.equilibra.utils.SharedPrefsManager;

/**
 * Worker encargado de realizar la sincronización de datos en segundo plano.
 * 
 * <p>Utiliza ListenableWorker para manejar operaciones asíncronas de Firebase 
 * sin bloquear hilos innecesariamente ni usar esperas activas.</p>
 */
public class CloudSyncWorker extends ListenableWorker {

    private static final String TAG = "CloudSyncWorker";

    public CloudSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            CloudSyncRepository syncRepository = new CloudSyncRepository(getApplicationContext());

            syncRepository.ensureAuthenticated(new CloudSyncRepository.OnAuthCompleteListener() {
                @Override
                public void onSuccess(String uid) {
                    Log.d(TAG, "Autenticado con éxito. Iniciando sincronización para UID: " + uid);
                    syncRepository.syncExpensesSync(uid);
                    // Actualizar marca de tiempo de sincronización exitosa
                    SharedPrefsManager.getInstance(getApplicationContext()).setLastSyncTime(System.currentTimeMillis());
                    completer.set(Result.success());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Fallo en la autenticación para sincronización", e);
                    completer.set(Result.retry());
                }
            });

            return "FirebaseSyncFuture";
        });
    }
}
