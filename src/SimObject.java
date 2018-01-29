import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

class SimObject implements Serializable{
    private String Name;
    private String ID;
    private Map<Integer, SimObjectProperty> Properties;

    SimObject(){
        Properties = new Hashtable<Integer, SimObjectProperty>();
    }

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

    public String propertiesToString(){
        SimObject simObject = this;
        String result = "";
        for (Map.Entry<Integer, SimObjectProperty> entry : simObject.getProperties().entrySet()){
            result += entry.getValue().getName() + " = " + entry.getValue().getValue() + ", ";
        }
        result += "\n";

        return result;
    }
}