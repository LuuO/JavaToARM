package javatoarm.java.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.type.JavaType;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFunctionCall implements JavaExpression, JavaStatement {
    String name;
    List<JavaExpression> arguments;

    public JavaFunctionCall(String name, List<JavaExpression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        List<Variable> arguments = new ArrayList<>();
        for (JavaExpression argument : this.arguments) {
            arguments.add(argument.compileExpression(subroutine, parent));
        }

        JavaType returnType = parent.getFunctionReturnType(name,
            arguments.stream().map(Variable::getType).collect(Collectors.toList()));
        TemporaryVariable returnValue = new TemporaryVariable(parent.registerAssigner, returnType);
        subroutine.addEmptyLine();
        subroutine.addComment("calling " + name);
        subroutine.addFunctionCall("function_" + name, returnValue.getRegister(), arguments);
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
