import java.util.Map;

public class ElementNameBuilder {

    private String NS;
    private String Name;

    ElementNameBuilder(String sourceName){
        setNamespaceParts(sourceName);
    }

    private void setNamespaceParts(String str){
        if(str.contains(":")){
            this.setNS(str.split(":")[0]);
            this.setName(str.split(":")[1]);
        }else{
            this.setNS("cim");
            this.setName(str);
        }
    }

    public String getNS() {
        return NS;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNS(String ns) {
        NS = ns;
    }
}
