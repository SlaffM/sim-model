public class Formatter {

    public static String GetSheetName(String name){
        String resultName = name.replaceAll("\\(\\d+\\)", "");
        resultName = resultName.replaceAll("\\([а-яА-Я]+\\)", "").trim();
        return resultName;
    }

}
