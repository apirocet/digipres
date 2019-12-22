package org.apirocet.digipres;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "write", description = "write/update the bag")
public class BagWriter implements Callable<Integer> {

    @CommandLine.Parameters(paramLabel = "<bag directory>", description = "the path to the bag directory")
    File bagdir;

    @Override
    public Integer call() {
        System.out.println("Writing bag " + bagdir);
        return 0;
    }
}
