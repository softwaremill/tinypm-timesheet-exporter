package pl.softwaremill.timesheet_exporter.datacollector;

import com.google.code.tinypmclient.Iteration;
import com.google.code.tinypmclient.Project;
import com.google.code.tinypmclient.Task;
import com.google.code.tinypmclient.TinyPM;
import com.google.code.tinypmclient.UserStory;
import com.google.code.tinypmclient.internal.Activity;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import pl.softwaremill.timesheet_exporter.ProgressBar;
import pl.softwaremill.timesheet_exporter.settings.ExporterSettings;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TinyPMDataCollector {

    protected TinyPM tinyPM;
    private ExporterSettings settings;
    private TimePredicatesFactory timePredicatesFactory;

    public TinyPMDataCollector(ExporterSettings settings) {

        this.settings = settings;
        tinyPM = new TinyPM(settings.getTinypmUrl(), settings.getAuthenticationToken());
        timePredicatesFactory = new TimePredicatesFactory(settings);
    }

    public Set<Project> loadListOfProjects() {
        Set<Project> activeProjects = new HashSet<Project>();

        List<Project> projects = tinyPM.getAllProjects();
        List<IterationInProject> allIterations = loadIterationsForProjects(projects);
        Collection<IterationInProject> iterationsInTime = filterIterations(allIterations);

        for (IterationInProject iteration : iterationsInTime) {
            activeProjects.add(iteration.getProject());
        }

        return activeProjects;
    }

    public Collection<ActivityInIteration> collectData() {

        List<Project> projects = loadRequestedProjects(settings.getProjectCodes());
        List<IterationInProject> allIterations = loadIterationsForProjects(projects);
        Collection<IterationInProject> iterationsInGivenMonth = filterIterations(allIterations);

        return loadFilteredTasks(iterationsInGivenMonth);
    }

    protected List<Project> loadRequestedProjects(List<String> projectCodes) {

        if (projectCodes == null) {
            return tinyPM.getAllProjects();
        }

        List<Project> projects = Lists.newArrayList();

        for (String code : projectCodes) {
            Project project = tinyPM.getProject(code);
            if (project != null) {
                projects.add(project);
            }
        }

        if (notAllProjectsWereFound(projectCodes, projects)) {
            return throwExceptionWithAvailableProjectCodes();
        }

        return projects;
    }

    private boolean notAllProjectsWereFound(List<String> projectCodes, List<Project> projects) {
        return projectCodes.size() != projects.size();
    }

    private List<Project> throwExceptionWithAvailableProjectCodes() {
        Collection<String> availableProjectCodes = Collections2.transform(tinyPM.getAllProjects(), new Function<Project, String>() {
            @Override
            public String apply(@Nullable Project project) {
                return project.getCode();
            }
        });

        throw new RuntimeException("Some projects were not found using given project codes. Available project codes are: \n" +
                Joiner.on(", ").join(availableProjectCodes));
    }

    private List<IterationInProject> loadIterationsForProjects(List<Project> projects) {
        List<IterationInProject> allIterations = Lists.newArrayList();
        for (Project project : projects) {

            List<Iteration> iterationsFromProject = tinyPM.getAllIterations(project);
            for (Iteration iteration : iterationsFromProject) {
                allIterations.add(new IterationInProject(iteration, project));
            }
        }
        return allIterations;
    }

    private Collection<IterationInProject> filterIterations(List<IterationInProject> allIterations) {
        return Collections2.filter(allIterations, timePredicatesFactory.forIteration());
    }

    private Collection<ActivityInIteration> loadFilteredTasks(Collection<IterationInProject> iterationsInGivenMonth) {
        List<ActivityInIteration> activities = Lists.newArrayList();

        int numberOfIterations = iterationsInGivenMonth.size();

        int currentIteration = 0;
        int pointsPerIteration = 100 / numberOfIterations;

        for (IterationInProject iteration : iterationsInGivenMonth) {
            List<Activity> timesheetForIteration = tinyPM.getTimesheetForIteration(iteration.getId());

            int i = 0;

            for (Activity activity : timesheetForIteration) {
                // show progress if needed
                if (settings.getShowProgress()) {
                    double pseudoPointsPerIteration = (double) (i++) / (double) timesheetForIteration.size();

                    ProgressBar.printProgBar(currentIteration * pointsPerIteration +
                            (int)(pseudoPointsPerIteration * pointsPerIteration));
                }

                UserStory userStory = tinyPM.getUserStory(activity.getUserStory().getId());
                Task task = tinyPM.getTask(activity.getTask().getId());

                activities.add(new ActivityInIteration(activity, iteration, userStory, task));
            }

            currentIteration++;
        }

        ProgressBar.printProgBar(100);

        return filterTasks(activities);
    }

    private Collection<ActivityInIteration> filterTasks(Collection<ActivityInIteration> activities) {

        activities = removeTasksBasingOnDate(activities);

        if (settings.getUser() != null) {
            return removeTasksFromOtherUsers(activities, settings.getUser());
        } else {
            return activities;
        }
    }

    private Collection<ActivityInIteration> removeTasksFromOtherUsers(Collection<ActivityInIteration> activities, final String user) {
        return Collections2.filter(activities, new Predicate<ActivityInIteration>() {
            @Override
            public boolean apply(ActivityInIteration activity) {
                return user.equals(activity.getUser().getName());
            }
        });
    }

    private Collection<ActivityInIteration> removeTasksBasingOnDate(Collection<ActivityInIteration> activities) {
        activities = Collections2.filter(activities, timePredicatesFactory.forTask());
        return activities;
    }


}
