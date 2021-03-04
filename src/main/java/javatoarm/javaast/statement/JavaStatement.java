package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.control.JavaLoop;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

/**
 * Represents a Java statement
 */
public interface JavaStatement extends JavaCode {

    /**
     * A return statement
     */
    class Return implements JavaStatement {
        private final JavaExpression returnValue;

        public Return(JavaExpression returnValue) {
            this.returnValue = returnValue;
        }

        public Return() {
            this.returnValue = null;
        }

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            if (returnValue != null) {
                Variable returnVariable = returnValue.compileExpression(subroutine, parent);
                subroutine.addReturn(returnVariable);
                returnVariable.deleteIfIsTemp();
            }
            subroutine.addJump(Condition.ALWAYS, parent.getEpilogueLabel());
        }
    }

    /**
     * A break statement
     */
    class Break implements JavaStatement {

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            // TODO support switch break
            if (!(parent.breakable instanceof JavaLoop)) {
                throw new JTAException.NotInALoop("break is not in a loop");
            }
            JavaLoop loop = (JavaLoop) parent.breakable;
            loop.addBreak(subroutine);
        }
    }

    /**
     * A continue statement
     */
    class Continue implements JavaStatement {

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            if (!(parent.breakable instanceof JavaLoop)) {
                throw new JTAException.NotInALoop("continue is not in a loop");
            }
            JavaLoop loop = (JavaLoop) parent.breakable;
            loop.addContinue(subroutine);
        }
    }
}
