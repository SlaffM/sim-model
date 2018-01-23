import java.io.Serializable;

public class SimObjectProperty implements Serializable{

    private String Name;
    private String Value;
    private boolean ref;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public boolean isRef() {
        return ref;
    }

    public void setRef(boolean ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "SimObjectProperty{" +
                "Name='" + Name + '\'' +
                ", Value='" + Value + '\'' +
                ", ref=" + ref +
                '}';
    }
}
