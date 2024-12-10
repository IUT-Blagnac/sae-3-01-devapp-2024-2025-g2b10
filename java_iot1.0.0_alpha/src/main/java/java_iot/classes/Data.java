package java_iot.classes;

import java.util.HashMap;
import java.util.Map;

/* Classe représentant les données de capteurs
 * 
 * @author PAPA-PATSOUMOUDOU Matthias
*/

public class Data {
    private Global global;
    private Map<String, Room> rooms;

    /* Constructeur */
    public Data(Global global, Map<String, Room> rooms) {
        this.global = global;
        this.rooms = rooms;
    }

    /* Getters */
    public Global getglobal() {
        return global;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    /* Setters */

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }

    public void setglobal(Global global) {
        this.global = global;
    }

    /**
     * Récupère tous les capteurs en alerte (status = true) dans toutes les
     * pièces.
     *
     * @return Une map où la clé est le nom de la salle et la valeur est le
     *         capteur en alerte.
     */
    public Map<String, Sensor> getAlertingSensors() {
        Map<String, Sensor> alertingSensors = new HashMap<>();
        for (Room room : rooms.values()) {
            for (Sensor sensor : room.getSensors()) {
                if (Boolean.TRUE.equals(sensor.getStatus())) { // Vérifie si le capteur est en alerte
                    alertingSensors.put(room.getName(), sensor);
                }
            }
        }
        return alertingSensors;
    }

    @Override
    public String toString() {
        return "{"
                + "global=" + global
                + ", rooms=" + rooms
                + '}';
    }
}
