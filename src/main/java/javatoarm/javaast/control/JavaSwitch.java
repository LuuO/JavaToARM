package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.ImmediateExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;

import java.util.LinkedHashMap;
import java.util.List;

public class JavaSwitch implements JavaCode {

    public final JavaExpression condition;
    public final LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases;
    public final List<JavaCode> defaultCase;

    public JavaSwitch(JavaExpression condition, LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases,
                      List<JavaCode> defaultCase) {
        this.condition = condition;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }


    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
