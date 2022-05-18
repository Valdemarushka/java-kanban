public class Managers {

    //когда переношу TaskManager,HistoryManager и Managers в отдельный пакет, то все ломается. не могу понять почему
    //вроде везде где надо пакет импортируется...

    public static TaskManager getDefault() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        return manager;
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}

