package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for LetDefinition and OpDefinition
 * Created by ediaz on 26-01-16.
 */
public abstract class LetDeclarationNode extends FunctionalDeclarationNode {

    List<FunctionPatternParamNode> params;
    List<FunctionNode> body;


    protected LetDeclarationNode(NameNode name, List<FunctionPatternParamNode> params, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
        this.body = new ArrayList<>();
    }

    public void add(FunctionNode node) {
        this.body.add(node);
        node.setParent(this);
    }

    public void add(ExpressionNode expr) {
        this.add(new FunctionNodeExpr(expr));
    }
}