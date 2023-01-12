package ipleiria.pdm.homecoffee.Enums;

public enum DeviceType {
    DIGITAL("DI"),
    ANALOG("AI"),
    PRESENCE("P"),
    HUMIDITY("H"),
    LUMINOSITY("L"),
    TEMPERATURE("T"),
    ACCELERATION("A"),
    PRESSURE("PR");

    String Acronym;

    DeviceType(String acronym){
        this.Acronym = acronym;
    }

    public static DeviceType searchByAcronym(String acronym){
        for(DeviceType deviceType : values()){
            if(deviceType.Acronym.equals(acronym)){
                return deviceType;
            }
        }
        return null;
    }
}
