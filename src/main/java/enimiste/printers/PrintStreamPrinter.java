package enimiste.printers;

import enimiste.HasInfo;

import java.io.PrintStream;
import java.util.function.Consumer;

public class PrintStreamPrinter implements Consumer<HasInfo> {
    private final PrintStream out;

    public PrintStreamPrinter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void accept(HasInfo hasInfo) {
        out.println("%s%s(%s)".formatted(".".repeat(hasInfo.getDepth() * 5),
                hasInfo.getSimpleName(), hasInfo.getType().name()));
    }
}
