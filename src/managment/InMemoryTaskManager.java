package managment;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final TreeSet<Task> prioritizedTasksSet;
    private int idSetter = 1;

    public InMemoryTaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasksSet = new TreeSet<>((o1, o2) -> {
            LocalDateTime time1 = o1.getStartTime();
            LocalDateTime time2 = o2.getStartTime();
            if ((time1 == null && time2 == null)){
                return -1;
            }
            if (time1 == null){
                return -1;
            }
            if (time2 == null){
                return 1;
            }
            if (time1.isBefore(time2)){
                return -1;
            }
            if (time1.equals(time2)){
                return 0;
            }
            return 1;
        });
    }

    @Override
    public void makeEpic(Epic epic){
        if (epic != null && findIntersect(epic)) {
            epic.setId(idSetter);
            idSetter++;
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void makeTask(Task task){
        if (task != null && findIntersect(task)) {
            task.setId(idSetter);
            idSetter++;
            tasks.put(task.getId(), task);
            prioritizedTasksSet.add(task);
        }
    }

    @Override
    public void makeSubtask(Subtask subtask){
        if (subtask != null && subtask.getParentEpic() != -1 && findIntersect(subtask)) {
            subtask.setId(idSetter);
            idSetter++;
            subtasks.put(subtask.getId(), subtask);
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.addSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
            prioritizedTasksSet.add(subtask);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtaskOfEpic(int id){
        Epic epic = epics.get(id);
        ArrayList<Integer> ids = (epic == null) ? new ArrayList<>() : epic.getSubtasksId();
        ArrayList<Subtask> result = new ArrayList<>();
        for (int subtaskId : ids){
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    @Override
    public void deleteAllTasks(){
        for(int task : tasks.keySet()){
            historyManager.remove(task);
            prioritizedTasksSet.remove(tasks.get(task));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            deleteAllSubtasksOfEpics(epic);
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks(){
        for (Subtask subtask : subtasks.values()){
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.removeSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
            prioritizedTasksSet.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksOfEpics(Epic epic){
        if (epic != null) {
            for (int subtask : epic.getSubtasksId()){
                Subtask task = subtasks.get(subtask);
                prioritizedTasksSet.remove(task);
                subtasks.remove(subtask);
                historyManager.remove(subtask);
            }
            epic.removeAllSubtasks();
        }
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getTask(int id){
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateEpic(Epic epic){
        if (epic != null && findIntersect(epic)) {
            ArrayList<Subtask> newSubtasks = new ArrayList<>();
            for (int id : epic.getSubtasksId()) {
                newSubtasks.add(subtasks.get(id));
            }
            Epic prevEpic = epics.get(epic.getId());
            for (int subtask : prevEpic.getSubtasksId()) {
                subtasks.remove(subtask);
            }
            epics.put(epic.getId(), epic);
            for (Subtask subtask : newSubtasks) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
    }

    @Override
    public void updateTask(Task task){
        if (task != null && findIntersect(task)) {
            prioritizedTasksSet.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            prioritizedTasksSet.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask){
        if (subtask != null && epics.containsKey(subtask.getParentEpic()) && findIntersect(subtask)){
            prioritizedTasksSet.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasksSet.add(subtask);
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.changeStatus(getAllSubtaskOfEpic(parentEpic.getId()));
        }
    }

    @Override
    public void deleteEpicById(int id){
        Epic epic = epics.get(id);

        if (epic != null) {
            epics.remove(id);
            historyManager.remove(id);
            for (int subtask : epic.getSubtasksId()) {
                subtasks.remove(subtask);
                historyManager.remove(subtask);
                prioritizedTasksSet.remove(subtasks.get(subtask));
            }
        }
    }

    @Override
    public void deleteTaskById(int id){
        if (tasks.containsKey(id)){
            prioritizedTasksSet.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id){
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            prioritizedTasksSet.remove(subtask);
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.removeSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    public ArrayList<Task> getPrioritizedTasks(){
        return new ArrayList<>(prioritizedTasksSet);
    }

    protected void addToHistory(int id){
        if (epics.containsKey(id)){
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)){
            historyManager.add(subtasks.get(id));
        } else if (tasks.containsKey(id)){
            historyManager.add(tasks.get(id));
        }
    }

    protected void addEpic(Epic epic){
        if (epic != null && findIntersect(epic)) {
            epics.put(epic.getId(), epic);
        }
    }

    protected void addSubtask(Subtask subtask){
        if (subtask != null && subtask.getParentEpic() != -1 &&
                epics.containsKey(subtask.getParentEpic()) && findIntersect(subtask)) {

            subtasks.put(subtask.getId(), subtask);
            prioritizedTasksSet.add(subtask);
        }
    }

    protected void addTask(Task task){
        if (task != null && findIntersect(task)) {
            tasks.put(task.getId(), task);
            prioritizedTasksSet.add(task);
        }
    }

    protected Epic findById(int id){
        return epics.get(id);
    }

    private boolean findIntersect(Task task){
        if (task.getStartTime() != null && task.getDuration() != null) {
            for (Task anotherTask : prioritizedTasksSet) {
                if (anotherTask.getStartTime() != null && anotherTask.getDuration() != null){
                    if (isIntersect(task, anotherTask)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isIntersect(Task task1, Task task2){
        LocalDateTime taskStartTime = task1.getStartTime();
        LocalDateTime taskEndTime = task1.getEndTime();
        LocalDateTime start = task2.getStartTime();
        LocalDateTime end = task2.getEndTime();

        boolean isStartIntersect = (taskStartTime.isEqual(start) || taskStartTime.isAfter(start)) &&
                (taskStartTime.isBefore(end) || taskStartTime.isEqual(end));
        boolean isEndIntersect = (taskEndTime.isEqual(start) || taskEndTime.isAfter(start)) &&
                (taskEndTime.isBefore(end) || taskEndTime.isEqual(end));
        return isStartIntersect || isEndIntersect;
    }
}