package tasks;

import managment.InMemoryTaskManager;
import managment.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic;
    private static TaskManager manager;

    @BeforeEach
    public void makeNewEpic(){
        epic = new Epic("тест", "тест");
        manager = new InMemoryTaskManager();
        manager.makeEpic(epic);
    }

    @Test
    public void statusShouldBeNewIfNoSubtasks(){
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void statusShouldBeNewIfAllSubtasksAreNew(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.NEW, epic);
        Subtask subtask2 = new Subtask("тест", "тест", Status.NEW, epic);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void statusShouldBeDoneIfAllSubtasksAreDone(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.DONE, epic);
        Subtask subtask2 = new Subtask("тест", "тест", Status.DONE, epic);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void statusShouldBeInProgressIfAllSubtasksAreDoneAndNew(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.DONE, epic);
        Subtask subtask2 = new Subtask("тест", "тест", Status.NEW, epic);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void statusShouldBeInProgressIfAllSubtasksInProgress(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic);
        Subtask subtask2 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void startTimeAndEndTimeShouldBeNullIfSubtasksWithoutStartTimeAndDuration(){
        Subtask subtask = new Subtask("тест", "тест", Status.IN_PROGRESS, epic);
        manager.makeSubtask(subtask);
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    public void epicShouldHaveStartTimeAndDurationIfSubtasksHaveIt(){
        Subtask subtask = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                                        LocalDateTime.of(2003, 1, 28, 9, 30));
        manager.makeSubtask(subtask);
        LocalDateTime end = LocalDateTime.of(2003, 1, 28, 11, 30);
        Duration duration = Duration.ofHours(2);
        assertEquals(epic.getEndTime(), end);
        assertEquals(epic.getDuration(), duration);
    }

    @Test
    public void epicShouldHaveStartTimeAndDurationIfNotAllSubtasksHaveIt(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        manager.makeSubtask(subtask1);
        Subtask subtask2 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic);
        manager.makeSubtask(subtask2);
        LocalDateTime end = LocalDateTime.of(2003, 1, 28, 11, 30);
        Duration duration = Duration.ofHours(2);

        assertEquals(epic.getStartTime(), LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(epic.getEndTime(), end);
        assertEquals(epic.getDuration(), duration);
    }

    @Test
    public void durationCanBeLongerThenSumOfDurationOfSubtasks(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        manager.makeSubtask(subtask1);
        Subtask subtask2 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                LocalDateTime.of(2003, 1, 28, 13, 30));
        manager.makeSubtask(subtask2);
        LocalDateTime end = LocalDateTime.of(2003, 1, 28, 15, 30);
        Duration duration = Duration.ofHours(6);

        assertEquals(epic.getStartTime(), LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(epic.getEndTime(), end);
        assertEquals(epic.getDuration(), duration);
    }

    @Test
    public void shouldNotAddSubtasksWhichHasTimeIntersection(){
        Subtask subtask1 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        manager.makeSubtask(subtask1);
        Subtask subtask2 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, Duration.ofHours(2),
                LocalDateTime.of(2003, 1, 28, 10, 30));
        manager.makeSubtask(subtask2);
        LocalDateTime end = LocalDateTime.of(2003, 1, 28, 11, 30);
        Duration duration = Duration.ofHours(2);

        assertEquals(1, epic.getSubtasksId().size());
        assertEquals(end, epic.getEndTime());
        assertEquals(duration, epic.getDuration());
        assertEquals(LocalDateTime.of(2003, 1, 28, 9, 30), epic.getStartTime());
    }
}