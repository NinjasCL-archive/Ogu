package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * data Data I J = A I | B J | ...
 * Created by ediaz on 29-01-16.
 */
public class DataDeclarationNode extends AlgebraicDataTypeDeclarationNode {

    private List<TypeParamNode> params;
    private List<TypeNode> values;

    public DataDeclarationNode(TypeIdentifierNode name, List<TypeParamNode> params, List<TypeNode> values, List<TypeIdentifierNode> deriving, List<DecoratorNode> decoratorNodes) {
        super(name, deriving, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.values.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(params)
                .addAll(values)
                .addAll(deriving)
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "Data{" +
                "name='" + name + '\''+
                ", params=" + params +
                ", values=" + values +
                ", deriving=" + deriving +
                ", decorators=" + decoratorNodes +
                '}';
    }
}