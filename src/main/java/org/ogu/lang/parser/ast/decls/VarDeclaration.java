package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class VarDeclaration extends FunctionalDeclaration {

    protected ExpressionNode value;
    protected TypeNode type;

    protected VarDeclaration(TypeNode type, ExpressionNode value, List<Decorator> decorators) {
        super(decorators);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    protected VarDeclaration(ExpressionNode value, List<Decorator> decorators) {
        super(decorators);
        this.value = value;
        this.value.setParent(this);
    }


    public VarDeclaration(IdentifierNode id, ExpressionNode value, List<Decorator> decorators) {
        super(id, decorators);
        this.value = value;
        this.value.setParent(this);
    }

    public VarDeclaration(IdentifierNode id, TypeNode type, List<Decorator> decorators) {
        super(id, decorators);
        this.type = type;
        this.type.setParent(this);
    }

    public VarDeclaration(IdentifierNode id, TypeNode type, ExpressionNode value, List<Decorator> decorators) {
        super(id, decorators);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null) {
            return ImmutableList.<Node>builder().add(name).add(value).addAll(decorators).build();
        } else {
            if (value == null)
                return ImmutableList.<Node>builder().add(name).add(type).addAll(decorators).build();

            else
                return ImmutableList.<Node>builder().add(name).add(value).add(type).addAll(decorators).build();
        }
    }

    @Override
    public String toString() {
        return "ValDeclaration{" +
                "id='" + name + '\''+
                ", type="+type+
                ", value=" + value +
                ", decorators" + decorators +
                '}';
    }
}
