package tasks;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList<Subtask> subtasks;

    public Epic(String title, String details) {
        super(title, details, Status.NEW);
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
        changeStatus();
    }

    public void changeStatus(){
        if (subtasks.isEmpty()){
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
            } else if (numOfNewSubtasks == subtasks.size()){
                status = Status.NEW;
            } else {
                status = Status.DONE;
            }
        }
    }

    public void removeSubtask(Subtask subtask){
        this.subtasks.remove(subtask);
        changeStatus();
    }

    public void removeAllSubtasks(){
        subtasks.clear();
        status = Status.NEW;
    }

    public ArrayList<Subtask> getSubtasks(){
        return subtasks;
    }
}