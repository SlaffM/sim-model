import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SimModel {

    private Map<Integer, String> headers;
    private static final int ROW_HEADERS_INDEX  = 0;
    private static final int ID_COLUMN_INDEX = 0;
    private ArrayList<String> notIncludedHeaders;
    private ArrayList<String> notIncludedSheets;
    private ArrayList<String> includedSheets;

    public static void main(String[] args) throws IOException {
           new SimModel().readFromExcel("./xls/sim_struct.xlsx");
    }

    private void readFromExcel(String file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        ArrayList<ArrayList<SimObject>> simObjectsCollection = new ArrayList<ArrayList<SimObject>>();

        buildNotIncludedHeaders();
        buildNotIncludedSheets();
        buildWithIncludedSheets();

        for (Sheet sheet : workbook) {

            if (notIncludedSheets.contains(sheet.getSheetName())) continue;
            //if (!includedSheets.contains(myExcellSheet.getSheetName())) continue;


            ArrayList<SimObject> simObjects = new ArrayList<SimObject>();

            for (Row rowData : sheet) {

                SimObject simObject = new SimObject();

                if (rowData.getRowNum() == ROW_HEADERS_INDEX) {
                    headers = buildHeaders(sheet);
                    continue;
                }

                for (Cell dataCell : rowData) {

                    int indxSimObject = dataCell.getColumnIndex();

                    if (indxSimObject == ID_COLUMN_INDEX) {
                        String sheetName = Formatter.GetFormattedSheetName(sheet.getSheetName());
                        simObject.setName(sheetName);
                        simObject.setID(dataCell.getStringCellValue());
                        continue;
                    }

                    if (headers.containsKey(indxSimObject)) {
                        SimObjectProperty simObjectProperty = buildSimProperty(dataCell, formulaEvaluator);
                        simObject.addProperty(indxSimObject, simObjectProperty);
                    }
                }
                simObjects.add(simObject);
            }

            simObjectsCollection.add(simObjects);
            printAllObjects(simObjects);
        }
        workbook.close();
        XMLWriter.WriteXML(simObjectsCollection, "./xls/mySIM.xml");
    }

    private void buildNotIncludedHeaders(){
        notIncludedHeaders = new ArrayList<String>();
        notIncludedHeaders.add("id_1");
        notIncludedHeaders.add("id_2");
        notIncludedHeaders.add("name_1");
        notIncludedHeaders.add("name_2");
        notIncludedHeaders.add("name_3");
    }

    private void buildNotIncludedSheets(){
        notIncludedSheets = new ArrayList<String>();
        notIncludedSheets.add("format_template");
        notIncludedSheets.add("Head(1)");
    }

    private void buildWithIncludedSheets(){
        includedSheets = new ArrayList<String>();
        includedSheets.add("VoltageLevel(3)");
    }

    private Hashtable<Integer, String> buildHeaders(Sheet sheet){
        Row rowHeaders = sheet.getRow(ROW_HEADERS_INDEX);
        Hashtable<Integer, String> headersLine = new Hashtable<Integer, String>();
        for(Cell headerName : rowHeaders){
            if (!notIncludedHeaders.contains(headerName.getStringCellValue())){
                headersLine.put(headerName.getColumnIndex(), headerName.getStringCellValue());
            }
        }
        return headersLine;
    }

    private SimObjectProperty buildSimProperty(Cell dataCell, FormulaEvaluator workbookEvaluator){

        SimObjectProperty simObjectProperty = new SimObjectProperty();

        String dataValue = "";
        boolean ref = false;

        switch (dataCell.getCellTypeEnum()){
            case NUMERIC:
                dataValue = String.valueOf(Double.valueOf(dataCell.getNumericCellValue()).intValue());
                break;
            case STRING:
                dataValue = dataCell.getStringCellValue();
                break;
            case FORMULA:
                String formulaValue = Formatter.GetStringWithoutQuotes(workbookEvaluator.evaluate(dataCell).formatAsString());
                if(isValueRef(formulaValue)){
                    dataValue = "#" + formulaValue;
                    ref = true;
                }else{
                    dataValue = formulaValue;
                }
                break;
            default:
                System.out.println("Тип неопределенной ячейки - " + dataCell.getCellTypeEnum());
                break;
        }

        simObjectProperty.setName(headers.get(dataCell.getColumnIndex()));
        simObjectProperty.setValue(dataValue);
        simObjectProperty.setRef(ref);

        return simObjectProperty;
    }

    private void printAllObjects(ArrayList<SimObject> listObjects){
        for(SimObject sObj : listObjects) {

            System.out.print(sObj.getName() + " - " + sObj.getID() + ": ");

            //sObj.propertiesToString();
            for (Map.Entry<Integer, SimObjectProperty> entry : sObj.getProperties().entrySet()){
                System.out.print(entry.getValue().getName() + " = " + entry.getValue().getValue() + ", ");
            }
            System.out.println("");
        }
    }

    private boolean isValueRef(String str){
        return str.startsWith("_");
    }

}


