package asunfdiez.com.equilibra.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import asunfdiez.com.equilibra.models.XPAuditLog;

@Dao
public interface XPAuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(XPAuditLog log);

    @Query("SELECT * FROM xp_audit_log WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<XPAuditLog>> getLogsByUserId(long userId);

    @Query("SELECT * FROM xp_audit_log WHERE userId = :userId ORDER BY timestamp DESC")
    List<XPAuditLog> getLogsByUserIdSync(long userId);
}
