package java_iot.classes;

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

    @Override
    public String toString() {
        return "{" +
                "global=" + global +
                ", rooms=" + rooms +
                '}';
    }
}
