class Formatter {

    static String GetFormattedSheetName(String name){
        return name.replaceAll("[()\\dа-яА-Я]", "").trim();
    }
}
