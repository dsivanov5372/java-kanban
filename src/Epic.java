import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    ArrayList<Subtask> subtasks;

    public Epic(String title, String details, int id) {
        super(title, details, id, Status.NEW);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic)obj;
        return Objects.equals(title, epic.title) && Objects.equals(details, epic.details)
                && Objects.equals(id, epic.id) && Objects.equals(status, epic.status);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        return "{" + super.toString() + "\nSubtasks list: " + subtasks.toString() + "}";
    }

    public void addSubtask(Subtask subtask){
        if(subtasks == null){
            subtasks = new ArrayList<>();
        }
        this.subtasks.add(subtask);
        changeStatus(subtask);
    }

    public void changeStatus(Subtask subtask){
        if(subtask.status == Status.IN_PROGRESS && (status == Status.NEW || status == Status.DONE)){
            status = Status.IN_PROGRESS;
        } else if (subtask.status == Status.DONE && status != Status.DONE){
            boolean isDone = true;
            for (Subtask task : this.subtasks){
                if(task.status != Status.DONE){
                    isDone = false;
                    break;
                }
            }
            if(isDone){
                status = Status.DONE;
            }
        } else if (subtask.status == Status.NEW && status != Status.NEW){
            boolean isNew = true;
            for (Subtask task : this.subtasks){
                if(task.status != Status.NEW){
                    isNew = false;
                    break;
                }
            }
            if(isNew){
                status = Status.NEW;
            }
        }
    }

    public void removeSubtask(Subtask subtask){
        this.subtasks.remove(subtask);
        if(subtasks.isEmpty()){
            status = Status.NEW;
        }
    }

    public void removeAllSubtasks(){
        subtasks.clear();
        status = Status.NEW;
    }

    public ArrayList<Subtask> getSubtasks(){
        return subtasks;
    }
}