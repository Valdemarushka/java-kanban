public class EpicTask extends Task {

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
