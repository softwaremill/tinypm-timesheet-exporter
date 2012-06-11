package pl.softwaremill.timesheet_exporter.settings;

import com.beust.jcommander.Parameter;
import com.google.common.annotations.VisibleForTesting;

import java.util.Date;
import java.util.List;

public class ExporterSettings {

    @Parameter(names = {"-url", "-u"}, description = "Tiny PM server address", required = true)
    private String tinypmUrl;

    @Parameter(names = {"-token", "-t"}, description = "Authentication token", required = true)
    private String authenticationToken;

    @Parameter(names = {"-loadProjects", "-lp"}, description = "If this is set, -field,, -sum, -user and -project are ignored" +
            " and the tool will generate a list of active projects during given time")
    private Boolean loadProjects = false;

    @Parameter(names = {"-project", "-p"}, description = "Codes of projects to be included in timesheet report")
    private List<String> projectCodes;

    @Parameter(names = {"-year", "-y"}, description = "Year of timesheet")
    private Integer year;

    @Parameter(names = {"-month", "-m"}, description = "Number of month (1-12) for timesheet", validateWith = MonthValidator.class)
    private Integer month;

    @Parameter(names = {"-dateFrom", "-df"}, description = "Start date of the report, in yyyy-MM-dd format",
            validateWith = DateValidator.class, converter = DateConverter.class)
    private Date dateFrom;

    @Parameter(names = {"-dateTo", "-dt"}, description = "End date of the report, in yyyy-MM-dd format",
            validateWith = DateValidator.class, converter = DateConverter.class)
    private Date dateTo;

    @Parameter(names = {"-user", "-e"}, description = "User's timesheet")
    private String user;

    @Parameter(names = "-output", description = "Report output format", converter = OutputConverter.class)
    private OutputEnum output = OutputEnum.CONSOLE;

    @Parameter(names = {"-fields", "-f"}, description = "Comma-separated fields to be exported")
    private String fields = "user,project,userStory,task,date,timeSpent,userStoryEstimation,taskEstimation";

    @Parameter(names = {"-sum", "-s"}, description = "If set, exporter will generate the sum of the time logged")
    private Boolean sum = false;

    @Parameter(names = {"-progress"}, description = "If set, progress bar will be shown on the error console (so the actual CSV can be redirected into file)")
    private Boolean showProgress = false;

    public String getTinypmUrl() {
        return tinypmUrl;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public List<String> getProjectCodes() {
        return projectCodes;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public String getUser() {
        return user;
    }

    public OutputEnum getOutput() {
        return output;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public String[] getFields() {
        return fields.split(",");
    }

    public Boolean getSum() {
        return sum;
    }

    public Boolean getLoadProjects() {
        return loadProjects;
    }

    public Boolean getShowProgress() {
        return showProgress;
    }

    @VisibleForTesting
    protected void setMonth(Integer month) {
        this.month = month;
    }

    @VisibleForTesting
    protected void setYear(Integer year) {
        this.year = year;
    }

    @VisibleForTesting
    protected void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    @VisibleForTesting
    protected void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @VisibleForTesting
    protected void setProjectCodes(List<String> projectCodes) {
        this.projectCodes = projectCodes;
    }

    @VisibleForTesting
    protected void setUser(String user) {
        this.user = user;
    }
}
