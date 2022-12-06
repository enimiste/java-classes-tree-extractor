package enimiste.printers;

import enimiste.HasInfo;

import java.io.OutputStream;
import java.util.function.Consumer;

public class HtmlPrinter implements Consumer<HasInfo> {
    private final OutputStream out;

    public HtmlPrinter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void accept(HasInfo hasInfo) {
        throw new RuntimeException("Not implemented");
    }
}
