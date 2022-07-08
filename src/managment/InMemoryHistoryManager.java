package managment;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> queue;

    public InMemoryHistoryManager(){}

    @Override
    public void add(Task task) {
        if(queue == null){
            queue = new ArrayList<>();
        } else if (queue.size() == 10){
            queue.remove(0);
        }
        queue.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return queue;
    }
}
