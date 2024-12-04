package java_iot.classes;

import java.util.Map;

public class Data {
    private Global global;
    private Map<String, Room> rooms;

    public Data(Global global, Map<String, Room> rooms) {
        this.global = global;
        this.rooms = rooms;
    }

    public Global getglobal() {
        return global;
    }

    public void setglobal(Global global) {
        this.global = global;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "{" +
                "global=" + global +
                ", rooms=" + rooms +
                '}';
    }
}
