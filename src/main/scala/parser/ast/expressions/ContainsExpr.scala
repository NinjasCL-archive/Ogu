package parser.ast.expressions

import lexer.{CONTAINS, NL, TokenStream}
import parser.Expression

case class ContainsExpr(val left: Expression, val right: Expression) extends Expression

object ContainsExpr extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    var expr = MatchesExpression.parse(tokens)
    if (tokens.peek(CONTAINS)) {
      val oper = tokens.consume(CONTAINS)
      tokens.consumeOptionals(NL)
      expr = ContainsExpr(expr, MatchesExpression.parse(tokens))
    }
    expr
  }
}