package tasks;

import java.util.Objects;

public class Task {
    protected String title;
    protected String details;
    protected int id;
    protected Status status;

    public Task(String title, String details){
        this.title = title;
        this.details = details;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return Objects.equals(title, task.title) && Objects.equals(details, task.details)
                && Objects.equals(id, task.id) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        return "{title: " + title + "\ndetails: " + details.toString() + "\n"
                + "id: " + id + "\nstatus: " + getStringStatus() + "}";
    }

    public String getStringStatus(){
        if (status == Status.NEW){
            return "NEW";
        } else if (status == Status.IN_PROGRESS){
            return "IN_PROGRESS";
        }
        return "DONE";
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDetails(String details){
        this.details = details;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public String getTitle(){
        return title;
    }

    public String getDetails(){
        return details;
    }

    public int getId(){
        return id;
    }

    public Status getStatus(){
        return status;
    }
}
