package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.List;

public class CodeParser {

    /**
     * Parse a piece of Java code, which could be a statement, a block, or a flow-control statement.
     *
     * @param lexer the lexer
     * @return the Java code
     * @throws JTAException if an error occurs
     */
    public static JavaCode parseCode(JavaLexer lexer) throws JTAException {
        Token next = lexer.peek();
        if (next.equals(BracketToken.CURLY_L)) {
            return parseBlock(lexer);
        } else if (ControlParser.isControlToken(next)) {
            return ControlParser.parse(lexer);
        }
        return StatementParser.parse(lexer);
    }

    /**
     * Parse a block of codes which is surrounded by curly brackets
     *
     * @param lexer the lexer
     * @return the block of code
     * @throws JTAException if an error occurs
     */
    public static JavaBlock parseBlock(JavaLexer lexer) throws JTAException {
        lexer.next(BracketToken.CURLY_L);

        List<JavaCode> body = new ArrayList<>();

        while (!lexer.nextIf(BracketToken.CURLY_R)) {
            body.add(parseCode(lexer));
            JavaParser.eatSemiColons(lexer);
        }

        return new JavaBlock(body);
    }

}

