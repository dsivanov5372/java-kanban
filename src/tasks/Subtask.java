package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int parentEpic;
    private Manager manager;

    public Subtask(String title, String details, int parentEpic, Manager manager) {
        super(title, details);
        this.parentEpic = parentEpic;
        this.manager = manager;
        (this.manager.getEpicById(parentEpic)).addSubtask(this);
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

    @Override
    public void setStatus(Status status){
        this.status = status;
        (manager.getEpicById(parentEpic)).changeStatus(this);
    }

    public void setParentEpic(int epic){
        parentEpic = epic;
    }
    public int getParentEpic(){
        return parentEpic;
    }

    public void setManager(Manager manager){
        this.manager = manager;
    }

    public Manager getManager(Manager manager){
        return manager;
    }
}
