package org.ogu.lang.parser.ast.modules;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.Expression;
import org.ogu.lang.parser.ast.modules.ExportsDeclaration;
import org.ogu.lang.parser.ast.modules.ModuleNameDefinition;
import org.ogu.lang.parser.ast.modules.UsesDeclaration;
import org.ogu.lang.parser.ast.typeusage.AliasDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * A Module
 * Created by ediaz on 20-01-16.
 */
public class OguModule extends Node {

    ModuleNameDefinition nameDefinition;
    private List<Node> topNodes = new ArrayList<>();
    private List<UsesDeclaration> uses = new ArrayList<>();
    private List<AliasDeclaration> aliases = new ArrayList<>();
    private List<ExportsDeclaration> exports = new ArrayList<>();

    public void add(Expression expression) {
        topNodes.add(expression);
        expression.setParent(this);
    }

    public void add(AliasDeclaration alias) {
        aliases.add(alias);
        alias.setParent(this);
    }

    public void addExports(List<ExportsDeclaration> exportsDeclarations) {
        exports.addAll(exportsDeclarations);
        for (ExportsDeclaration exportsDeclaration : exportsDeclarations)
            exportsDeclaration.setParent(this);
    }

    public void addUses(List<UsesDeclaration> usesDeclarations) {
        uses.addAll(usesDeclarations);
        for (UsesDeclaration usesDeclaration : usesDeclarations)
            usesDeclaration.setParent(this);
    }

    public void setName(ModuleNameDefinition nameDefinition) {
        if (this.nameDefinition != null) {
            this.nameDefinition.setParent(this);
        }
        this.nameDefinition = nameDefinition;
        this.nameDefinition.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(nameDefinition)
                .addAll(topNodes)
                .addAll(aliases)
                .addAll(exports)
                .addAll(uses).build();
    }

    public ModuleNameDefinition getNameDefinition() {
        return nameDefinition;
    }
}
