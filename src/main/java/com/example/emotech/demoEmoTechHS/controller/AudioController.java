package com.example.emotech.demoEmoTechHS.controller;

import com.example.emotech.demoEmoTechHS.data.Audio;
import com.example.emotech.demoEmoTechHS.service.AudioService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

@RestController
@RequestMapping("api/audio")
@AllArgsConstructor
public class AudioController {

    @Autowired
    private AudioService audioService;

    @GetMapping("all")
    public ResponseEntity<List<String>> getAllAudioNames(){

        return ResponseEntity
                .ok(audioService.getAllAudioNames());

    }
    //@GetMapping
    //public List<Audio> findAll(){
    //    return audioService.findAll();
    //}

    @GetMapping("{name}")
    public ResponseEntity<Resource> getAudioByName(@PathVariable("name") String name){

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(audioService.getAudio(name).getData()));

    }
    @GetMapping("info/{name}")
    public ResponseEntity<String> getAudioInfoByName(@PathVariable("name") String name){

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audioService.getAudio(name).getAudioDetails());

    }
    //@GetMapping("/{id}")
    //public Audio findById(@PathVariable String id){
    //    return audioService.findById(id);
    //}

    @PostMapping
    public ResponseEntity<String> saveAudio(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws IOException, UnsupportedAudioFileException {

        audioService.saveAudio(file, name);
        return ResponseEntity.ok("Audio saved successfully.");

    }
    //public Audio create(@RequestBody Audio audio){
    //    return audioService.save(audio);
    //}

    @PutMapping("/{id}")
    public Audio update(@RequestBody Audio audio){
        return audioService.save(audio);
    }

    //@DeleteMapping("/{id}")
    //public void deleteById(@PathVariable String id){
    //    audioService.deleteById(id);
    //}

}


