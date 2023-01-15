package ipleiria.pdm.homecoffee.Enums;

/**
 * Enumeração para tipos de dispositivos suportados.
 */
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

    /**
     * Construtor da classe DeviceType
     * @param acronym acrônimo do tipo de dispositivo
     * @param unit unidade de medida do dispositivo
     */
    DeviceType(String acronym, String unit){
        this.acronym = acronym;
        this.unit = unit;
    }

    /**
     * Retorna a unidade de medida do dispositivo
     * @return unidade de medida do dispositivo
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Retorna o tipo de dispositivo com base no seu acrônimo
     * @param acronym acrônimo do tipo de dispositivo
     * @return tipo de dispositivo correspondente ao acrônimo fornecido
     */
    public static DeviceType searchByAcronym(String acronym){
        for(DeviceType deviceType : values()){
            if(deviceType.acronym.equals(acronym)){
                return deviceType;
            }
        }
        return null;
    }
}
