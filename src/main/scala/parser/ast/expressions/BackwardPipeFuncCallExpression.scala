package parser.ast.expressions

import lexer.PIPE_LEFT_FIRST_ARG
import parser.{CallExpression, Expression}

case class BackwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(BackwardPipeFirstArgFuncCallExpression, PIPE_LEFT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFuncCallExpression(args)

}
