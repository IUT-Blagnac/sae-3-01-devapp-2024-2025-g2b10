package java_iot.classes;

import java.util.List;

/* Classe représentant une salle contenant des capteurs 
 * @author PAPA-PATSOUMOUDOU Matthias
*/

public class Room {
    private String name;
    private List<Sensor> sensors;

    public Room(String name, List<Sensor> sensors) {
        this.name = name;
        this.sensors = sensors;
    }

    /* Getters */
    public String getName() {
        return name;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    /* Setters */

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\n{" +
                "name=" + name + '\'' +
                ", sensors=" + sensors +
                '}';
    }
}
