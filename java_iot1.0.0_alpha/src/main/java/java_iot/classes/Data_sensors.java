package java_iot.classes;

public class Data_sensors {

    private String roomName;
    private Double co2;
    private Double humidity;
    private Double temperature;
    private Double value;
    private Boolean status;
    private String time;

    // Constructeur
    public Data_sensors(String roomName, Double co2, Double humidity, Double temperature, String time) {
        this.roomName = roomName;
        this.co2 = co2;
        this.humidity = humidity;
        this.temperature = temperature;
        this.time = time;
    }

    // Getters
    public String getRoomName() {
        return roomName;
    }

    public Double getCo2() {
        return co2;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getTime() {
        return time;
    }

}