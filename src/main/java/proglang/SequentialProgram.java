package proglang;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SequentialProgram {
    private Stmt topStatement;

    public SequentialProgram(Stmt topStatement){
        this.topStatement = topStatement;
    }
    public Map<String, Integer> execute (Map<String, Integer> initialStore){
        Map<String,Integer> workingStore = new HashMap<String,Integer>(initialStore);
        @Nullable
        Stmt result = StmtKt.step(topStatement, workingStore);
        if (result == null) return workingStore;
        else {
            topStatement = result;
            return execute(workingStore);
        }
    }

    @Override
    public String toString() {
        return topStatement.toString();
    }
}
