package lexer

import org.joda.time.DateTime

sealed trait TOKEN

class OPER extends TOKEN
class PAREN extends TOKEN
trait PIPEOPER extends OPER
trait LOGICAL_BIN_OPER extends OPER
trait COMPARATIVE_BIN_OPER extends OPER
trait SUMOPER extends OPER
trait MULOPER extends OPER

case object EOF extends TOKEN
case class ERROR(line:Int, text:String) extends TOKEN
case object INDENT extends TOKEN
case object DEDENT extends TOKEN
case object NL extends TOKEN
case class ATOM(value: String) extends TOKEN
case class ID(value: String) extends TOKEN
case class TID(value: String) extends TOKEN
trait KEYWORD extends TOKEN
trait CONTROL extends KEYWORD
case object AS extends KEYWORD
case object BIND extends KEYWORD
trait DECL extends KEYWORD
case object CATCH extends CONTROL
case object CLASS extends DECL
case object COND extends CONTROL
case object CONTAINS extends OPER
case object DATA extends DECL
case object DEF extends DECL
case object DISPATCH extends DECL
case object DO extends KEYWORD
case object ELIF extends KEYWORD
case object ELSE extends KEYWORD
case object EXTENDS extends KEYWORD
case object FOR extends CONTROL
case object FROM extends KEYWORD
case object FINALLY extends KEYWORD
case object IF extends CONTROL
case object IMPORT extends KEYWORD
case object IN extends KEYWORD
case object IS extends KEYWORD
case object LAZY extends KEYWORD
case object LET extends DECL
case object LOOP extends CONTROL
case object MODULE extends KEYWORD
case object NEW extends KEYWORD
case object OTHERWISE extends KEYWORD
case object PRIVATE extends DECL
case object PROXY extends CONTROL
case object RECORD extends DECL
case object RECUR extends CONTROL
case object REIFY extends CONTROL
case object REFER extends KEYWORD
case object REPEAT extends CONTROL
case object SET extends CONTROL
case object SYNC extends CONTROL
case object THEN extends CONTROL
case object THROW extends CONTROL
case object TRAIT extends DECL
case object TRY extends CONTROL
case object UNTIL extends CONTROL
case object USING extends CONTROL
case object VAR extends DECL
case object WHEN extends CONTROL
case object WHERE extends KEYWORD
case object WHILE extends CONTROL
case object WITH extends KEYWORD

class LITERAL extends TOKEN
class TEXT(text: String) extends LITERAL
case class STRING(value: String) extends TEXT(value)
case class FSTRING(value: String) extends TEXT(value)
case class CHAR(chr: String) extends TEXT(chr)
case class REGEXP(re: String) extends TEXT(re)
case class INT(value: Int) extends LITERAL
case class DOUBLE(value: Double) extends LITERAL
case class FLOAT(value: Float) extends LITERAL
case class LONG(value: Long) extends LITERAL
case class BIGINT(value: BigInt) extends LITERAL
case class BIGDECIMAL(value: BigDecimal) extends LITERAL
class BOOL(value: Boolean) extends LITERAL
case object TRUE extends BOOL(true)
case object FALSE extends BOOL(false)

case class ISODATETIME(value: DateTime) extends LITERAL

case object AND extends LOGICAL_BIN_OPER
case object ANDB extends OPER
case object ARROBA extends OPER
case object ARROW extends OPER
case object ASSIGN extends OPER
case object BACK_ARROW extends OPER
case object COMMA extends OPER
case object CONS extends OPER
class COMPOSEOPER extends OPER
case object COMPOSEFORWARD extends COMPOSEOPER
case object COMPOSEBACKWARD extends COMPOSEOPER
case object COLON extends OPER
case object DIV extends MULOPER
case object DOLLAR extends PIPEOPER
case object DOT extends OPER
case object DOTDOT extends OPER
case object DOTDOTLESS extends OPER
case object DOTDOTDOT extends OPER
case object DOTO extends PIPEOPER
case object DOTO_BACK extends PIPEOPER
case object EQUALS extends COMPARATIVE_BIN_OPER
case object GUARD extends OPER
case object GE extends COMPARATIVE_BIN_OPER
case object GT extends COMPARATIVE_BIN_OPER
case object LAMBDA extends TOKEN
case object LBRACKET extends PAREN
case object LCURLY extends PAREN
case object HASHLCURLY extends PAREN
case object LPAREN extends PAREN
case object LE extends COMPARATIVE_BIN_OPER
case object LT extends COMPARATIVE_BIN_OPER
case object MATCH extends COMPARATIVE_BIN_OPER
case object MATCHES extends COMPARATIVE_BIN_OPER
case object MINUS extends SUMOPER
case object MINUS_BIG extends SUMOPER
case object MOD extends MULOPER
case object MULT extends MULOPER
case object MULTBIG extends MULOPER
case object NOTEQUALS extends COMPARATIVE_BIN_OPER
case object NOTMATCHES extends COMPARATIVE_BIN_OPER
case object OR extends LOGICAL_BIN_OPER
case object PIPELEFT extends PIPEOPER
case object PIPELEFTFIRSTARG extends PIPEOPER
trait FORWARD_PIPE extends PIPEOPER
case object PIPERIGHT extends FORWARD_PIPE
case object PIPERIGHTFIRSTARG extends FORWARD_PIPE

