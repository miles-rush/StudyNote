package com.study.properties;

import com.study.properties.property.Weight;
import org.springframework.core.convert.converter.Converter;


/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/21
 */
public class WeightConverter implements Converter<String, Weight> {
    @Override
    public Weight convert(String s) {
        return new Weight(s.toCharArray()[0]);
    }
}
