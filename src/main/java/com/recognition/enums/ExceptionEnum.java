package com.recognition.enums;

public enum ExceptionEnum {
    sensorException("传感器异常"),
    differentException("异物异常"),
    UnableRecognizeException("无法识别异常")
    ;

    private String description;
    ExceptionEnum(String description){
        this.description=description;
    }

    public String getDescription() {
        return description;
    }
}
