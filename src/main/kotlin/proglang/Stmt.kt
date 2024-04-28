package proglang

import java.lang.Exception

sealed interface Stmt {
    var next: Stmt?
    val lastInSequence: Stmt
        get() {
            var curr: Stmt = this;
            while (curr.next != null){
                curr = curr.next!!;
            }
            return curr;
        }
    fun toString(indent: Int): String
    fun clone(): Stmt

    class Assign(
        val name: String,
        val expr: IntExpr,
        override var next: Stmt? = null,
    ) : Stmt {
        override fun toString(indent: Int): String {
            var string: String = makeIndent(indent);
            string += "$name = $expr"
            if (next != null) string += "\n${next!!.toString(indent)}";
            return string;
        }

        override fun toString(): String {
            return toString(0) + "\n"
        }

        override fun clone(): Stmt {
            val next2 = if (next == null) null else next!!.clone()
            return Assign(name, expr, next2);
        }
    }

    class If(
        val condition: BoolExpr,
        val thenStmt: Stmt,
        val elseStatement: Stmt? = null,
        override var next: Stmt? = null,
    ): Stmt {
        override fun toString(indent: Int): String {
            val indentStr: String = makeIndent(indent);
            var string: String = indentStr + "if ($condition) {\n";
            string += thenStmt.toString(indent + 4) + "\n$indentStr}"
            if (elseStatement != null) {
                string += " else {\n${elseStatement.toString(indent + 4)}\n$indentStr}"
            }
            if (next != null)
                string += "\n${next!!.toString(indent)}"


            return string;
        }

        override fun toString(): String {
            return toString(0) + "\n"
        }

        override fun clone(): Stmt {
            val else2 = if (elseStatement == null) null else elseStatement.clone()
            val next2 = if (next == null) null else next!!.clone()

            return If(condition, thenStmt.clone(), else2, next2)
        }
    }

    class While(
        val condition: BoolExpr,
        val body: Stmt? = null,
        override var next: Stmt? = null,
    ): Stmt{


        override fun toString(indent: Int): String {
            val indentStr = makeIndent(indent)
            var string = indentStr + "while ($condition) {\n";
            if (body != null) string += body.toString(indent + 4);
            string += "\n$indentStr}"
            if (next != null) string += "\n" + next!!.toString(indent);
            return string;
        }

        override fun clone(): Stmt {
            val body2 = if (body == null) null else body.clone()
            val next2 = if (next == null) null else next!!.clone()
            return While(condition, body2, next2);
        }

        override fun toString(): String {
            return toString(0) + "\n";
        }

    }


}


fun Stmt.step(store: MutableMap<String, Int>): Stmt? = when (this){
    is Stmt.Assign -> assignStep(this, store)
    is Stmt.If -> ifStep(this,store)
    is Stmt.While -> whileStep(this, store)
}
fun whileStep (stmt: Stmt.While, store: MutableMap<String, Int>): Stmt?{
    val bool = stmt.condition.eval(store)
    if (store["j"] == 20) {
        println()
    }
    if (bool){
        if (stmt.body != null) {

            val newWhile = stmt.body.clone();
            val newLoop = stmt.clone();
            val temp = newLoop.next
            newLoop.next = null
            val last = newWhile.lastInSequence;
            last.next = newLoop;
            last!!.next!!.next = temp;
            return newWhile
        } else {
            return stmt
        }
    } else{
        return stmt.next
    }
}
fun ifStep(stmt: Stmt.If, store: MutableMap<String, Int>): Stmt?{
    val bool = stmt.condition.eval(store)
    if (bool) {
        stmt.thenStmt.lastInSequence.next = stmt.next
        return stmt.thenStmt
    } else if (stmt.elseStatement != null){
        stmt.elseStatement.lastInSequence.next = stmt.next
        return stmt.elseStatement
    } else {
        return stmt.next
    }
}
fun assignStep(stmt: Stmt.Assign, store: MutableMap<String, Int>): Stmt?{
    try{
        val x = stmt.expr.eval(store)
        store[stmt.name] = x
        return stmt.next
    } catch (e: Exception){
        throw UndefinedBehaviourException("Can't evaluate expression");
    }
}
fun makeIndent(indent: Int): String{
    var string = "";
    for (i in 0 until indent)
        string += " "
    return string;
}
