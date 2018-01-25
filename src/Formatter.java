class Formatter {

    static String GetFormattedSheetName(String name){
        return name.replaceAll("[()\\dа-яА-Я]", "").trim();
    }

    static String GetStringWithoutQuotes(String str){
        return str.replaceAll("\"", "");
    }

    static String ReplaceColonOnUnderlining(String str) { return str.replaceAll(":", "_"); }

}
