import java.util.ArrayList;

public class SimModel {

    private ArrayList<ArrayList<SimObject>> list;

    SimModel(){
        list = new ArrayList<>();
    }

    public ArrayList<ArrayList<SimObject>> getList() {
        return list;
    }

    public void addSimObjectsToModel(ArrayList<SimObject> listOfSimObjects){
        list.add(listOfSimObjects);
    }
}
