package io.skodjob;

public class CommandLineOptions {
    private String filePath;
    private String generatePath;

    public CommandLineOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--filePath")) {
                filePath = args[++i];
            } else if (arg.equals("--generatePath")) {
                generatePath = args[++i];
            } else if (arg.contains("--")) {
                throw new RuntimeException("Unsupported command line option " + arg);
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getGeneratePath() {
        return generatePath;
    }
}
