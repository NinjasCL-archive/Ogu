package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;

import java.util.ArrayList;
import java.util.List;

/**
 * A Contract is a declarations that contains methods
 * class, trait and instance are contracts
 * Created by ediaz on 25-01-16.
 */
public class ContractDeclaration extends TypeDeclaration {

    protected List<FunctionalDeclaration> members;

    protected ContractDeclaration(OguName name, List<FunctionalDeclaration> members, List<Decorator> decorators) {
        super(name, decorators);
        this.members = new ArrayList<>();
        this.members.addAll(members);
        this.members.forEach((m) -> m.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(members)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "ContractDeclaration{" +
                "name='" + name + '\''+
                ", members=" + members +
                ", decorators=" + decorators +
                '}';
    }
}