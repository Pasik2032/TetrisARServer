package ru.hse.cs.tetrisar2.exception;

public class UserAlredyExistException extends Exception{
    public UserAlredyExistException(String message){
        super(message);
    }
}
