package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.LinkedList;
import java.util.List;

/**
 * A concrete functionc all
 * Created by ediaz on 21-01-16.
 */
public class FunctionCallNode extends InvocableExpressionNode {

    private ExpressionNode function;

    public ExpressionNode getFunction() {
        return function;
    }

    @Override
    public String toString() {

        return "FunctionCall{" +
                "function=(" + function + ')' +
                ", actualParams=" + actualParamNodes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionCallNode that = (FunctionCallNode) o;

        if (!actualParamNodes.equals(that.actualParamNodes)) return false;
        if (!function.equals(that.function)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + actualParamNodes.hashCode();
        return result;
    }

    public FunctionCallNode(ExpressionNode name, List<ActualParamNode> actualParamNodes) {
        super(actualParamNodes);
        this.function = name;
        this.function.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(function).addAll(actualParamNodes).build();
    }

    @Override
    public TypeUsage calcType() {
        return function.calcType().asInvocable().internalInvocableDefinitionFor(actualParamNodes).get().asFunction().getReturnType();
    }


    @Override
    protected List<? extends FormalParameter> formalParameters(SymbolResolver resolver) {
        return function.findFormalParametersFor(this).get();
    }

    public List<ExpressionNode> getActualParamValuesInOrder() {
        List<ExpressionNode> values = new LinkedList<>();
        for (ActualParamNode actualParam : actualParamNodes) {
            values.add(actualParam.getValue());
        }
        return values;
    }

}
