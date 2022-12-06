package enimiste;

import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class ClassInfoNode implements HasInfo {
    private final String fullName;
    private final String simpleName;
    private final int depth;
    private JarClassesTree childs;

    public ClassInfoNode(String fullName, int depth) {
        this.fullName = Objects.requireNonNull(fullName);
        this.depth = depth;
        {
            String s1 = fullName.substring(fullName.lastIndexOf('.') + 1);
            var i$ = s1.lastIndexOf('$');
            if (i$ != -1)
                this.simpleName = s1.substring(i$ + 1);
            else this.simpleName = s1;
        }
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
        return fullName.equals(classInfoNode.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }

    public ClassInfoNode findBy(String className) {
        if (this.fullName.equals(className)) return this;
        if (childs == null) return null;
        return childs.findBy(className);
    }
}
