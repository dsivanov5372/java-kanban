package managment;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private class CustomLinkedList{
        private final HashMap<Integer, Node> table = new HashMap<Integer, Node>();
        private Node head;
        private Node tail;

        public void linkLast(Task task){
            Node element = new Node();
            element.setTask(task);

            if (table.containsKey(task.getId())){
                removeNode(table.get(task.getId()));
            }

            if (head == null){
                tail = head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            if (table.size() == 10){
                removeNode(head);
            }

            table.put(task.getId(), element);
        }

        public ArrayList<Task> getTasks(){
            ArrayList<Task> result = new ArrayList<>();
            for (Node element = head; element != null; element = element.getNext()){
                result.add(element.getTask());
            }
            return result;
        }

        public void removeNode(Node node){
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

        public Node getNode(int id){
            return table.getOrDefault(id, null);
        }
    }

    private final CustomLinkedList list = new CustomLinkedList();
    public InMemoryHistoryManager(){}

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
}
