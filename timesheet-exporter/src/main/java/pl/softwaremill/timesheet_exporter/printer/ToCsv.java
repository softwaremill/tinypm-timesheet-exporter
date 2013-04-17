package pl.softwaremill.timesheet_exporter.printer;

import pl.softwaremill.timesheet_exporter.settings.SettingsValidator;
import pl.softwaremill.timesheet_exporter.transform.DataRow;

public class ToCsv {

    public static String toCSV(DataRow row, String[] fields) {
        StringBuilder builder = new StringBuilder();

        for (String field : fields) {
            String value;

            if (staticColumn(field)) {
                value = getStaticValue(field);
            }
            else {
                value = row.getValue(field);
            }
            appendWithComma(builder, value);
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static String getColumns(String[] fields) {
        StringBuilder builder = new StringBuilder();

        for (String field : fields) {
            String columnName;

            if (staticColumn(field)) {
                columnName = getStaticColumnName(field);
            }
            else {
                columnName = camelCaseToNormalCase(field);
            }
            appendWithComma(builder, columnName);
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    private static String getStaticColumnName(String field) {
        return getStaticElement(field, 0);
    }

    private static String getStaticValue(String field) {
        return getStaticElement(field, 1);
    }

    private static String getStaticElement(String field, int index) {
        return field.split("\\"+SettingsValidator.CONSTANT_FIELD_SEPARATOR)[index];
    }

    private static boolean staticColumn(String field) {
        return field.contains(SettingsValidator.CONSTANT_FIELD_SEPARATOR);
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
