package managment;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static class CustomLinkedList{
        private final HashMap<Integer, Node> table = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkLast(Task task){
            Node element = new Node();
            element.setTask(task);

            if (table.containsKey(task.getId())){
                removeNode(table.get(task.getId()));
            }

            if (head == null){
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            table.put(task.getId(), element);
        }

        private ArrayList<Task> getTasks(){
            ArrayList<Task> result = new ArrayList<>();
            for (Node element = head; element != null; element = element.getNext()){
                result.add(element.getTask());
            }
            return result;
        }

        private void removeNode(Node node){
            if (node != null) {
                table.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node getNode(int id){
            return table.get(id);
        }
    }

    private final CustomLinkedList list = new CustomLinkedList();

    @Override
    public void add(Task task) {
        list.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return list.getTasks();
    }

    @Override
    public void remove(int id){
        list.removeNode(list.getNode(id));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        InMemoryHistoryManager manager = (InMemoryHistoryManager)obj;
        return this.getHistory().equals(manager.getHistory());
    }
}
