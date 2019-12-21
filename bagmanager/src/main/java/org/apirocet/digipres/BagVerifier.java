package org.apirocet.digipres;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "verify", description = "verify the bag")
public class BagVerifier implements Callable<Integer> {

    @ParentCommand
    private BagManager bagmanager;

    @Override
    public Integer call () {
        System.out.println("Verifying bag ");
        return 0;
    }
}
