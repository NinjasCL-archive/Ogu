package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;

import java.util.List;

/**
 * A common alias (for vals, defs, lets) not Types
 * Created by ediaz on 22-01-16.
 */
public class IdAliasDeclarationNode extends AliasDeclarationNode {

    private IdentifierNode aliasOrigin;


    public IdAliasDeclarationNode(IdentifierNode target, IdentifierNode origin, List<DecoratorNode> decoratorNodes) {
        super(target, decoratorNodes);
        this.aliasOrigin = origin;
        this.aliasOrigin.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeAliasDeclaration{" +
                "aliasTarget=" + name +
                ", aliasOrigin=" + aliasOrigin +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).add(aliasOrigin).build();
    }
}