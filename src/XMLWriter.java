import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class XMLWriter {

    private static String file;

    public static void WriteXML(ArrayList<ArrayList<SimObject>> sheets, String file){

        setFile(file);

        Element rootElement = new Element("root");

        for(ArrayList sheetClass : sheets) {

            for (Object sObj : sheetClass) {

                SimObject simObject = (SimObject)sObj;

                Element simElement = new Element(simObject.getName()).setAttribute("rdf_ID", simObject.getID());

                for (Map.Entry<Integer, SimObjectProperty> objProp : simObject.getProperties().entrySet()) {
                    SimObjectProperty simObjectProperty = objProp.getValue();

                    String propertyName = simObjectProperty.getName().replaceAll(":", "_");
                    Element elementProperty = new Element(propertyName);

                    if (simObjectProperty.isRef()) {
                        elementProperty.setAttribute("rdf_resource", simObjectProperty.getValue());
                    } else {
                        elementProperty.setName(propertyName).addContent(simObjectProperty.getValue());
                    }

                    simElement.addContent(elementProperty);
                }

                rootElement.addContent(simElement);
            }
        }
        Document myDocument = new Document(rootElement);

        printXml(myDocument);
    }

    private static void printXml(Document document){
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

            FileWriter writer = new FileWriter(file);
            outputter.output(document, writer);
            writer.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static void setFile(String file) {
        XMLWriter.file = file;
    }
}
