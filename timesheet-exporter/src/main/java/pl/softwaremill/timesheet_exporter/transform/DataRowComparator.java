package pl.softwaremill.timesheet_exporter.transform;

import java.util.Comparator;

public class DataRowComparator implements Comparator<DataRow> {


    @Override
    public int compare(DataRow row1, DataRow row2) {

        int userCompared = row1.getUser().compareTo(row2.getUser());

        if (userCompared != 0) {
            return userCompared;
        }

        int projectCompared = row1.getProject().compareTo(row2.getProject());

        if (projectCompared != 0) {
            return projectCompared;
        }

        int datesCompared = row1.getDate().compareTo(row2.getDate());

        if (datesCompared != 0) {
            return datesCompared;
        }

        int userStoriesCompared = row1.getUserStory().compareTo(row2.getUserStory());

        if (userStoriesCompared != 0) {
            return userStoriesCompared;
        }

        int taskCompared = row1.getTask().compareTo(row2.getTask());

        return taskCompared;
    }
}
