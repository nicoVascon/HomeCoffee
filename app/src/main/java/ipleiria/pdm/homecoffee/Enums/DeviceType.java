package ipleiria.pdm.homecoffee.Enums;

public enum DeviceType {
    DIGITAL("DI", ""),
    ANALOG("AI", "%"),
    PRESENCE("P", ""),
    HUMIDITY("H", "%"),
    LUMINOSITY("L", "LUX"),
    TEMPERATURE("T", "ºC"),
    ACCELERATION("A", "m/s2"),
    PRESSURE("PR", "hPa");

    String acronym;
    String unit;

    DeviceType(String acronym, String unit){
        this.acronym = acronym;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public static DeviceType searchByAcronym(String acronym){
        for(DeviceType deviceType : values()){
            if(deviceType.acronym.equals(acronym)){
                return deviceType;
            }
        }
        return null;
    }
}
