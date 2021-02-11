package javatoarm.java.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaCode;
import javatoarm.java.JavaLoop;
import javatoarm.java.JavaScope;
import javatoarm.java.expression.JavaExpression;
import javatoarm.staticanalysis.Variable;

public interface JavaStatement extends JavaCode {

    void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException;

    class Return implements JavaStatement {
        // private final JavaFunction functionToReturn;
        private final JavaExpression returnValue;

        public Return(JavaExpression returnValue) {
            this.returnValue = returnValue;
        }

        public Return() {
            this.returnValue = null;
        }

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            if (returnValue == null) {
                subroutine.addReturn();
            } else {
                Variable returnVariable = returnValue.compileExpression(subroutine, parent);
                subroutine.addReturn(returnVariable);
                returnVariable.deleteIfIsTemp();
            }
        }
    }

    class Break implements JavaStatement {

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            JavaScope current = parent;
            // TODO support switch break
            while (!(current.owner instanceof JavaLoop)) {
                current = current.parent;
                if (current == null) {
                    throw new JTAException.NotInALoop("");
                }
            }
            JavaLoop loop = (JavaLoop) current.owner;
            loop.addBreak(subroutine);
        }
    }

    class Continue implements JavaStatement {

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            JavaScope current = parent;
            while (!(current.owner instanceof JavaLoop)) {
                current = current.parent;
                if (current == null) {
                    throw new JTAException.NotInALoop("");
                }
            }
            JavaLoop loop = (JavaLoop) current.owner;
            loop.addContinue(subroutine);
        }
    }
}
