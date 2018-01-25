import java.util.ArrayList;

public class SimModel {

    private ArrayList<ArrayList<SimObject>> list;

    SimModel(){
        list = new ArrayList<>();
    }

    public ArrayList<ArrayList<SimObject>> getList() {
        return list;
    }

    public void setList(ArrayList<ArrayList<SimObject>> list) {
        this.list = list;
    }

    public void addToList(ArrayList<SimObject> listOfSimObjects){
        list.add(listOfSimObjects);
    }
}
