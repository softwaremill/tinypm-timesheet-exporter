package pl.softwaremill.timesheet_exporter.settings;

import com.beust.jcommander.IStringConverter;

public class OutputConverter implements IStringConverter<OutputEnum> {

    @Override
    public OutputEnum convert(String value) {
        OutputEnum convertedValue = OutputEnum.fromString(value);

        if (convertedValue == null) {
            return OutputEnum.CONSOLE;
        }
        return convertedValue;
    }
}
