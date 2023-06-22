package anonym.fx;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    @CommandLine.Option(names = {"-n", "--name"}, defaultValue = "World")
    private String name;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.printf("Hello, %s!\n", name);
        return 0;
    }
}