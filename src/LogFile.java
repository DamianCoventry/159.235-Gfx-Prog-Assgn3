import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class LogFile {
    public static LogFile Instance;

    static {
        try {
            Instance = new LogFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private final PrintWriter _logFile;
    public LogFile() throws FileNotFoundException, UnsupportedEncodingException {
        _logFile = new PrintWriter("Log.txt", "UTF-8");
    }
    public void write(String text) {
        _logFile.println(text);
    }
    public void close() {
        _logFile.flush();
        _logFile.close();
    }
}
