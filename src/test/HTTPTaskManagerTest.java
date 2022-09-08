package test;
import managment.HTTPTaskManager;
import managment.Managers;
import org.junit.jupiter.api.*;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager>{
    private static KVServer server;
    public HTTPTaskManagerTest() throws IOException, InterruptedException {
        super((HTTPTaskManager) Managers.getDefault("http://localhost:8078/"));
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = new KVServer();
        server.start();
    }

    @AfterAll
    static void stopServer(){
        server.stop();
    }

    @Test
    public void managerShouldBeEmptyIfRestoreFromEmptyServer() throws IOException, InterruptedException {
        HTTPTaskManager newManager = HTTPTaskManager.loadFromServer("http://localhost:8078/", "vulnerability");

        assertTrue(newManager.getAllTasks().isEmpty());
        assertTrue(newManager.getAllSubtasks().isEmpty());
        assertTrue(newManager.getAllEpics().isEmpty());
        assertTrue(newManager.getPrioritizedTasks().isEmpty());
        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void restoreManagerFromServer() throws IOException, InterruptedException {
        Task task = new Task("test", "test", Status.NEW);
        manager.makeTask(task);
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        manager.getTask(task.getId());
        manager.getSubtask(subtask.getId());

        HTTPTaskManager newManager = HTTPTaskManager.loadFromServer("http://localhost:8078/", "vulnerability");

        assertEquals(manager.getAllTasks(), newManager.getAllTasks());
        assertEquals(manager.getAllEpics(), newManager.getAllEpics());
        assertEquals(manager.getAllSubtasks(), newManager.getAllSubtasks());
        assertEquals(manager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
        assertEquals(manager.getHistory(), newManager.getHistory());
    }
}
