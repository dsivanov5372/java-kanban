package tasks;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList<Integer> subtasksId;
    
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
        if(subtasksId == null){
            subtasksId = new ArrayList<>();
        }
        return "{" + super.toString() + "\nSubtasks id list: " + subtasksId.toString() + "}";
    }

    public void addSubtask(Subtask subtask, ArrayList<Subtask> subtasks){
        if(subtasksId == null){
            subtasksId = new ArrayList<>();
        }
        this.subtasksId.add(subtask.getId());
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