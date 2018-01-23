import java.io.Serializable;
import java.util.Map;

public class SimObject implements Serializable{
    private String Name;
    private String ID;
    private Map<Integer, SimObjectProperty> Properties;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Map<Integer, SimObjectProperty> getProperties() {
        return Properties;
    }

    public void setProperties(Map<Integer, SimObjectProperty> properties) {
        Properties = properties;
    }

    public void addProperty(int key, SimObjectProperty prop){
        Properties.put(key, prop);
    }

    @Override
    public String toString() {
        return "SimObject{" +
                "Name='" + Name + '\'' +
                ", ID='" + ID + '\'' +
                ", Properties=" + Properties +
                '}';
    }
}