case object PLUS extends SUMOPER
case object PLUSBIG extends SUMOPER
case object PLUSPLUS extends SUMOPER
case object POW extends OPER
case object QUESTION extends OPER
case object RBRACKET extends PAREN
case object RCURLY extends PAREN
case object RPAREN extends PAREN
case object SEMI extends OPER

object KeywordMap {

  def apply(str: String) : Option[TOKEN] = {
    table.get(str)
  }

  private[this] val table = Map(
    ("as", AS),
    ("bind", BIND),
    ("catch", CATCH),
    ("class", CLASS),
    ("cond", COND),
    ("contains", CONTAINS),
    ("data", DATA),
    ("def", DEF),
    ("dispatch", DISPATCH),
    ("do", DO),
    ("elif", ELIF),
    ("else", ELSE),
    ("extends", EXTENDS),
    ("false", FALSE),
    ("finally", FINALLY),
    ("for", FOR),
    ("from", FROM),
    ("if", IF),
    ("import", IMPORT),
    ("in", IN),
    ("is", IS),
    ("lazy", LAZY),
    ("let", LET),
    ("loop", LOOP),
    ("module", MODULE),
    ("new", NEW),
    ("otherwise", OTHERWISE),
    ("priv", PRIVATE),
    ("private", PRIVATE),
    ("proxy", PROXY),
    ("record", RECORD),
    ("recur", RECUR),
    ("reify", REIFY),
    ("repeat", REPEAT),
    ("set", SET),
    ("sync", SYNC),
    ("then", THEN),
    ("throw", THROW),
    ("trait", TRAIT),
    ("true", TRUE),
    ("try", TRY),
    ("until", UNTIL),
    ("using", USING),
    ("var", VAR),
    ("val", LET),
    ("when", WHEN),
    ("where", WHERE),
    ("while", WHILE),
    ("with", WITH))
}

object OperatorMap {

  def apply(str: String) : Option[TOKEN] = {
    table.get(str)
  }

  private[this] val table = Map(
      ("&&", AND),
      ("&", ANDB),
      ("@", ARROBA),
      ("@", ARROBA),
      ("->", ARROW),
      ("=", ASSIGN),
      ("<-", BACK_ARROW),
      (":", COLON),
      (",", COMMA),
      (">>", COMPOSEFORWARD),
      ("<<", COMPOSEBACKWARD),
      ("::", CONS),
      ("/", DIV),
      ("$", DOLLAR),
      ("...", DOTDOTDOT),
      ("..<", DOTDOTLESS),
      ("..", DOTDOT),
      (".", DOT),
      ("!>", DOTO),
      ("<!", DOTO_BACK),
      ("==", EQUALS),
      (">=", GE),
      (">", GT),
      ("|", GUARD),
      ("\\", LAMBDA),
      ("<=", LE),
      ("[", LBRACKET),
      ("{", LCURLY),
      ("#{", HASHLCURLY),
      ("(", LPAREN),
      ("<", LT),
      ("~", MATCH),
      ("=~", MATCHES),
      ("-", MINUS),
      ("-'", MINUS_BIG),
      ("%", MOD),
      ("*", MULT),
      ("*'", MULTBIG),
      ("/=", NOTEQUALS),
      ("!~", NOTMATCHES),
      ("||", OR),
      ("<|", PIPELEFT),
      ("|<", PIPELEFTFIRSTARG),
      ("|>", PIPERIGHT),
      (">|", PIPERIGHTFIRSTARG),
      ("+", PLUS),
      ("+'", PLUSBIG),
      ("++", PLUSPLUS),
      ("^", POW),
      ("?", QUESTION),
      ("]", RBRACKET),
      ("}", RCURLY),
      (")", RPAREN))
}