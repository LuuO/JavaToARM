package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaScope;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaName;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;

public class JavaIncrementDecrement implements JavaExpression, JavaStatement {
    public final JavaName variable;
    public final boolean post;
    public final boolean increase;

    public JavaIncrementDecrement(JavaName variable, boolean post, boolean increase) {
        this.variable = variable;
        this.post = post;
        this.increase = increase;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable variable = this.variable.compileExpression(subroutine, parent);
        if (post) {
            // TODO: improve
            TemporaryVariable temporaryVariable =
                    new TemporaryVariable(parent.registerAssigner, variable.getType());
            subroutine.addAssignment(temporaryVariable, variable);
            subroutine.addIncrementDecrement(variable, increase);
            return temporaryVariable;
        } else {
            subroutine.addIncrementDecrement(variable, increase);
            return variable;
        }
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable variable = this.variable.compileExpression(subroutine, parent);
        subroutine.addIncrementDecrement(variable, increase);
    }
}
