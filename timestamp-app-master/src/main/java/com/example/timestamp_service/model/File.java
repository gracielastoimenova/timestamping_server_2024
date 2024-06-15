package com.example.timestamp_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private byte[] fileContent;


    public File() {
    }

    public File(String file_name, byte[] file_content) {
        this.fileName = file_name;
        this.fileContent = file_content;
    }

}
