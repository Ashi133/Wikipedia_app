package com.android.wikipedia.Database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {
    @Query (" SELECT * FROM ASHI")
    List<Entities> getAllData();
    @Insert
    void insertData(Entities entities);
    @Delete
    void deleteAllData(Entities entities);
    @Delete
    void delete(ArrayList<Entities> entities);
    @Query("UPDATE ASHI SET Title = :dTitle ,Url =:dUrl")
    void update(String dTitle,String dUrl);
    @Query("SELECT * FROM ASHI WHERE Id = :dId")
    int getId(int dId);
}
