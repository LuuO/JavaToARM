package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.*;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.ArrayType;
import javatoarm.javaast.type.JavaType;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FunctionParser {

    /**
     * Parse a function declaration.
     *
     * @param lexer       the lexer
     * @param className   name of the class containing the function
     * @param annotations annotations of the function
     * @return the function
     * @throws JTAException if an error occurs
     */
    public static JavaFunction parse(JavaLexer lexer, String className,
                                     List<JavaAnnotation> annotations) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);

        List<JavaType> typeParameters = lexer.peek(AngleToken.LEFT)
                ? TypeParser.parseTypeParameters(lexer)
                : null;
        JavaType returnType = TypeParser.parseType(lexer, true);

        String methodName = lexer.peek(BracketToken.ROUND_L) && returnType.name().equals(className)
                ? returnType.toString() /* constructor */
                : JavaParser.parseName(lexer) /* other functions */;

        List<VariableDeclareStatement> arguments = parseArgumentDeclares(lexer);

        List<JavaType> exceptions = new ArrayList<>();
        if (lexer.nextIf(KeywordToken._throws)) {
            do {
                exceptions.add(TypeParser.parseType(lexer, false));
            } while (lexer.nextIf(SymbolToken.COMMA));
        }

        JavaBlock body = lexer.nextIf(SymbolToken.SEMI_COLON) ? null : CodeParser.parseBlock(lexer);

        return new JavaFunction(returnType, methodName, body, arguments, properties, typeParameters, annotations,
                exceptions);
    }

    /**
     * Parse the arguments of a function declaration. This method eats the left round bracket and the right round bracket
     * surrounding the arguments. Example: (boolean arg1, int[] arg2, arg3[]) or ()
     *
     * @param lexer the lexer
     * @return a list of argument declarations. If there is no argument, returns an empty list.
     * @throws JTAException if an error occurs
     */
    public static List<VariableDeclareStatement> parseArgumentDeclares(JavaLexer lexer)
            throws JTAException {
        List<VariableDeclareStatement> arguments = new ArrayList<>();

        lexer.next(BracketToken.ROUND_L);
        if (!lexer.nextIf(BracketToken.ROUND_R)) {
            do {
                JavaType type = TypeParser.parseType(lexer, true);
                String name = JavaParser.parseName(lexer);

                /* check array - int arg[] */
                if (lexer.nextIf(BracketToken.SQUARE_L)) {
                    lexer.next(BracketToken.SQUARE_R);
                    type = new ArrayType(type);
                }
                arguments.add(new VariableDeclareStatement(
                        type, name, null, Collections.emptySet()));

            } while (lexer.nextIf(SymbolToken.COMMA));
            lexer.next(BracketToken.ROUND_R);
        }

        return arguments;
    }

    /**
     * Parse the arguments of a function call. This method eats the left round bracket and the right round bracket
     * surrounding the arguments. Example: (arg1, arg2, arg3) or ()
     *
     * @param lexer the lexer
     * @return a list of arguments. If there is no argument, returns an empty list.
     * @throws JTAException if an error occurs
     */
    public static List<JavaRightValue> parseCallArguments(JavaLexer lexer) throws JTAException {
        List<JavaRightValue> arguments = new ArrayList<>();
        lexer.next(BracketToken.ROUND_L);
        if (!lexer.nextIf(BracketToken.ROUND_R)) {
            do {
                arguments.add(ExpressionParser.parse(lexer));
            } while (lexer.nextIf(SymbolToken.COMMA));
            lexer.next(BracketToken.ROUND_R);
        }
        return arguments;
    }
}
