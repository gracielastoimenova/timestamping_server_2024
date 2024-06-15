package com.example.timestamp_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Entity
public class Timestamps {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long archiveNum;
    @DateTimeFormat
    private String dateAndTimeOfSigning;
    @ManyToOne(fetch=FetchType.EAGER)
    private User user;
    @OneToOne
    private File file;
    private byte[] timestampedFile;


    public Timestamps() {
    }
    public Timestamps(String dateAndTimeOfSigning, User user, File file, byte[] timestampedFile) {
        this.dateAndTimeOfSigning = dateAndTimeOfSigning;
        this.user = user;
        this.file = file;
        this.timestampedFile = timestampedFile;
    }

}
