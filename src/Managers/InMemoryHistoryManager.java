package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    List<Node<Task>> history = new ArrayList();
    Map<Integer, Node<Task>> mapNode = new HashMap<Integer, Node<Task>>();
    Node<Task> first = null;
    Node<Task> last = null;
    final int MAX_HISTORY_SIZE = 10;

    public List<Task> getTasks() {
        ArrayList<Task> historyList = new ArrayList<>();
        for (Node<Task> node : history) {
            historyList.add(node.getCurrent());
        }
        return historyList;
    }


    public void linkFirst(Task task) {
        Node<Task> nodeFirst = new Node<Task>(null, task, null);   //создали первый узел
        history.add(nodeFirst);                                     //добавили узел в список
        first = nodeFirst;                                          //указали первый узел
        last = nodeFirst;                                           //Указали последний узел
        mapNode.put(task.getId(), nodeFirst);
    }

    public void linkLast(Task task) {
        if (task != null) {
            Node<Task> nodeLast = new Node<Task>(last, task, null); //создали первый узел+сделали ссылку на предыдущий
            last.setNext(nodeLast);                                     //сделалил ссылку в предыдущем узле на нынешний
            history.add(nodeLast);                                      //добавили узел в список
            last = nodeLast;                                            //Указали последний узел
            mapNode.put(task.getId(), nodeLast);
        }

    }

    public void removeFirst(Node<Task> first) {
        Node<Task> newFirstNode = first.getNext();
        newFirstNode.setPrevious(null);
        first = newFirstNode;
        history.remove(0);
    }

    public void removeNode(Node<Task> node) {

        Node<Task> prevNode = node.getPrevious();
        Node<Task> nextNode = node.getNext();
        if (prevNode != null && nextNode != null) {
            prevNode.setNext(nextNode);
            nextNode.setPrevious(prevNode);
            history.remove(node);
        }
    }

    public void addInHistory(Task task) {
        if (task != null) {
            if (history.size() == 0) {
                linkFirst(task);
            } else if (history.size() > 0 && history.size() < MAX_HISTORY_SIZE) {
                if (!mapNode.containsKey(task.getId())) {
                    linkLast(task);
                }
                if (mapNode.containsKey(task.getId())) {
                    removeNode(mapNode.get(task.getId()));
                    linkLast(task);
                }
            } else {//если history.size()==10
                if (!mapNode.containsKey(task.getId())) {
                    removeFirst(first);
                    linkLast(task);
                }
                if (mapNode.containsKey(task.getId())) {
                    removeNode(mapNode.get(task.getId()));
                    linkLast(task);
                }
            }
        }
    }

    @Override
    public void remove(int id) {
        removeNode(mapNode.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}