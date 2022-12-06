package enimiste;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class JarClassesTree {
    private final int depth;
    private final Set<ClassInfoNode> nodes = new HashSet<>();

    public JarClassesTree(int depth) {
        this.depth = depth;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void visit(Consumer<HasInfo> visiteur) {
        for (var node : nodes) node.visit(visiteur);
    }

    public void addAtRoot(String className) {
        nodes.add(new ClassInfoNode(className, depth));
    }

    public void addAt(String parentClassName, String className) {
        if (className == null) throw new RuntimeException("className shouldn't be null");

        var parent = findBy(parentClassName);
        if (parent != null) {
            parent.addAtRoot(className);
        } else {
            addAtRoot(parentClassName);
            addAt(parentClassName, className);
        }
    }

    public ClassInfoNode findBy(String className) {
        for (var node : nodes) {
            var found = node.findBy(className);
            if (found != null) return found;
        }
        return null;
    }
}
