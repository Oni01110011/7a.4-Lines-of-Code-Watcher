import java.util.Map;

public interface FileObserver {
    void update(Map<String, Integer> fileCounts, Map<String, int[]> lineCounts);

}
