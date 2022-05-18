public class Managers {

    public static TaskManager getDefault() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        return manager;
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
