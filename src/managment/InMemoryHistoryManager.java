package managment;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private LinkedList<Task> queue;

    public InMemoryHistoryManager(){}

    @Override
    public void add(Task task) {
        if(queue == null){
            queue = new LinkedList<>();
        } else if (queue.size() == 10){
            queue.remove(0);
        }
        queue.add(task);
    }

    @Override
    public List<Task> getHistory() {
        if(queue == null){
            return new LinkedList<>();
        }
        return queue;
    }
}
