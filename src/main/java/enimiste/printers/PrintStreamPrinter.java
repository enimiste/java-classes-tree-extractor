package enimiste.printers;

import enimiste.HasInfo;

import java.io.PrintStream;

public class PrintStreamPrinter implements ClassTreePrinter {
    private final PrintStream out;
    private final boolean closeStream;

    public PrintStreamPrinter(PrintStream out, boolean closeStream) {
        this.out = out;
        this.closeStream = closeStream;
    }

    @Override
    public void accept(HasInfo hasInfo) {
        out.println("%s%s (%s)".formatted(".".repeat(hasInfo.getDepth() * 5),
                hasInfo.getSimpleName(), hasInfo.getFullName()));
    }

    @Override
    public void close() throws Exception {
        if (closeStream) {
            out.flush();
            out.close();
        }
    }
}
