package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaBlock;
import javatoarm.java.JavaCode;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.List;

public class CodeParser {

    public static JavaCode parseCode(JavaLexer lexer) throws JTAException {
        Token next = lexer.peek();
        if (next.equals(BracketToken.CURLY_L)) {
            return parseBlock(lexer);
        }
        if (ControlParser.isControlToken(next)) {
            return ControlParser.parse(lexer);
        }
        return StatementParser.parse(lexer);
    }

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

