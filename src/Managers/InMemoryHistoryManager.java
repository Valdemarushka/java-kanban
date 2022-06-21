package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node<Task>> mapNode = new HashMap<Integer, Node<Task>>();
    Node<Task> first = null;
    Node<Task> last = null;


    private List<Task> getTasks() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node<Task> lastNode = last;
        Node<Task> thisNode = first;
        if (lastNode == null && thisNode == null) {
            return historyList;
        }
        while (true) {
            historyList.add(thisNode.getCurrent());
            thisNode = thisNode.getNext();
            if (thisNode == null) {
                break;
            }
        }
        return historyList;
    }


    private void linkFirst(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> nodeFirst = new Node<Task>(null, task, null);
        mapNode.put(task.getId(), nodeFirst);
        first = nodeFirst;
        last = nodeFirst;
    }

    private void linkLast(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> nodeLast = new Node<Task>(last, task, null);
        last.setNext(nodeLast);
        last = nodeLast;
        mapNode.put(task.getId(), nodeLast);
    }


    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.getPrevious() == null && node.getNext() == null) {//удаление единственного нода
            first = null;
            last = null;
        } else if (node.getPrevious() == null) {//удаление первого нода
            Node<Task> newFirstNode = node.getNext();
            newFirstNode.setPrevious(null);
            first = newFirstNode;
        } else if (node.getNext() == null) {//удаление последнего нода
            Node<Task> newLastNode = node.getPrevious();
            newLastNode.setNext(null);
            last = newLastNode;
        } else if (node.getPrevious() != null && node.getNext() != null) {//удаление средних нод
            Node<Task> prevNode = node.getPrevious();
            Node<Task> nextNode = node.getNext();
            prevNode.setNext(nextNode);
            nextNode.setPrevious(prevNode);
        }
        mapNode.remove(node.getCurrent().getId());
    }


    @Override
    public void remove(Integer id) {
        removeNode(mapNode.get(id));
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (mapNode.size() == 0) {
            linkFirst(task);
        } else {
            removeNode(mapNode.get(task.getId()));
            linkLast(task);
        }
    }


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}