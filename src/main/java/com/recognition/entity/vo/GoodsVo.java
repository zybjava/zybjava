package com.recognition.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo {
    private String id; // 6 位的商品编号，每个商品唯一
    private double weight; // 商品单件重量，单位 g
    private double packageTolerance;//商品的容差
}
