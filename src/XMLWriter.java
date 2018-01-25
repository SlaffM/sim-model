import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class XMLWriter {

    private String file;
    private SimModel simModel;

    XMLWriter(SimModel simModel, String file){
        this.simModel = simModel;
        this.file = file;
    }

    private Document createDocument(SimModel simModel, String file){

        Element simRootElement = new Element("SimROOT");

        for(ArrayList simObjects : simModel.getList()) {

            for (Object sObj : simObjects) {

                SimObject simObject = (SimObject)sObj;

                Element simElement = new Element(simObject.getName()).setAttribute("rdf_ID", simObject.getID());

                for (Map.Entry<Integer, SimObjectProperty> objProp : simObject.getProperties().entrySet()) {
                    SimObjectProperty simObjectProperty = objProp.getValue();

                    String propertyName = Formatter.ReplaceColonOnUnderlining(simObjectProperty.getName());
                    Element elementProperty = new Element(propertyName);

                    if (simObjectProperty.isRef()) {
                        elementProperty.setAttribute("rdf_resource", simObjectProperty.getValue());
                    } else {
                        elementProperty.setName(propertyName).addContent(simObjectProperty.getValue());
                    }

                    simElement.addContent(elementProperty);
                }

                simRootElement.addContent(simElement);
            }
        }
        Document myDocument = new Document(simRootElement);
        return myDocument;
        //writeDocument(myDocument);
    }

    public void WriteModel(){
        Document document = createDocument(this.getSimModel(), this.getFile());
        writeDocument(document);
    }

    private void writeDocument(Document document){
        try {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

            FileWriter writer = new FileWriter(file);
            xmlOutputter.output(document, writer);
            writer.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private String getFile() {
        return file;
    }

    private SimModel getSimModel() {
        return simModel;
    }

    private void setSimModel(SimModel simModel) {
        this.simModel = simModel;
    }
}
