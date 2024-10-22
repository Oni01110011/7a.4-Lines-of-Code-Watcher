import java.io.File;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class FileWatcher implements FileObserver {

    @Override
    public void update(Map<String, Integer> fileCounts) {
        System.out.println("\nDateien");
        System.out.println("-------------------");
        fileCounts.forEach((fileType, count) -> {
            System.out.printf("%-15s %d%n", fileType, count);
        });
        System.out.println("-------------------");
    }
}