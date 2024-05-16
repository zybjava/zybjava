package com.recognition.entity;

import com.recognition.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecognitionException {
    private int layer;
    // 枚举值，自行发挥
    private ExceptionEnum exception;
    private int beginWeight;
    private int endWeight;
}
