package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.NameNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A Contract is a declarations that contains methods
 * class, trait and instance are contracts
 * Created by ediaz on 25-01-16.
 */
public class ContractDeclarationNode extends TypeDeclarationNode {

    protected List<FunctionalDeclarationNode> members;

    protected ContractDeclarationNode(NameNode name, List<FunctionalDeclarationNode> members, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
        this.members = new ArrayList<>();
        this.members.addAll(members);
        this.members.forEach((m) -> m.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(members)
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "ContractDeclaration{" +
                "name='" + name + '\''+
                ", members=" + members +
                ", decorators=" + decoratorNodes +
                '}';
    }
}