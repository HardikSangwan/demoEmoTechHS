package com.example.emotech.demoEmoTechHS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "An audio file with this name already exists")
public class AudioAlreadyExistsException extends RuntimeException {

}