package com.j1.marblerye;

public class CSVWriter {
    private StringBuilder builder;
    private String header;

    private final String NEW_LINE = "\n";
    private final String COMMA = ",";

    public CSVWriter() {
        builder = new StringBuilder();
    }

    public void setHeader(String headerString) {
        header = headerString;
    }

    public void nextLine() {
        builder.deleteCharAt(builder.length() - 1);
        builder.append(NEW_LINE);
    }

    public void writeLine(String... items) {
        for (String item : items) {
            builder.append(item);
            builder.append(COMMA);
        }
    }

    public String getCSV() {
        return header + NEW_LINE + builder.toString();
    }
}
