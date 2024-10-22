import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class DirectoryContents {
    private List<FileObserver> observers = new ArrayList<>();
    private Map<String, Integer> fileCounts = new HashMap<>();
    private Path dir;

    public DirectoryContents(Path dir) {
        this.dir = dir;
        updateFileCounts();
    }

    /**
     * Methode zum Hinzufügen eines Observers, der Observer wird
     * sofort mit den aktuellen Dateiinformationen "versorgt"
     * @param observer Der an dem Path anzuhängende Observer
     */
    public void attach(FileObserver observer) {
        observers.add(observer);
        observer.update(fileCounts, countLines());
    }

    /**
     * Methode zum Entfernen eines Observers
     * @param observer Der zu entfernende Observer
     */
    public void detach(FileObserver observer) {
        observers.remove(observer);
    }

    /**
     * Benachrichtige alle Observer über eine Änderung
     */
    private void notifyChanges() {
        for (FileObserver observer : observers) {
            observer.update(fileCounts, countLines());
        }
    }

    /**
     * Aktualisiere die Datei-Typ Zählung und benachrichtige die Observer
     */
    public void updateFileCounts() {
        try {
            fileCounts.clear();
            Files.list(dir).forEach(file -> {
                String fileName = file.getFileName().toString();
                String fileExtension = getFileExtension(fileName);
                fileCounts.put(fileExtension, fileCounts.getOrDefault(fileExtension, 0) + 1);
            });
            notifyChanges();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Extrahiert die Dateiendung
     * @param fileName der Name der File deren Dateiendung zu extrahieren ist
     * @return die Dateiendung
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "Unknown" : fileName.substring(dotIndex + 1);
    }

    /**
     * Methode, um den Ordner zu überwachen und bei Änderungen zu reagieren
     * @throws IOException Sollte das Verzeichnis nicht exestieren
     * @throws InterruptedException
     */
    public void startWatching() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Watcher gestartet. Überwacht: " + dir.toString());

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filePath = (Path) event.context();

                System.out.println("Ereignis: " + kind.name() + " - " + filePath);
                updateFileCounts();
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    /**
     * Zählt die Zeilen, Codezeilen und Leerzeilen jeder Datei im Verzeichnis
     * @return Eine Map, die den Dateityp mit einem Array verknüpft: [Gesamtzeilen, Codezeilen, Leerzeilen]
     */
    public Map<String, int[]> countLines() {
        Map<String, int[]> lineCounts = new HashMap<>();

        try {
            Files.list(dir).forEach(file -> {
                String fileName = file.getFileName().toString();
                String fileExtension = getFileExtension(fileName);

                int totalLines = 0;
                int codeLines = 0;
                int emptyLines = 0;

                try {
                    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                    totalLines = lines.size();

                    for (String line : lines) {
                        line = line.trim();

                        if (line.isEmpty()) {
                            emptyLines++;
                        } else {
                            codeLines++;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!lineCounts.containsKey(fileExtension)) {
                    lineCounts.put(fileExtension, new int[]{totalLines, codeLines, emptyLines});
                } else {
                    int[] currentCounts = lineCounts.get(fileExtension);
                    currentCounts[0] += totalLines;
                    currentCounts[1] += codeLines;
                    currentCounts[2] += emptyLines;
                    lineCounts.put(fileExtension, currentCounts);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineCounts;
    }

}
