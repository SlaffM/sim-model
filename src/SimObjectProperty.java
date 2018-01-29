import java.io.Serializable;

class SimObjectProperty implements Serializable{

    private String Name;
    private String Value;
    private boolean ref;

    String getName() {
        return Name;
    }

    void setName(String name) {
        Name = name;
    }

    String getValue() {
        return Value;
    }

    void setValue(String value) {
        Value = value;
    }

    boolean isRef() {
        return ref;
    }

    void setRef(boolean ref) {
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
