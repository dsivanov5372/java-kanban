package managment;

public class Managers {
    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private static final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    public static TaskManager getDefault(){
        return taskManager;
    }

    public static HistoryManager getDefaultHistory(){
        return historyManager;
    }
}
