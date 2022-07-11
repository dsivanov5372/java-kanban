package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int parentEpic;

    public Subtask(String title, String details, Status status, Epic parentEpic) {
        super(title, details, status);
        this.parentEpic = parentEpic.getId();
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subTask = (Subtask)obj;
        return Objects.equals(title, subTask.title) && Objects.equals(details, subTask.details)
                && Objects.equals(id, subTask.id) && Objects.equals(status, subTask.status);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        return "{" + super.toString() + "\nEpic id: " + parentEpic + "}";
    }

    public void setParentEpic(Epic epic){
        parentEpic = epic.getId();
    }
    public int getParentEpic(){
        return parentEpic;
    }
}
