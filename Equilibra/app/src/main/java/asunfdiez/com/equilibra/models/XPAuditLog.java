package asunfdiez.com.equilibra.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "xp_audit_log",
        foreignKeys = @ForeignKey(
                entity = UserProfile.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class XPAuditLog {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;

    private long userId;
    private int xpAmount;
    private String description;
    private long timestamp;

    public XPAuditLog() {
        this.timestamp = System.currentTimeMillis();
    }

    public XPAuditLog(long userId, int xpAmount, String description) {
        this.userId = userId;
        this.xpAmount = xpAmount;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(int xpAmount) {
        this.xpAmount = xpAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
