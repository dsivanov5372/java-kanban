package test;

import managment.InMemoryTaskManager;
import managment.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

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
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @ParameterizedTest
    @ArgumentsSource(SubtaskArgumentsProvider.class)
    public void epicShouldChangeStatusWithSubtasks(Subtask subtask1, Subtask subtask2, Status status){
        subtask1.setParentEpic(epic);
        subtask2.setParentEpic(epic);
        manager.makeSubtask(subtask1);
        manager.makeSubtask(subtask2);
        assertEquals(epic.getStatus(), status);
    }

    static class SubtaskArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Subtask subtask1 = new Subtask("тест", "тест", Status.NEW, epic, null, null);
            Subtask subtask2 = new Subtask("тест", "тест", Status.NEW, epic, null, null);
            Subtask subtask3 = new Subtask("тест", "тест", Status.DONE, epic, null, null);
            Subtask subtask4 = new Subtask("тест", "тест", Status.DONE, epic, null, null);
            Subtask subtask5 = new Subtask("тест", "тест", Status.NEW, epic, null, null);
            Subtask subtask6 = new Subtask("тест", "тест", Status.DONE, epic, null, null);
            Subtask subtask7 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, null, null);
            Subtask subtask8 = new Subtask("тест", "тест", Status.IN_PROGRESS, epic, null, null);
            return Stream.of(Arguments.of(subtask1, subtask2, Status.NEW),
                            Arguments.of(subtask3, subtask4, Status.DONE),
                            Arguments.of(subtask5, subtask6, Status.IN_PROGRESS),
                            Arguments.of(subtask7, subtask8, Status.IN_PROGRESS));
        }
    }

    @Test
    public void startTimeAndEndTimeShouldBeNullIfSubtasksWithoutStartTimeAndDuration(){
        Subtask subtask = new Subtask("тест", "тест", Status.NEW, epic, null, null);
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
        Subtask subtask2 = new Subtask("тест", "тест", Status.NEW, epic, null, null);
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