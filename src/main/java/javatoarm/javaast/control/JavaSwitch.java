package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.ImmediateExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a switch block
 */
public class JavaSwitch implements JavaCode {
    public final JavaExpression condition;
    public final LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases;
    public final List<JavaCode> defaultCase;

    /**
     * Create an instance of JavaSwitch
     *
     * @param condition   the expression to evaluate
     * @param cases       a map that maps case labels to their corresponding codes
     * @param defaultCase the code to execute for the default case
     */
    public JavaSwitch(JavaExpression condition, LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases,
                      List<JavaCode> defaultCase) {
        this.condition = condition;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented("JavaSwitch");
    }
}
