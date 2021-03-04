package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.variable.JavaScope;
import javatoarm.variable.TemporaryVariable;
import javatoarm.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a function call expression
 */
public class JavaFunctionCall implements JavaExpression, JavaStatement {
    String name;
    List<JavaRightValue> arguments;

    /**
     * Constructs a new JavaFunctionCall
     *
     * @param functionPath path to the function
     * @param arguments    arguments
     */
    public JavaFunctionCall(String functionPath, List<JavaRightValue> arguments) {
        this.name = functionPath;
        this.arguments = arguments;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        List<Variable> arguments = new ArrayList<>();
        for (JavaRightValue argument : this.arguments) {
            if (argument instanceof JavaExpression) {
                arguments.add(((JavaExpression) argument).compileExpression(subroutine, parent));
            } else {
                throw new JTAException.NotImplemented(argument.toString());
            }
        }

        JavaType returnType = parent.getFunctionReturnType(name,
                arguments.stream().map(Variable::getType).collect(Collectors.toList()));
        TemporaryVariable returnValue = subroutine.getTemporary(returnType);
        subroutine.addEmptyLine();
        subroutine.addComment("calling " + name);
        subroutine.addFunctionCall("function_" + name, returnValue.getRegister(null), arguments);
        subroutine.addEmptyLine();

        arguments.forEach(Variable::deleteIfIsTemp);
        return returnValue;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable returnValue = compileExpression(subroutine, parent);
        returnValue.deleteIfIsTemp();
    }
}
