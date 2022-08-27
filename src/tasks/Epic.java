package tasks;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

public class Epic extends Task {
    ArrayList<Integer> subtasksId;
    
    public Epic(String title, String details) {
        super(title, details, Status.NEW);
        duration = null;
        startTime = null;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic)obj;
        return Objects.equals(title, epic.title) && Objects.equals(details, epic.details)
                && Objects.equals(id, epic.id) && Objects.equals(status, epic.status) &&
                Objects.equals(duration, epic.duration) && Objects.equals(startTime, epic.startTime) &&
                Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        if(subtasksId == null){
            subtasksId = new ArrayList<>();
        }
        return "{" + super.toString() + "\nSubtasks id list: " + subtasksId.toString() +
                "\nstart: " + startTime +"\nend:" + getEndTime() +"}";
    }

    public void updateDuration(ArrayList<Subtask> subtasks){
        TreeSet<Subtask> set = new TreeSet<>((o1, o2) -> {
            LocalDateTime time1 = o1.getStartTime();
            LocalDateTime time2 = o2.getStartTime();
            if ((time1 == null && time2 == null)){
                return -1;
            }
            if (time1 == null){
                return -1;
            }
            if (time2 == null){
                return 1;
            }
            if (time1.isBefore(time2)){
                return -1;
            }
            if (time1.equals(time2)){
                return 0;
            }
            return 1;
        });

        set.addAll(subtasks);

        Subtask lastSubtask = set.last();
        Subtask firstSubtask = set.first();

        for (Subtask subtask : set){
            if (subtask.getStartTime() != null){
                firstSubtask = subtask;
                break;
            }
        }

        LocalDateTime start = firstSubtask.getStartTime();
        LocalDateTime startOfLastSubtask = lastSubtask.getStartTime();
        Duration duration = lastSubtask.getDuration();
        if (start != null && startOfLastSubtask != null && duration != null){
            this.startTime = start;
            this.duration = Duration.between(start, startOfLastSubtask.plus(duration));
        }
    }

    public void addSubtask(Subtask subtask, ArrayList<Subtask> subtasks){
        if(subtasksId == null){
            subtasksId = new ArrayList<>();
        }
        this.subtasksId.add(subtask.getId());
        subtasks.add(subtask);
        updateDuration(subtasks);
        changeStatus(subtasks);
    }

    public void changeStatus(ArrayList<Subtask> subtasks){
        if (subtasksId.isEmpty() || subtasks == null){
            status = Status.NEW;
        } else {
            int numOfDoneSubtasks = 0;
            int numOfNewSubtasks = 0;
            int numOfSubtasksInProgress = 0;

            for (Subtask subtask : subtasks){
                if (subtask.getStatus() == Status.NEW){
                    numOfNewSubtasks++;
                } else if (subtask.getStatus() == Status.IN_PROGRESS){
                    numOfSubtasksInProgress++;
                } else if (subtask.getStatus() == Status.DONE){
                    numOfDoneSubtasks++;
                }
            }

            if(numOfSubtasksInProgress > 0 || (numOfNewSubtasks > 0 && numOfDoneSubtasks > 0)) {
                status = Status.IN_PROGRESS;
            } else if (numOfNewSubtasks == subtasksId.size()){
                status = Status.NEW;
            } else {
                status = Status.DONE;
            }
        }
    }

    public void removeSubtask(Subtask subtask, ArrayList<Subtask> subtasks){
        this.subtasksId.remove(subtasksId.indexOf(subtask.getId()));
        subtasks.remove(subtask);
        changeStatus(subtasks);
    }

    public void removeAllSubtasks(){
        subtasksId.clear();
        changeStatus(null);
    }

    public ArrayList<Integer> getSubtasksId(){
        if(subtasksId == null){
            subtasksId = new ArrayList<>();
        }
        return subtasksId;
    }
}