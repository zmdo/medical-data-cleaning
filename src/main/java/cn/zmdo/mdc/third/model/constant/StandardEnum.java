package cn.zmdo.mdc.third.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public interface StandardEnum<T extends Enum<T>> {

    int code();

    @JsonValue
    String value();

}
