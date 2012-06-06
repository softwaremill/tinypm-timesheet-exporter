package pl.softwaremill.timesheet_exporter.settings;

import com.beust.jcommander.ParameterException;
import com.google.common.annotations.VisibleForTesting;
import pl.softwaremill.timesheet_exporter.transform.DataRow;

import java.lang.reflect.Field;

public class SettingsValidator {

    public void validate(ExporterSettings settings) {
        validateProjectAndUserSettings(settings);
        validateDateSettings(settings);
        validateFields(settings);
    }

    @VisibleForTesting
    protected void validateFields(ExporterSettings settings) {
        for (String field : settings.getFields()) {
            try {
                DataRow.class.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                String fields = "{";

                for (Field declaredField : DataRow.class.getDeclaredFields()) {
                    fields += declaredField.getName() + " ";
                }

                System.err.println("Field "+field+" is incorrect. Available fields are: "+fields+"}");
                System.exit(1);
            }
        }

    }

    @VisibleForTesting
    protected void validateDateSettings(ExporterSettings settings) {
        if (settings.getYear() == null && settings.getMonth() == null && settings.getDateFrom() == null && settings.getDateTo() == null) {
            throw new ParameterException("Either pair -year and -month or pair -dateFrom and -dateTo must be provided");
        }

        if ((settings.getYear() != null || settings.getMonth() != null) && (settings.getDateFrom() != null && settings.getDateTo() != null)) {
            throw new ParameterException("For pair -dateFrom and -dateTo parameters -year and -month are prohibited");
        }

        if ((settings.getYear() != null && settings.getMonth() != null) && (settings.getDateFrom() != null || settings.getDateTo() != null)) {
            throw new ParameterException("For pair -year and -month parameters -dateFrom and -dateTo are prohibited");
        }

        if (settings.getYear() == null && settings.getMonth() == null && (settings.getDateFrom() != null && settings.getDateFrom() != null) == false) {
            throw new ParameterException("Both parameters -dateFrom and -dateTo must be provided");
        }

        if (settings.getDateFrom() == null && settings.getDateFrom() == null && (settings.getYear() != null && settings.getMonth() != null) == false) {
            throw new ParameterException("Both parameters -year and -month must be provided");
        }

        if (settings.getYear() == null && settings.getMonth() == null && settings.getDateFrom().after(settings.getDateTo())) {
            throw new ParameterException("Value of -dateTo can not be before -dateFrom");
        }
    }

    @VisibleForTesting
    protected void validateProjectAndUserSettings(ExporterSettings settings) {
        if (settings.getProjectCodes() == null && settings.getUser() == null && !settings.getLoadProjects()) {
            throw new ParameterException("Either -project, -user or -loadProjects must be provided");
        }
    }
}