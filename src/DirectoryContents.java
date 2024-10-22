import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DirectoryContents {
    private List<FileObserver> observers = new ArrayList<>();
    private Map<String, Integer> fileCounts = new HashMap<>();
    private Path dir;

    public DirectoryContents(Path dir) {
        this.dir = dir;
        updateFileCounts();
    }

    public void attach(FileObserver observer) {
        observers.add(observer);
        observer.update(fileCounts);
    }

    public void detach(FileObserver observer) {
        observers.remove(observer);
    }

    private void notifyChange() {
        for (FileObserver observer : observers) {
            observer.update(fileCounts);
        }
    }

    public void updateFileCounts() {
        try {
            fileCounts.clear();
            Files.list(dir).forEach(file -> {
                String fileName = file.getFileName().toString();
                String fileExtension = getFileExtension(fileName);
                fileCounts.put(fileExtension, fileCounts.getOrDefault(fileExtension, 0) + 1);
            });
            notifyChange();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "Unknown" : fileName.substring(dotIndex + 1);
    }


}
