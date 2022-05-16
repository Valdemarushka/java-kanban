import java.util.List;
import java.util.ArrayList;
public class Managers {

       public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
//тут совсем не понял зачем вообще нужен этот класс и задание не особо понял. и что тестировать не могу понять.
// пытался спрашивать в слаке, но там я был не понят:(
}
