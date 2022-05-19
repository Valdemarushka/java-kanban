package Managers;

public class Managers {

    //когда переношу Managers.TaskManager,Managers.HistoryManager и Managers.Managers в отдельный пакет, то все ломается. не могу понять почему
    //вроде везде где надо пакет импортируется...

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

