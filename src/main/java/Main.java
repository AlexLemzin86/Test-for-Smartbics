public class Main {
    public static void main(String[] args) {
        new HandleFilesService().countErrorsFromFiles(
                "src/main/resources/log1.txt",
                "src/main/resources/log2.txt",
                "src/main/resources/log3.txt");
    }
}
