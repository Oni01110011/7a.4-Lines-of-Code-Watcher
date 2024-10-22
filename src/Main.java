import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            DirectoryContents directorySubject = new DirectoryContents(Paths.get("TestFileFolder"));

            FileWatcher fileTypeObserver = new FileWatcher();
            directorySubject.attach(fileTypeObserver);

            directorySubject.startWatching();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
