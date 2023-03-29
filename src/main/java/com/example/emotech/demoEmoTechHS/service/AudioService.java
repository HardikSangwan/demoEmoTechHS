package com.example.emotech.demoEmoTechHS.service;

import com.example.emotech.demoEmoTechHS.data.Audio;
import com.example.emotech.demoEmoTechHS.exception.EntityNotFoundException;
import com.example.emotech.demoEmoTechHS.exception.AudioNotFoundException;
import com.example.emotech.demoEmoTechHS.exception.AudioAlreadyExistsException;
import com.example.emotech.demoEmoTechHS.repository.AudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import java.io.InputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import java.util.List;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

@Service
@AllArgsConstructor
public class AudioService {

    @Autowired
    private AudioRepository audioRepository;

    public Audio getAudio(String name) {
        if(!audioRepository.existsByName(name)){
            throw new AudioNotFoundException();
        }
        return audioRepository.findByName(name);
    }

    public List<String> getAllAudioNames() {
        return audioRepository.getAllEntryNames();
    }

    public void saveAudio(byte[] streamAud, String name) throws IOException {
        if(audioRepository.existsByName(name)){
            throw new AudioAlreadyExistsException();
        }
        Audio newAud = new Audio(name, streamAud);
        audioRepository.save(newAud);
    }

    public void saveAudio(MultipartFile file, String name) throws IOException, UnsupportedAudioFileException {
        if(audioRepository.existsByName(name)){
            throw new AudioAlreadyExistsException();
        }
        AudioFileFormat audioInDetails = null;
        try {
            InputStream initialStream = file.getInputStream();
            InputStream bufferedIn = new BufferedInputStream(initialStream);
            audioInDetails = AudioSystem.getAudioFileFormat(bufferedIn);
            System.out.println(audioInDetails.toString());
        } catch(UnsupportedAudioFileException e) {
            System.out.println(e);
        }
        Audio newAud = new Audio(name, file.getBytes());
        if (audioInDetails != null) {
            newAud.setAudioDetails(audioInDetails.toString());
        } else {
            newAud.setAudioDetails("Audio Details Unavailable (try Wav format files)");
        }
        
        audioRepository.save(newAud);
    }

    public List<Audio> findAll(){
        return audioRepository.findAll();
    }

    //public Audio findById(String id){
    //    return audioRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    //}

    public Audio save(Audio audio){
        return audioRepository.save(audio);
    }

    //public void deleteById(String id){
    //    audioRepository.deleteById(id);
    //}

}

