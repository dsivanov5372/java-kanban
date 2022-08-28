package test;

import managment.FileBackedTasksManager;
import managment.FileTaskReader;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final Path fileToSaveData = Path.of("backup.csv");
    private final File fileToLoadFrom = new File(String.valueOf(fileToSaveData));

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager());
    }

    @Test
    public void managerShouldBeEmptyAfterLoadingFromEmptyFile(){
        FileTaskReader.save(manager);
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertEquals(manager.getAllEpics(), newManager.getAllEpics());
        assertEquals(manager.getAllTasks(), newManager.getAllTasks());
        assertEquals(manager.getAllSubtasks(), newManager.getAllSubtasks());
        assertEquals(manager.getHistory(), newManager.getHistory());
        assertEquals(manager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
        assertTrue(newManager.getAllEpics().isEmpty());
        assertTrue(newManager.getAllTasks().isEmpty());
        assertTrue(newManager.getAllSubtasks().isEmpty());
        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void loadEpicWithoutSubtasksFromFileToManagerWithoutSubtasks(){
        Epic epic1 = new Epic("test", "test");
        manager.makeEpic(epic1);
        FileTaskReader.save(manager);
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertEquals(manager.getAllEpics(), newManager.getAllEpics());
        assertEquals(manager.getAllTasks(), newManager.getAllTasks());
        assertEquals(manager.getAllSubtasks(), newManager.getAllSubtasks());
        assertEquals(manager.getHistory(), newManager.getHistory());
        assertEquals(manager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
        assertTrue(newManager.getAllEpics().contains(epic1));
        assertEquals(1, newManager.getAllEpics().size());
        assertTrue(newManager.getAllSubtasks().isEmpty());
        Epic foundEpic = newManager.getEpic(epic1.getId());
        assertTrue(foundEpic.getSubtasksId().isEmpty());
    }

    @Test
    public void historyShouldBeEmptyWhenLoadFromFileWithEmptyHistory(){
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test", "test", Status.NEW);
        Task task3 = new Task("test", "test", Status.NEW);
        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeTask(task3);
        FileTaskReader.save(manager);
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void loadEpicWithTime(){
        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы чтобы не вылететь из вуза");
        manager.makeEpic(firstEpic);
        LocalDateTime time = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Subtask firstSubtask = new Subtask("Заботать линал", "Заботать линейные операторы квадрики и коники",
                Status.IN_PROGRESS, firstEpic, duration, time);
        manager.makeSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                Status.IN_PROGRESS, firstEpic);
        manager.makeSubtask(secondSubtask);
        Subtask thirdSubtask = new Subtask("Выбрать бюджет", "Понять как долго смогу поголодать",
                Status.DONE, firstEpic);
        manager.makeSubtask(thirdSubtask);
        FileTaskReader.save(manager);
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);

        assertEquals(3, manager.getPrioritizedTasks().size());
        assertEquals(time, firstEpic.getStartTime());
        assertEquals(duration, firstEpic.getDuration());
    }
}