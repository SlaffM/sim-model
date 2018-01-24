import java.io.Serializable;
import java.util.Map;

public class SimObject implements Serializable{
    private String Name;
    private String ID;
    private Map<Integer, SimObjectProperty> Properties;

    String getName() {
        return Name;
    }

    void setName(String name) {
        Name = name;
    }

    String getID() {
        return ID;
    }

    void setID(String ID) {
        this.ID = ID;
    }

    Map<Integer, SimObjectProperty> getProperties() {
        return Properties;
    }

    void setProperties(Map<Integer, SimObjectProperty> properties) {
        Properties = properties;
    }

    void addProperty(int key, SimObjectProperty prop){
        Properties.put(key, prop);
    }

    @Override
    public String toString() {
        return "SimObject{" +
                "Name='" + Name + '\'' +
                ", ID='" + ID + '\'' +
                ", Properties=" + Properties.values() +
                '}';
    }
}