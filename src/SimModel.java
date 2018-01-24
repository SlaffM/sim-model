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

        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
        FormulaEvaluator mainWorkbookEvaluator = myExcelBook.getCreationHelper().createFormulaEvaluator();
        ArrayList<ArrayList<SimObject>> listGeneral = new ArrayList<ArrayList<SimObject>>();

        buildNotIncludedHeaders();
        buildNotIncludedSheets();
        buildWithIncludedSheets();

        for (Sheet sheet : myExcelBook) {

            Sheet myExcellSheet = sheet;
            if (notIncludedSheets.contains(myExcellSheet.getSheetName())) continue;
            //if (!includedSheets.contains(myExcellSheet.getSheetName())) continue;


            ArrayList<SimObject> listObjectsOfClass = new ArrayList<SimObject>();

            //headers = new Hashtable<Integer, String>();

            for (Row rowData : myExcellSheet) {

                SimObject rfd = new SimObject();
                rfd.setProperties(new Hashtable<Integer, SimObjectProperty>());

                if (rowData.getRowNum() == ROW_HEADERS_INDEX) {
                    headers = buildHeaders(myExcellSheet);
                    continue;
                }

                for (Cell dataCell : rowData) {

                    int indxSimObject = dataCell.getColumnIndex();

                    if (indxSimObject == ID_COLUMN_INDEX) {
                        String sheetName = Formatter.GetFormattedSheetName(myExcellSheet.getSheetName());
                        rfd.setName(sheetName);
                        rfd.setID(dataCell.getStringCellValue());
                        continue;
                    }

                    if (headers.containsKey(indxSimObject)) {
                        SimObjectProperty simObjectProperty = buildSimProperty(dataCell, mainWorkbookEvaluator);
                        rfd.addProperty(indxSimObject, simObjectProperty);
                    }
                }
                listObjectsOfClass.add(rfd);
            }

            listGeneral.add(listObjectsOfClass);
            printAllObjects(listObjectsOfClass);
        }
        myExcelBook.close();
        XMLWriter.WriteXML(listGeneral, "./xls/mySIM.xml");
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
                String tempStr = workbookEvaluator.evaluate(dataCell).formatAsString().replaceAll("\"", "");
                if(tempStr.startsWith("_")){
                    dataValue = "#" + tempStr;
                    ref = true;
                }else{
                    dataValue = tempStr;
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

            for (Map.Entry<Integer, SimObjectProperty> entry : sObj.getProperties().entrySet()){
                System.out.print(entry.getValue().getName() + " = " + entry.getValue().getValue() + ", ");
            }
            System.out.println("");
        }
    }

}


