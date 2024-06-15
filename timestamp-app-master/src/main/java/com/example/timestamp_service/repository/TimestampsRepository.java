package com.example.timestamp_service.repository;

import com.example.timestamp_service.model.File;
import com.example.timestamp_service.model.dto.TimestampResponse;
import com.example.timestamp_service.model.Timestamps;
import com.example.timestamp_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimestampsRepository  extends JpaRepository<Timestamps, Long> {
    @Query("SELECT t.archiveNum AS archiveNum, t.dateAndTimeOfSigning AS dateAndTimeOfSigning, t.file AS file FROM Timestamps t WHERE t.user = :user")
    List<TimestampResponse> findAllByUser(@Param("user") User user);
    @Query("SELECT t.archiveNum AS archiveNum, t.dateAndTimeOfSigning AS dateAndTimeOfSigning, t.file AS file FROM Timestamps t")
    List<TimestampResponse> findAllTimestampResponses();
    Timestamps findByFile(File file);
    @Query("SELECT t.archiveNum AS archiveNum, t.dateAndTimeOfSigning AS dateAndTimeOfSigning, t.file AS file FROM Timestamps t WHERE t.file.fileName LIKE %:name%")
    List<TimestampResponse> findAllByFile_FileNameContaining(@Param("name") String name);
    @Query("SELECT t.archiveNum AS archiveNum, t.dateAndTimeOfSigning AS dateAndTimeOfSigning, t.file AS file FROM Timestamps t JOIN t.user u WHERE t.file.fileName LIKE %:name% AND u = :user")
    List<TimestampResponse> findAllByFile_FileNameContainingAndUser(@Param("name") String name, @Param("user") User user);
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Timestamps t JOIN t.user u WHERE t.file.fileName = :name AND u = :user")
    Boolean existsByFile_FileNameAndUser(@Param("name") String name, @Param("user") User user);

}
