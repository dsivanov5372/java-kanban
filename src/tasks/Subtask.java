package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int parentEpic = -1;

    public Subtask(String title, String details, Status status, Epic parentEpic) {
        super(title, details, status);
        if (parentEpic != null) {
            this.parentEpic = parentEpic.getId();
        }
    }

    public Subtask(String title, String details, Status status, Epic parentEpic,
                   Duration duration, LocalDateTime startTime){
        super(title, details, status, duration, startTime);
        if (parentEpic != null) {
            this.parentEpic = parentEpic.getId();
        }
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask)obj;
        return Objects.equals(title, subtask.title) && Objects.equals(details, subtask.details)
                && Objects.equals(id, subtask.id) && Objects.equals(status, subtask.status) &&
                Objects.equals(duration, subtask.duration) && Objects.equals(startTime, subtask.startTime);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        return "{" + super.toString() + "\nEpic id: " + parentEpic +
                "\nstart: " + startTime +"\nend:" + getEndTime() +"}";
    }

    public void setParentEpic(Epic epic){
        parentEpic = epic.getId();
    }
    public int getParentEpic(){
        return parentEpic;
    }
}
