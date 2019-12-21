package org.apirocet.digipres;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "write", description = "write/update the bag")
public class BagWriter implements Callable<Integer> {

    @ParentCommand
    private BagManager bagmanager;

    @Override
    public Integer call() {
        System.out.println("Writing bag ");
        return 0;
    }
}
