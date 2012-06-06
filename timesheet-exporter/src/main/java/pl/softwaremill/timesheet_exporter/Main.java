package pl.softwaremill.timesheet_exporter;

import com.beust.jcommander.JCommander;
import com.google.code.tinypmclient.User;
import com.google.common.collect.Multimap;
import pl.softwaremill.timesheet_exporter.datacollector.ActivityInIteration;
import pl.softwaremill.timesheet_exporter.datacollector.TinyPMDataCollector;
import pl.softwaremill.timesheet_exporter.printer.IReportPrinter;
import pl.softwaremill.timesheet_exporter.printer.PrinterFactory;
import pl.softwaremill.timesheet_exporter.settings.ExporterSettings;
import pl.softwaremill.timesheet_exporter.settings.SettingsValidator;
import pl.softwaremill.timesheet_exporter.transform.DataRow;
import pl.softwaremill.timesheet_exporter.transform.DataTransfomer;

import java.lang.reflect.Field;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {

        ExporterSettings exporterSettings = new ExporterSettings();
        new JCommander(exporterSettings, args);

        // validate fields
        for (String field : exporterSettings.getFields()) {
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

        new SettingsValidator().validate(exporterSettings);

        Collection<ActivityInIteration> activities = new TinyPMDataCollector(exporterSettings).collectData();

        Multimap<User, DataRow> dataReadyToPrint = new DataTransfomer(activities).transform();

        IReportPrinter printer = PrinterFactory.createPrinter(exporterSettings);
        printer.printReport(dataReadyToPrint);

    }
}
