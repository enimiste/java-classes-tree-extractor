package enimiste;

import enimiste.printers.HtmlPrinter;
import enimiste.printers.PrintStreamPrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    final static File JARS_FOLDER_PATH = new File("C:\\Users\\user\\.m2\\repository\\ma\\iss-apps\\ma-iss-core-framework\\grhm-2.18.13");

    public static void main(String[] args) {
        JarClassesTree tree = new JarClassesTree(0);
        try {
            if (!JARS_FOLDER_PATH.isDirectory()) return;
            URL[] jarsUrl;
            List<String> sb = new ArrayList<>();
            try (var files = Files.walk(JARS_FOLDER_PATH.toPath())
                    .map(x -> {
                        try {
                            return x.toUri().toURL();
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(url -> url.toString().endsWith(".jar"))
            ) {
                jarsUrl = files.peek(url -> sb.add("  - %s".formatted(url.toString()))).toArray(URL[]::new);
            }
            if (jarsUrl.length == 0) return;

            System.out.printf("Start processing %s jars : %n", jarsUrl.length);
            sb.forEach(System.out::println);

            try (URLClassLoader urlClassLoader = new URLClassLoader(jarsUrl)) {
                for (URL url : jarsUrl) {
                    try (JarFile jar = new JarFile(new File(url.toURI()))) {
                        List<JarEntry> classes = jar.stream()
                                .filter(j -> j.getRealName().endsWith(".class"))
                                .toList();

                        for (var cls : classes) {
                            readClass(urlClassLoader, cls.getRealName(), parentChild -> {
                                if (parentChild.childClassName == null) {
                                    tree.addAtRoot(parentChild.parentClassName);
                                } else {
                                    tree.addAt(parentChild.parentClassName, parentChild.childClassName);
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("End processing.");
        System.out.println("Printing the Tree to the Console :");
        System.out.println("=".repeat(100));
        try (var printer = new PrintStreamPrinter(System.out, false)) {
            //tree.visit(printer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String outputFile = "./tree_%d.html".formatted(System.currentTimeMillis());
        System.out.println("Printing the Tree to HTML file at %s :".formatted(outputFile));
        try (var printer = new HtmlPrinter(new FileOutputStream(outputFile), true)) {
            tree.visit(printer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=".repeat(100));
    }

    static void readClass(URLClassLoader urlClassLoader, String resourceClass,
                          Consumer<ParentChild> visiteur) throws Exception {
        readClass(urlClassLoader, resourceClass, visiteur, null);
    }

    static void readClass(URLClassLoader urlClassLoader,
                          String parentResourceClass,
                          Consumer<ParentChild> visiteur,
                          String childClass) throws Exception {
        final String parentClassName = parentResourceClass.replace('/', '.')
                .replace(".class", "");
        visiteur.accept(new ParentChild(parentClassName, childClass));
        byte[] bytes = null;
        try (InputStream inputStream = urlClassLoader.getResourceAsStream(parentResourceClass)) {
            if (inputStream != null) {
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
            }
        }
        if (bytes == null) return;
        ClassReader reader = new ClassReader(bytes);
        reader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] parents) {
                if (superName != null && !superName.isBlank() && !superName.equals("java/lang/Object")) {
                    try {
                        readClass(urlClassLoader, superName, visiteur, parentClassName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                for (String p : parents) {
                    try {
                        readClass(urlClassLoader, p, visiteur, parentClassName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 0);
    }

    @Getter
    @AllArgsConstructor
    @ToString
    private static class ParentChild {
        @NonNull
        private String parentClassName;
        private String childClassName;
    }
}
