package test;

import managment.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager>{

    protected T manager;

    public TaskManagerTest(T manager){
        this.manager = manager;
    }

    @BeforeEach
    public void updateManager(){
        manager.deleteAllTasks();
        manager.deleteAllEpics();
    }

    @Test
    public void shouldMakeEpicIfNotNull(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        assertNotEquals(0, epic.getId());
    }

    @Test
    public void shouldNotMakeEpicIfNull(){
        manager.makeEpic(null);
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void shouldMakeTaskIfNotNull(){
        Task task = new Task("test", "test", Status.NEW);
        manager.makeTask(task);
        assertNotEquals(0, task.getId());
    }

    @Test
    public void shouldNotMakeTaskIfNull(){
        manager.makeTask(null);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldMakeSubtaskIfNotNull(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.DONE, epic);
        manager.makeSubtask(subtask);
        assertNotEquals(0, subtask.getId());
        assertEquals(subtask.getParentEpic(), epic.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldNotMakeSubtaskIfNull(){
        manager.makeSubtask(null);
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldNotMakeSubtaskIfHasNoParentEpic(){
        Subtask subtask = new Subtask("test", "test", Status.NEW, null);
        manager.makeSubtask(subtask);
        assertEquals(-1, subtask.getParentEpic());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldReturnEmptyArrayIfNoEpics(){
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyArrayIfContainsEpics(){
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        ArrayList<Epic> arr = manager.getAllEpics();
        assertTrue(arr.contains(epic1));
        assertTrue(arr.contains(epic2));
        assertEquals(2, arr.size());
    }

    @Test
    public void shouldReturnEmptyArrayIfNoTasks(){
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyArrayIfHasTasks(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        manager.makeTask(task1);
        manager.makeTask(task2);
        ArrayList<Task> arr = manager.getAllTasks();
        assertTrue(arr.contains(task1));
        assertTrue(arr.contains(task2));
        assertEquals(2, arr.size());
    }

    @Test
    public void shouldReturnEmptyArrayIfNoSubtasks(){
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyArrayIfContainsSubtasks(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        ArrayList<Subtask> arr = manager.getAllSubtasks();
        assertTrue(arr.contains(subtask));
        assertEquals(1, arr.size());
    }

    @Test
    public void shouldReturnEmptyArrayOfSubtasksIfEpicDoesNotExist(){
        assertTrue(manager.getAllSubtaskOfEpic(22222).isEmpty());
    }

    @Test
    public void shouldReturnEmptyArrayOfSubtasksIfEpicDoesNotHaveSubtasks(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        assertTrue(manager.getAllSubtaskOfEpic(epic.getId()).isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyArrayIfEpicHavesSubtasks(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        ArrayList<Subtask> arr = manager.getAllSubtaskOfEpic(epic.getId());
        assertTrue(arr.contains(subtask));
        assertEquals(1, arr.size());
    }

    @Test
    public void epicsShouldNotHaveSubtasksAfterCleaningSubtaskHashMap(){
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("test", "test", Status.NEW, epic2);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        manager.deleteAllSubtasks();
        assertTrue(epic1.getSubtasksId().isEmpty());
        assertTrue(epic2.getSubtasksId().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubtasksOfEpic(){
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("test", "test", Status.NEW, epic2);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        manager.deleteAllSubtasksOfEpic(epic2);
        assertTrue(epic2.getSubtasksId().isEmpty());
        assertTrue(epic1.getSubtasksId().contains(subtask1.getId()));
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExist(){
        assertNull(manager.getEpic(22222));
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExist(){
        assertNull(manager.getTask(22222));
    }

    @Test
    public void shouldReturnNullIfSubtaskDoesNotExist(){
        assertNull(manager.getSubtask(22222));
    }

    @Test
    public void shouldUpdateEpicIfNotNull(){
        Epic epic1 = new Epic("test", "test");
        manager.makeEpic(epic1);
        Epic epic2 = new Epic("text", "text");
        epic2.setId(epic1.getId());
        manager.updateEpic(epic2);
        assertEquals(epic2, manager.getEpic(epic1.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull(){
        Epic epic1 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.updateEpic(null);
        assertEquals(epic1, manager.getEpic(epic1.getId()));
    }

    @Test
    public void shouldUpdateTaskIfNotNull(){
        Task task1 = new Task("test", "test", Status.NEW);
        manager.makeTask(task1);
        Task task2 = new Task("text", "text", Status.NEW);
        task2.setId(task1.getId());
        manager.updateTask(task2);
        assertEquals(task2, manager.getTask(task1.getId()));
    }

    @Test
    public void shouldNotUpdateTaskIfNull(){
        Task task1 = new Task("test", "test", Status.NEW);
        manager.makeTask(task1);
        manager.updateTask(null);
        assertEquals(task1, manager.getTask(task1.getId()));
    }

    @Test
    public void shouldUpdateSubtaskIfNotNull(){
        Epic epic1 = new Epic("test", "test");
        manager.makeEpic(epic1);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("text", "text", Status.DONE, epic1);
        manager.makeSubtask(subtask1);
        subtask2.setId(subtask1.getId());
        manager.updateSubtask(subtask2);
        assertEquals(subtask2, manager.getSubtask(subtask1.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull(){
        Epic epic1 = new Epic("test", "test");
        manager.makeEpic(epic1);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, epic1);
        manager.makeSubtask(subtask1);
        manager.updateSubtask(null);
        assertEquals(subtask1, manager.getSubtask(subtask1.getId()));
    }

    @Test
    public void shouldDeleteEpicIfContains(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        manager.deleteEpicById(epic.getId());
        assertNull(manager.getEpic(epic.getId()));
    }

    @Test
    public void shouldNotChangeEpicHashMapIfNotContains(){
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        manager.deleteEpicById(222222);
        ArrayList<Epic> arr = manager.getAllEpics();
        assertTrue(arr.contains(epic1));
        assertTrue(arr.contains(epic2));
        assertEquals(2, arr.size());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty(){
        manager.deleteAllEpics();
        manager.deleteEpicById(222222);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldDeleteTaskIfContains(){
        Task task = new Task("test", "test", Status.NEW);
        manager.makeTask(task);
        manager.deleteTaskById(task.getId());
        assertNull(manager.getTask(task.getId()));
    }

    @Test
    public void shouldNotChangeTaskHashMapIfNotContains(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.deleteTaskById(222222);
        ArrayList<Task> arr = manager.getAllTasks();
        assertTrue(arr.contains(task1));
        assertTrue(arr.contains(task2));
        assertEquals(2, arr.size());
    }

    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty(){
        manager.deleteAllTasks();
        manager.deleteTaskById(222222);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldDeleteSubtaskIfContains(){
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertNull(manager.getSubtask(subtask.getId()));
        assertEquals(0, epic.getSubtasksId().size());
    }

    @Test
    public void shouldNotChangeSubtaskHashMapIfNotContains(){
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("text", "text", Status.DONE, epic2);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        manager.deleteSubtaskById(2222222);
        ArrayList<Subtask> arr = manager.getAllSubtasks();
        assertTrue(arr.contains(subtask1));
        assertTrue(arr.contains(subtask2));
        assertEquals(2, arr.size());
    }

    @Test
    public void shouldDoNothingIfSubtaskHashMapIsEmpty(){
        manager.deleteAllEpics();
        manager.deleteSubtaskById(222222);
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldReturnEmptyHistoryIfManagerHasNoTasks(){
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist(){
        manager.getTask(222222);
        manager.getSubtask(222222);
        manager.getEpic(2222222);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks(){
        Task task = new Task("test", "test", Status.NEW);
        manager.makeTask(task);
        manager.getTask(task.getId());
        Epic epic = new Epic("test", "test");
        manager.makeEpic(epic);
        Subtask subtask = new Subtask("test", "test", Status.NEW, epic);
        manager.makeSubtask(subtask);
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        List<Task> arr = manager.getHistory();
        assertEquals(3, arr.size());
        assertTrue(arr.contains(task));
        assertTrue(arr.contains(subtask));
        assertTrue(arr.contains(epic));
    }
}
