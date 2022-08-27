package managment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    public void makeManager(){
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldReturnEmptyHistoryIfHaveNoTasks(){
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void tasksShouldBeAddedInTheTailOfQuery(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        List<Task> arr = manager.getHistory();

        assertEquals(3, arr.size());
        assertEquals(task1, arr.get(0));
        assertEquals(task2, arr.get(1));
        assertEquals(task3, arr.get(2));
    }

    @Test
    public void historyShouldContainOnlyOneTaskIfDuplicate(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        manager.add(task1);
        manager.add(task2);
        manager.add(task2);
        manager.add(task2);
        manager.add(task2);
        List<Task> arr = manager.getHistory();

        assertEquals(2, arr.size());
        assertEquals(task1, arr.get(0));
        assertEquals(task2, arr.get(1));
    }

    @Test
    public void moveTasksToTheTailIfWasDuplicated(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task2);
        List<Task> arr = manager.getHistory();

        assertEquals(3, arr.size());
        assertEquals(task1, arr.get(0));
        assertEquals(task3, arr.get(1));
        assertEquals(task2, arr.get(2));
    }

    @Test
    public void headShouldBeNextTaskInHistoryIfDeleteHead(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task1.getId());
        List<Task> arr = manager.getHistory();

        assertEquals(2, arr.size());
        assertEquals(task2, arr.get(0));
        assertEquals(task3, arr.get(1));
    }

    @Test
    public void tailShouldBePreviousTaskInHistoryIfDeleteTail(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task3.getId());
        List<Task> arr = manager.getHistory();

        assertEquals(2, arr.size());
        assertEquals(task1, arr.get(0));
        assertEquals(task2, arr.get(1));
    }

    @Test
    public void historyShouldContain2TasksAfterDeletingMiddleOne(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task2.getId());
        List<Task> arr = manager.getHistory();

        assertEquals(2, arr.size());
        assertEquals(task1, arr.get(0));
        assertEquals(task3, arr.get(1));
    }

    @Test
    public void historyShouldBeEmptyAfterDeletingSingleTask(){
        Task task1 = new Task("test", "test", Status.NEW);
        task1.setId(1);
        manager.add(task1);
        manager.remove(task1.getId());
        assertTrue(manager.getHistory().isEmpty());
    }
}