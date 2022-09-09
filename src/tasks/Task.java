package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task>{
    protected Duration duration;
    protected LocalDateTime startTime;
    protected String title;
    protected String details;
    protected int id = 0;
    protected Status status;

    public Task(String title, String details, Status status){
        this.title = title;
        this.details = details;
        this.status = status;
        duration = null;
        startTime = null;
    }

    public Task(String title, String details, Status status, Duration duration, LocalDateTime startTime){
        this.title = title;
        this.details = details;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Task obj) {
        LocalDateTime time1 = this.getStartTime();
        LocalDateTime time2 = obj.getStartTime();
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
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, details, id, status);
    }

    @Override
    public String toString(){
        return "{\"title\":\"" + title + "\",\"details\":\"" + details + "\",\"status\":\"" + status + "\"," +
                    "\"duration\":\"" + duration + "\",\"startTime\":\"" + startTime + "\"}";
    }

    public LocalDateTime getEndTime(){
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime){
        this.startTime = startTime;
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

    public Duration getDuration(){
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
