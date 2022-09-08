package managment;

import java.io.IOException;

public class Managers {
    public static TaskManager getDefault(String url) throws IOException, InterruptedException {
        return new HTTPTaskManager(url);
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
