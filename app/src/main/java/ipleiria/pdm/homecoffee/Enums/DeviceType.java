package ipleiria.pdm.homecoffee.Enums;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public enum DeviceType {
    HUMIDITY,
    LIGHT,
    TEMPERATURE,
    ACCELERATION,
    PRESSURE;

    @NonNull
    @Override
    public String toString() {
        return super.toString() + " SENSOR";
    }
}
