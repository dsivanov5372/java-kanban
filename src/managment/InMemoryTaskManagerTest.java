package managment;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @Test
    public void shouldAddToHistoryExistedTasks(){
        Task task = new Task("test", "test", Status.NEW);
        Epic epic = new Epic("test", "test");
        manager.makeTask(task);
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        manager.addToHistory(task.getId());
        manager.addToHistory(epic.getId());
        manager.addToHistory(subtask.getId());
        List<Task> arr = manager.getHistory();
        assertTrue(arr.contains(task));
        assertTrue(arr.contains(epic));
        assertTrue(arr.contains(subtask));
    }

    @Test
    public void shouldNotAddToHistoryNotExistingTask(){
        manager.addToHistory(222222);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldAddEpicIfNotNull(){
        Epic epic = new Epic("test", "test");
        epic.setStatus(Status.NEW);
        epic.setId(2222);
        manager.addEpic(epic);
        assertEquals(epic, manager.getEpic(2222));
    }

    @Test
    public void shouldNotAddEpicIfNull(){
        manager.addEpic(null);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldAddTaskIfNotNull(){
        Task task = new Task("test", "test", Status.NEW);
        task.setId(2222);
        manager.addTask(task);
        assertEquals(task, manager.getTask(2222));
    }

    @Test
    public void shouldNotAddTaskIfNull(){
        manager.addTask(null);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldAddSubtaskIfNotNull(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        subtask.setId(2222);
        manager.addSubtask(subtask);
        assertEquals(subtask, manager.getSubtask(2222));
    }

    @Test
    public void shouldNotAddSubtaskIfNull(){
        manager.addSubtask(null);
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldNotAddSubtaskIfParentEpicNull(){
        Subtask subtask = new Subtask("test", "test", Status.NEW, null);
        subtask.setId(2222);
        manager.addSubtask(subtask);
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldFindEpicIfContainsAndNotAddToHistory(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        assertTrue(manager.getHistory().isEmpty());
        assertEquals(manager.getEpic(epic.getId()), manager.findById(epic.getId()));
    }

    @Test
    public void shouldReturnNullIfManagerDoesNotContainEpic(){
        assertNull(manager.findById(2222222));
    }

    @Test
    public void shouldNotMakeTaskIfItHasIntersection(){
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task("test", "test", Status.NEW, duration, start);
        Task task2 = new Task("test", "test", Status.NEW, duration, start);
        Task task3 = new Task("test", "test", Status.NEW, null, null);
        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeTask(task3);
        ArrayList<Task> arr = manager.getAllTasks();

        assertEquals(0, task2.getId());
        assertEquals(2, arr.size());
        assertTrue(arr.contains(task1));
        assertTrue(arr.contains(task3));
        assertFalse(arr.contains(task2));
    }

    @Test
    public void shouldMakeTasksIfNoIntersection(){
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task("test", "test", Status.NEW, duration, start);
        Task task2 = new Task("test", "test", Status.NEW, duration, start.plusDays(1));
        manager.makeTask(task1);
        manager.makeTask(task2);
        ArrayList<Task> arr = manager.getAllTasks();

        assertEquals(2, arr.size());
        assertTrue(arr.contains(task1));
        assertTrue(arr.contains(task2));
    }

    @Test
    public void shouldMakeTasksIfTimeIsNull(){
        Task task1 = new Task("test", "test", Status.NEW, null, null);
        Task task2 = new Task("test", "test", Status.NEW, null, null);
        manager.makeTask(task1);
        manager.makeTask(task2);
        ArrayList<Task> arr = manager.getAllTasks();

        assertEquals(2, arr.size());
        assertTrue(arr.contains(task1));
        assertTrue(arr.contains(task2));
    }

    @Test
    public void returnEmptySortedTasksArrayIfNoTasks(){
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void returnEmptySortedTasksArrayAfterFullDeleteFromManager(){
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task = new Task("test", "test", Status.NEW, duration, start);
        Epic epic = new Epic("test", "test");
        manager.makeTask(task);
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic, duration, start.plusDays(1));
        manager.makeSubtask(subtask);
        manager.deleteAllEpics();
        manager.deleteAllTasks();

        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void returnSortedTasksArray(){
        LocalDateTime start1 = LocalDateTime.of(2003, 1, 28, 9, 30);
        LocalDateTime start2 = LocalDateTime.of(2003, 1, 29, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task("test", "test", Status.NEW, duration, start1);
        Task task2 = new Task("test", "test", Status.NEW, duration, start2);
        Task task3 = new Task("test", "test", Status.NEW, null, null);
        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeTask(task3);
        ArrayList<Task> arr = manager.getPrioritizedTasks();

        assertEquals(3, arr.size());
        assertEquals(task3, arr.get(0));
        assertEquals(task1, arr.get(1));
        assertEquals(task2, arr.get(2));
    }
}
