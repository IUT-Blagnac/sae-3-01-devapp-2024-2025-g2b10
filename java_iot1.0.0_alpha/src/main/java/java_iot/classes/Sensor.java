package java_iot.classes;

public class Sensor {
    private String name;
    private Double value;
    private Boolean status;
    private String time;

    public Sensor(String name, Double value, Boolean status, String time) {
        this.name = name;
        this.value = value;
        this.status = status;
        this.time = time;

    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name +
                ", value=" + value +
                ", status=" + status + ", time :" + time +
                '}';
    }
}
