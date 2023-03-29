package com.example.emotech.demoEmoTechHS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "An audio file with that id was not found")
public class AudioNotFoundException extends RuntimeException{


}
