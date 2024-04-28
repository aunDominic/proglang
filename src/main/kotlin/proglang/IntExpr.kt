package proglang

sealed interface IntExpr {
    class Add(val lhs: IntExpr, val rhs: IntExpr) : IntExpr{
        override fun toString(): String {
            return "$lhs + $rhs";
        }
    }
    class Literal(val value: Int): IntExpr{
        override fun toString(): String {
            return value.toString();
        }
    }
    class Var(val name: String): IntExpr{
        override fun toString(): String {
            return name
        }
    }
    class Mul(val lhs: IntExpr, val rhs: IntExpr): IntExpr{
        override fun toString(): String {
            return "$lhs * $rhs";
        }
    }
    class Sub(val lhs: IntExpr, val rhs: IntExpr): IntExpr{
        override fun toString(): String {
            return "$lhs - $rhs";
        }
    }
    class Div(val lhs: IntExpr, val rhs: IntExpr): IntExpr{
        override fun toString(): String {
            return "$lhs / $rhs"
        }
    }
    class Fact(val expr: IntExpr): IntExpr{
        override fun toString(): String {
            return "$expr!"
        }
    }
    class Paren(val expr: IntExpr): IntExpr{
        override fun toString(): String {
            return "($expr)"
        }
    }

}

fun IntExpr.eval(store: Map<String, Int>): Int = when (this) {
    is IntExpr.Add -> lhs.eval(store) + rhs.eval(store)
    is IntExpr.Literal -> value
    is IntExpr.Var -> if (store.containsKey(name)) store[name]!! else throw UndefinedBehaviourException("Variable does not exist.");
    is IntExpr.Mul -> lhs.eval(store) * rhs.eval(store)
    is IntExpr.Sub -> lhs.eval(store) - rhs.eval(store)
    is IntExpr.Div -> if (rhs.eval(store) == 0) throw UndefinedBehaviourException("Can't divide by 0.") else lhs.eval(store) / rhs.eval(store);
    is IntExpr.Fact -> if (expr.eval(store) < 0) throw UndefinedBehaviourException("Factorial can't be negative.") else expr.evalFact(store)
    is IntExpr.Paren -> expr.eval(store)
}

fun IntExpr.evalFact(store: Map<String, Int>): Int {
    var expr = this.eval(store);
    if (expr == 1 || expr == 0) return 1;
    return expr * IntExpr.Sub(this, IntExpr.Literal(1)).evalFact(store)
}
