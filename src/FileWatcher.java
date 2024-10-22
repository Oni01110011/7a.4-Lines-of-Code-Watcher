import java.util.Map;

public class FileWatcher implements FileObserver {

    @Override
    public void update(Map<String, Integer> fileCounts) {
        System.out.println("\nDateien:");
        System.out.println("-------------------");
        fileCounts.forEach((fileType, count) -> {
            System.out.printf("%-15s %d%n", fileType, count);
        });
    }
}
