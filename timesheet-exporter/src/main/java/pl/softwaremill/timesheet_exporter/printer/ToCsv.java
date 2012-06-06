package pl.softwaremill.timesheet_exporter.printer;

import pl.softwaremill.timesheet_exporter.transform.DataRow;

public class ToCsv {

    public static String toCSV(DataRow row, String[] fields) {
        StringBuilder builder = new StringBuilder();

        for (String field : fields) {
            appendWithComma(builder, row.getValue(field));
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static String getColumns(String[] fields) {
        StringBuilder builder = new StringBuilder();

        for (String field : fields) {
            appendWithComma(builder, camelCaseToNormalCase(field));
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    private static StringBuilder appendWithComma(StringBuilder builder, String text) {
        return builder
                .append(escapeCSV(text))
                .append(",");
    }

    private static String camelCaseToNormalCase(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (i == 0) {
                builder.append(Character.toUpperCase(ch));
            }
            else {
                if (Character.isUpperCase(ch)) {
                    builder.append(" ");
                }
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    public static String escapeCSV(String text) {
        String escapedText = text;

        if (escapedText.contains(",") ||
                escapedText.contains("\n") ||
                escapedText.trim().length() != escapedText.length()) {

            // first 'escape' quotes
            escapedText = escapedText.replaceAll("\"", "\"\"");

            // and surround with quotes
            escapedText = "\""+escapedText+"\"";
        }

        return escapedText;
    }
}
