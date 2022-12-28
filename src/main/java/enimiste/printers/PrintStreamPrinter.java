package enimiste.printers;

import enimiste.HasInfo;

import java.io.PrintStream;

public class PrintStreamPrinter implements ClassTreePrinter {
    private final PrintStream out;
    private final boolean closeStream;
    private final boolean useOnlySimpleName;

    public PrintStreamPrinter(PrintStream out, boolean closeStream, boolean useOnlySimpleName) {
        this.out = out;
        this.closeStream = closeStream;
        this.useOnlySimpleName = useOnlySimpleName;
    }

    @Override
    public void accept(HasInfo hasInfo) {
        String simpleName = hasInfo.getSimpleName();
        boolean ussn = useOnlySimpleName;
        try {
            Integer.parseInt(simpleName);
            ussn = true;
            simpleName = hasInfo.getFullName();
        } catch (NumberFormatException e) {
            //NOTHING
        }
        if (ussn)
            out.printf("%s%s%n", ".".repeat(hasInfo.getDepth() * 5 + 2),
                    simpleName);
        else
            out.printf("%s%s (%s)%n", ".".repeat(hasInfo.getDepth() * 5 + 2),
                    simpleName, hasInfo.getFullName());
    }

    @Override
    public void close() throws Exception {
        if (closeStream) {
            out.flush();
            out.close();
        }
    }
}
