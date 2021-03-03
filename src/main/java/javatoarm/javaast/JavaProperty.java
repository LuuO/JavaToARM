package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.token.KeywordToken;
import javatoarm.token.Token;

/**
 * Represent a property
 */
public enum JavaProperty {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE, STATIC, FINAL, NATIVE;

    /**
     * Get a property.
     *
     * @param token the property token
     * @return if the token is a valid property, returns the corresponding property.
     * Otherwise returns null.
     */
    public static JavaProperty get(Token token) {
        if (token instanceof KeywordToken) {
            return switch ((KeywordToken) token) {
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

    /**
     * Validators for properties. They can be used to check if a property is legal for some element.
     */
    @FunctionalInterface
    public interface Validator {

        /**
         * Validator for class members
         */
        Validator CLASS_MEMBER = new Validator() {
            @Override
            public void validate(JavaProperty property) throws JTAException {
                if (property == PACKAGE_PRIVATE) {
                    throw new JTAException.UnsupportedProperty(property);
                }
            }
        };

        /**
         * Validator for classes
         */
        Validator CLASS = new Validator() {
            @Override
            public void validate(JavaProperty property) throws JTAException {
                if (property == PROTECTED || property == NATIVE) {
                    throw new JTAException.UnsupportedProperty(property);
                }
            }
        };

        /**
         * Validates the property
         *
         * @param property the property
         * @throws JTAException if the property is invalid
         */
        void validate(JavaProperty property) throws JTAException;
    }
}
