package enimiste.printers;

import enimiste.HasInfo;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

public class HtmlPrinter implements ClassTreePrinter {
    private final PrintWriter out;
    private final boolean closeStream;
    private final Stack<String> stack = new Stack<>();
    private boolean headerWritten;
    private int lastDepth;

    public HtmlPrinter(OutputStream out, boolean closeStream) {
        this.out = new PrintWriter(out);
        this.closeStream = closeStream;
    }

    @Override
    public void accept(HasInfo hasInfo) {
        if (!headerWritten) {
            out.println("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta http-equiv="X-UA-Compatible" content="IE=edge">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Classes Tree</title>
                    </head>
                    <body>
                        <h1>Classes Tree</h1>
                        <ul>
                    """.strip());
            stack.push("""
                        </ul>
                    </body>
                    </html>
                    """.strip());
            headerWritten = true;
        }
        if (hasInfo.hasChilds()) {
            out.println("<li><span title='%s'>%s</span><ul>".formatted(hasInfo.getFullName(), hasInfo.getSimpleName()));
            stack.push("</ul></li>");
        } else {
            if (!stack.isEmpty() && hasInfo.getDepth() < lastDepth)
                out.println(stack.pop());
            out.println("<li><span title='%s'>%s</span></li>".formatted(hasInfo.getFullName(), hasInfo.getSimpleName()));
        }
        lastDepth = hasInfo.getDepth();
    }

    @Override
    public void close() throws Exception {
        while (!stack.empty()) {
            out.println(stack.pop());
        }
        if (closeStream) {
            out.flush();
            out.close();
        }
    }
}
