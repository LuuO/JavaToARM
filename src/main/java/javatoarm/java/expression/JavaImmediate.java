package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.Immediate;
import javatoarm.java.JavaRightValue;
import javatoarm.java.JavaScope;
import javatoarm.java.JavaType;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.ValueToken;

public class JavaImmediate implements JavaRightValue, JavaExpression {
    public final JavaType type;
    public final Object value;

    public JavaImmediate(JavaType type, Object value) {
        // TODO: check parameters
        this.type = type;
        this.value = value;
    }

    public JavaImmediate(ValueToken token) {
        this(token.getType(), token.getValue());
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        return new Immediate(type, value, parent.registerAssigner);
    }
}
