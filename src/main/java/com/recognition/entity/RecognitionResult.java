package com.recognition.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecognitionResult {
    // 没有任何异常时，为 true
    private boolean successful;
    // 允许 empty 不允许 null；成功且为 empty 时即为无购物
    private List<RecognitionItem> items;
    // 允许 empty 不允许 null
    private List<RecognitionException> exceptions;
}
