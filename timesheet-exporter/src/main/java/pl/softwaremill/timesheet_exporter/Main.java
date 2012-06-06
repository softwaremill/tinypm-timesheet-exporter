package pl.softwaremill.timesheet_exporter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.code.tinypmclient.Project;
import com.google.code.tinypmclient.User;
import com.google.common.collect.Multimap;
import pl.softwaremill.timesheet_exporter.datacollector.ActivityInIteration;
import pl.softwaremill.timesheet_exporter.datacollector.TinyPMDataCollector;
import pl.softwaremill.timesheet_exporter.printer.IReportPrinter;
import pl.softwaremill.timesheet_exporter.printer.PrinterFactory;
import pl.softwaremill.timesheet_exporter.printer.ToCsv;
import pl.softwaremill.timesheet_exporter.settings.ExporterSettings;
import pl.softwaremill.timesheet_exporter.settings.SettingsValidator;
import pl.softwaremill.timesheet_exporter.transform.DataRow;
import pl.softwaremill.timesheet_exporter.transform.DataTransfomer;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        ExporterSettings exporterSettings = new ExporterSettings();
        JCommander jCommander = new JCommander(exporterSettings);

        if (args.length == 0) {
            jCommander.usage();
            System.exit(0);
        }

        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage() + "\n");

            jCommander.usage();
            System.exit(1);
        }

        new SettingsValidator().validate(exporterSettings);

        TinyPMDataCollector tinyPMDataCollector = new TinyPMDataCollector(exporterSettings);

        if (exporterSettings.getLoadProjects()) {
            System.out.println("Project Name,Project Code");
            for (Project project : tinyPMDataCollector.loadListOfProjects()) {
                System.out.println(ToCsv.escapeCSV(project.getName()) + "," + ToCsv.escapeCSV(project.getCode()));
            }
        } else {
            Collection<ActivityInIteration> activities = tinyPMDataCollector.collectData();

            Multimap<User, DataRow> dataReadyToPrint = new DataTransfomer(activities).transform();

            IReportPrinter printer = PrinterFactory.createPrinter(exporterSettings);
            printer.printReport(dataReadyToPrint);
        }

    }
}
