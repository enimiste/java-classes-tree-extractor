package enimiste;

import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class ClassInfoNode implements HasInfo {
    private final String simpleName;
    private final ClassInfoType type;
    private final int depth;
    private JarClassesTree childs;

    public ClassInfoNode(String simpleName, ClassInfoType type, int depth) {
        this.simpleName = simpleName;
        this.type = type;
        this.depth = depth;
    }

    public void addAtRoot(String className) {
        if (className == null) throw new RuntimeException("className shouldn't be null");
        if (childs == null) childs = new JarClassesTree(depth + 1);
        childs.addAtRoot(className);
    }

    public void visit(Consumer<HasInfo> visiteur) {
        visiteur.accept(this);
        if (childs != null)
            childs.visit(visiteur);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfoNode classInfoNode = (ClassInfoNode) o;
        return simpleName.equals(classInfoNode.simpleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleName);
    }

    public ClassInfoNode findBy(String className) {
        if (this.simpleName.equals(className)) return this;
        if (childs == null) return null;
        return childs.findBy(className);
    }
}
