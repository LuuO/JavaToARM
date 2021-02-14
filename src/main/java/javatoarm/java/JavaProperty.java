package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.token.KeywordToken;
import javatoarm.token.Token;

public enum JavaProperty {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE, STATIC, FINAL, NATIVE;

    public static JavaProperty get(Token token) {
        if (token instanceof KeywordToken) {
            return switch ((((KeywordToken) token).keyword)) {
                case _public -> PUBLIC;
                case _private -> PRIVATE;
                case _protected -> PROTECTED;
                case _static -> STATIC;
                case _final -> FINAL;
                case _native -> NATIVE;
                default -> null;
            };
        }
        return null;
    }

    @FunctionalInterface
    public interface Validator {

        Validator CLASS_MEMBER = new Validator() {
            @Override
            public void validate(JavaProperty property) throws JTAException {
                if (property == PACKAGE_PRIVATE) {
                    throw new JTAException.UnsupportedProperty(property);
                }
            }
        };

        Validator CLASS = new Validator() {
            @Override
            public void validate(JavaProperty property) throws JTAException {
                if (property == PROTECTED) {
                    throw new JTAException.UnsupportedProperty(property);
                }
            }
        };

        void validate(JavaProperty property) throws JTAException;
    }
}
