package org.apirocet.digipres;

import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "read", description = "read columns from the inventory spreadsheet")
public class CmdInventoryManagerReader implements Callable<Integer> {

    @CommandLine.ParentCommand
    InventoryManagerApp ima;

    @CommandLine.Parameters(paramLabel = "<inventory spreadsheet file>", description = "the path to the inventory spreadsheet")
    File inventoryFile;

    @CommandLine.Option(names = "--column", split = ",",
            description ="column(s) to extract and display, separated by a comma." )
    List<String> columns;

    @Override
    public Integer call() {
        if (ima.logfile != null) {
            System.setProperty("logfile", ima.logfile);
        }

        InventoryManagerReader ir = new InventoryManagerReader(inventoryFile, columns);
        return ir.read();
    }
}
