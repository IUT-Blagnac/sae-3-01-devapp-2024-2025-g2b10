package java_iot.classes;

import java.util.List;

public class Room {
    private String name;
    private List<Sensor> sensors;

    public Room(String name, List<Sensor> sensors) {
        this.name = name;
        this.sensors = sensors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public String toString() {
        return "\n{" +
                "name=" + name + '\'' +
                ", sensors=" + sensors +
                '}';
    }
}
