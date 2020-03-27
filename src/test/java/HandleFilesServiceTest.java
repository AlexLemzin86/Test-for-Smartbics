import org.junit.Test;

public class HandleFilesServiceTest {

    @Test
    public void countErrorsFromFiles() {
        new HandleFilesService().countErrorsFromFiles(
                "src/main/resources/log1.txt",
                "src/main/resources/log2.txt",
                "src/main/resources/log3.txt");
    }
}
