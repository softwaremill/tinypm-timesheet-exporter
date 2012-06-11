package pl.softwaremill.timesheet_exporter.datacollector;

import com.google.code.tinypmclient.Task;
import com.google.code.tinypmclient.User;
import com.google.code.tinypmclient.UserStory;
import com.google.code.tinypmclient.internal.Activity;

import java.util.Date;

public class ActivityInIteration implements ItemWithDateRange {

    private Activity activity;
    private IterationInProject iteration;
    private UserStory userStory;
    private Task task;

    public ActivityInIteration(Activity activity, IterationInProject iteration, UserStory userStory, Task task) {
        this.activity = activity;
        this.iteration = iteration;
        this.userStory = userStory;
        this.task = task;
    }

    public Date getDate() {
        return activity.getDate();
    }

    public void setUser(User user) {
        activity.setUser(user);
    }

    public void setDate(Date date) {
        activity.setDate(date);
    }

    public User getUser() {
        return activity.getUser();
    }

    public void setTimeSpent(float timeSpent) {
        activity.setTimeSpent(timeSpent);
    }

    public float getTimeSpent() {
        return activity.getTimeSpent();
    }

    public UserStory getUserStory() {
        return userStory;
    }

    public void setUserStory(UserStory userStory) {
        this.userStory = userStory;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public IterationInProject getIteration() {
        return iteration;
    }

    @Override
    public Date getStartDate() {
        return getDate();
    }

    @Override
    public Date getEndDate() {
        return getDate();
    }
}
