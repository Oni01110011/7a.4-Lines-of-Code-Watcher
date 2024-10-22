import java.util.Map;

public class FileWatcher implements FileObserver {

    @Override
    public void update(Map<String, Integer> fileCounts, Map<String, int[]> lineCounts) {
        System.out.println("\nDateien  Zeilen  Davon Code  Davon Leerzeilen");
        System.out.println("---------------------------------------------------------");

        fileCounts.forEach((fileType, count) -> {
            int[] lineData = lineCounts.getOrDefault(fileType, new int[]{0, 0, 0});
            int totalLines = lineData[0];
            int codeLines = lineData[1];
            int emptyLines = lineData[2];

            System.out.printf("%-12s %6d %10d %15d %18d%n", fileType, count, totalLines, codeLines, emptyLines);
        });
    }
}
