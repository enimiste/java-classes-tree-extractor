package enimiste.printers;

import enimiste.HasInfo;

import java.util.function.Consumer;

public interface ClassTreePrinter extends Consumer<HasInfo>, AutoCloseable {

}
