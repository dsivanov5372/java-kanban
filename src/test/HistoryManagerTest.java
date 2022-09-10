package test;

import managment.HistoryManager;
import managment.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {
    private HistoryManager manager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void makeManager(){
        manager = new InMemoryHistoryManager();
        task1 = new Task("test", "test", Status.NEW, null, null);
        task2 = new Task("test", "test", Status.NEW, null, null);
        task3 = new Task("test", "test", Status.NEW, null, null);
    }

    @Test
    public void shouldReturnEmptyHistoryIfHaveNoTasks(){
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void tasksShouldBeAddedInTheTailOfQuery(){
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
        task1.setId(1);
        manager.add(task1);
        manager.remove(task1.getId());
        assertTrue(manager.getHistory().isEmpty());
    }
}