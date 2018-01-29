import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SimModelCreator {

    private Map<Integer, String> headers;
    private final int ROW_HEADERS_INDEX  = 0;
    private final int ID_COLUMN_INDEX = 0;
    private final int ID_COLUMN_CLASS_INDEX = 1;

    private static final String xlsFileModel = "./xls/sim_struct.xlsx";
    private final String rdfFileModel = "./xls/mySIM.rdf";

    private ArrayList<String> notIncludedHeaders;
    private ArrayList<String> notIncludedSheets;
    private ArrayList<String> includedSheets;

    public static void main(String[] args) throws IOException {
           new SimModelCreator().createAndWriteSimModel(xlsFileModel);
    }

    private void createAndWriteSimModel(String file) throws IOException {
        SimModel simModel = createModelFromExcel(file);
        writeSimModel(simModel);
    }

    private SimModel createModelFromExcel(String file) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        SimModel simModel = new SimModel();

        buildNotIncludedHeaders();
        buildNotIncludedSheets();
        buildWithIncludedSheets();

        for (Sheet sheet : workbook) {

            if (notIncludedSheets.contains(sheet.getSheetName())) continue;

            ArrayList<SimObject> simObjects;

            if (sheet.getSheetName().equals("Head(1)")){
                simObjects = buildSimObjectsFromHeadSheet(sheet, formulaEvaluator);
            }else{
                simObjects = buildSimObjectsFromStandartSheet(sheet, formulaEvaluator);
            }

            simModel.addSimObjectsToModel(simObjects);

            printAllObjects(simObjects);
        }
        workbook.close();

        return simModel;
    }

    private ArrayList<SimObject> buildSimObjectsFromStandartSheet(Sheet sheet, FormulaEvaluator formulaEvaluator){
        ArrayList<SimObject> simObjects = new ArrayList<>();
        for (Row rowData : sheet) {

            SimObject simObject = new SimObject();

            if (rowData.getRowNum() == ROW_HEADERS_INDEX) {
                headers = buildHeaders(rowData);
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

        return simObjects;
    }

    private ArrayList<SimObject> buildSimObjectsFromHeadSheet(Sheet sheet, FormulaEvaluator formulaEvaluator){
        ArrayList<SimObject> simObjects = new ArrayList<>();
        for (Row rowData : sheet) {

            SimObject simObject = new SimObject();

            if (rowData.getRowNum() == ROW_HEADERS_INDEX) {
                headers = buildHeaders(rowData); continue;
            }

            for (Cell dataCell : rowData) {

                int indxSimObject = dataCell.getColumnIndex();

                if (indxSimObject == ID_COLUMN_INDEX) {
                    simObject.setID(dataCell.getStringCellValue());
                    continue;
                }

                if(indxSimObject == ID_COLUMN_CLASS_INDEX){
                    String className = Formatter.ReplaceColonOnUnderlining(dataCell.getStringCellValue());
                    simObject.setName(className);
                    continue;
                }

                if (headers.containsKey(indxSimObject)) {
                    SimObjectProperty simObjectProperty = buildSimProperty(dataCell, formulaEvaluator);
                    simObject.addProperty(indxSimObject, simObjectProperty);
                }
            }

            simObjects.add(simObject);
        }

        return simObjects;
    }


    private void writeSimModel(SimModel simModel){
        XMLWriter xmlWriter = new XMLWriter(simModel, rdfFileModel);
        xmlWriter.WriteModel();
    }

    private void buildNotIncludedHeaders(){
        notIncludedHeaders = new ArrayList<>();
        notIncludedHeaders.add("id_1");
        notIncludedHeaders.add("id_2");
        notIncludedHeaders.add("name_1");
        notIncludedHeaders.add("name_2");
        notIncludedHeaders.add("name_3");
        notIncludedHeaders.add("class");
    }

    private void buildNotIncludedSheets(){
        notIncludedSheets = new ArrayList<>();
        notIncludedSheets.add("format_template");
        //notIncludedSheets.add("Head(1)");
    }

    private void buildWithIncludedSheets(){
        includedSheets = new ArrayList<>();
        includedSheets.add("VoltageLevel(3)");
    }

    private Hashtable<Integer, String> buildHeaders(Row rowHeaders){
        Hashtable<Integer, String> headersLine = new Hashtable<>();
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

    private void printAllObjects(ArrayList<SimObject> simObjects){
        for(SimObject simObject : simObjects) {

            System.out.print(simObject.getName() + " - " + simObject.getID() + ": ");

            simObject.propertiesToString();

            for (Map.Entry<Integer, SimObjectProperty> entry : simObject.getProperties().entrySet()){
                System.out.print(entry.getValue().getName() + " = " + entry.getValue().getValue() + ", ");
            }
            System.out.println("");
        }
    }

    private boolean isValueRef(String str){
        return str.startsWith("_");
    }

}


