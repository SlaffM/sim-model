import com.sun.org.apache.xml.internal.utils.NameSpace;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class XMLWriter {

    private String file;
    private SimModel simModel;

    XMLWriter(SimModel simModel, String file){
        this.simModel = simModel;
        this.file = file;
    }

    private Document createDocument(SimModel simModel, String file){

        Namespace rdf = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Namespace cim = Namespace.getNamespace("cim", "http://iec.ch/TC57/2003/CIM-schema-cim10#");
        Namespace pti = Namespace.getNamespace("pti", "http://www.pti-us.com/PTI_CIM-schema-cim10#");
        Namespace fgc = Namespace.getNamespace("fgc", "http://fsk-ess.ru/cim-extension/2007/cimex10#");

        Map<String, String> nsTable = new Hashtable<>();
        nsTable.put(rdf.getPrefix(), rdf.getURI());
        nsTable.put(cim.getPrefix(), cim.getURI());
        nsTable.put(pti.getPrefix(), pti.getURI());
        nsTable.put(fgc.getPrefix(), fgc.getURI());

        Element simRootElement = new Element("RDF", rdf);
        simRootElement.addNamespaceDeclaration(rdf);
        simRootElement.addNamespaceDeclaration(cim);
        simRootElement.addNamespaceDeclaration(pti);
        simRootElement.addNamespaceDeclaration(fgc);

        for(ArrayList simObjects : simModel.getList()) {

            for (Object sObj : simObjects) {

                SimObject simObject = (SimObject)sObj;

                ElementNameBuilder elementNameSimObject = new ElementNameBuilder(simObject.getName());
                Namespace nsElemSimObj = Namespace.getNamespace(elementNameSimObject.getNS(), nsTable.get(elementNameSimObject.getNS()));

                Element simElement = new Element(simObject.getName(), nsElemSimObj).setAttribute("ID", simObject.getID(), rdf);

                for (Map.Entry<Integer, SimObjectProperty> objProp : simObject.getProperties().entrySet()) {
                    SimObjectProperty simObjectProperty = objProp.getValue();

                    ElementNameBuilder elementNameProperty = new ElementNameBuilder(simObjectProperty.getName());
                    Namespace nsElemProp = Namespace.getNamespace(elementNameProperty.getNS(), nsTable.get(elementNameProperty.getNS()));

                    Element elementProperty = new Element(elementNameProperty.getName(), nsElemProp);

                    if (simObjectProperty.isRef()) {
                        elementProperty.setAttribute("resource", simObjectProperty.getValue(), rdf);
                    } else {
                        elementProperty.setName(elementNameProperty.getName()).addContent(simObjectProperty.getValue());
                    }

                    simElement.addContent(elementProperty);
                }

                simRootElement.addContent(simElement);
            }
        }
        return new Document(simRootElement);
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
}
