package com.study.properties.property;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/21
 */
@Data
public class Weight {
    private int w;
    public Weight(char w) {
        this.w = Integer.parseInt(w + "");
    }
}
