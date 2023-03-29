package com.example.emotech.demoEmoTechHS.data;

import lombok.Data;
import lombok.NoArgsConstructor;
//import javax.sound.sampled.AudioFileFormat;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    @Lob
    private byte[] data;

    private String audioDetails;

    public Audio(String name, byte[] data){
        this.name = name;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setAudioDetails(String audioDetails) {
        this.audioDetails = audioDetails.toString();
    }

    public String getAudioDetails(){
        return audioDetails;
    }

}

//curl -X POST http://localhost:8080/api/audio -H 'Content-Type: application/json' -d '{ "name":"Test", "completed":false}'
//curl -X GET http://localhost:8080/api/audio
//curl -X GET http://localhost:8080/api/audio/6406b953ae69651a4a799ab5
