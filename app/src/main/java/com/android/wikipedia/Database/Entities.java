package com.android.wikipedia.Database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@SuppressWarnings("ALL")
@Entity(tableName = "ASHI")
public class Entities {
    @PrimaryKey(autoGenerate = true)
    int I;
    @ColumnInfo(name = "Id")
    int id;
    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "Url")
    private String url;
    public Entities(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
