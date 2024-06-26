package com.infomaximum.cluster.graphql.schema.scalartype;

import com.infomaximum.cluster.graphql.exception.GraphQLExecutorInvalidSyntaxException;
import graphql.AssertException;
import graphql.Scalars;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Set;

/**
 * Created by kris on 19.01.17.
 */
public class GraphQLScalarTypeCustom {

    public static final GraphQLTypeScalar GraphQLBoolean = new GraphQLTypeScalar(
            Scalars.GraphQLBoolean,
            Set.of(Boolean.class, boolean.class)
    );
    public static final GraphQLTypeScalar GraphQLInt = new GraphQLTypeScalar(
            Scalars.GraphQLInt,
            Set.of(Integer.class, int.class)
    );
    public static final GraphQLTypeScalar GraphQLBigDecimal = new GraphQLTypeScalar(
            "BigDecimal", "Built-in java.math.BigDecimal",
            Set.of(BigDecimal.class),
            new Coercing<BigDecimal, BigDecimal>() {

                private BigDecimal convertImpl(Object input) {
                    if (isNumberIsh(input)) {
                        try {
                            return new BigDecimal(input.toString());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return null;

                }

                @Override
                public BigDecimal serialize(Object input) {
                    BigDecimal result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingSerializeException(
                                "Expected type 'BigDecimal' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public BigDecimal parseValue(Object input) {
                    BigDecimal result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingParseValueException(
                                "Expected type 'BigDecimal' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public BigDecimal parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return new BigDecimal(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException(
                                    "Unable to turn AST input into a 'BigDecimal' : '" + String.valueOf(input) + "'"
                            );
                        }
                    } else if (input instanceof IntValue) {
                        return new BigDecimal(((IntValue) input).getValue());
                    } else if (input instanceof FloatValue) {
                        return ((FloatValue) input).getValue();
                    }
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + typeName(input) + "'."
                    );
                }
            }
    );

    public static final GraphQLTypeScalar GraphQLDouble = new GraphQLTypeScalar(
            "Double", "Built-in Double",
            Set.of(Double.class, double.class),
            new Coercing<Double, Double>() {

                private Double convertImpl(Object input) {
                    if (isNumberIsh(input)) {
                        try {
                            return Double.parseDouble(input.toString());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return null;

                }

                @Override
                public Double serialize(Object input) {
                    Double result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingSerializeException(
                                "Expected type 'Double' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Double parseValue(Object input) {
                    Double result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingParseValueException(
                                "Expected type 'Double' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Double parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return Double.parseDouble(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException(
                                    "Unable to turn AST input into a 'Double' : '" + String.valueOf(input) + "'"
                            );
                        }
                    } else if (input instanceof IntValue) {
                        return ((IntValue) input).getValue().doubleValue();
                    } else if (input instanceof FloatValue) {
                        return ((FloatValue) input).getValue().doubleValue();
                    }
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + typeName(input) + "'."
                    );
                }
            }
    );

    public static final GraphQLTypeScalar GraphQLString = new GraphQLTypeScalar(
            Scalars.GraphQLString,
            String.class
    );
    public static final GraphQLTypeScalar GraphQLFloat = new GraphQLTypeScalar(
            "Float", "Built-in Float",
            Set.of(Float.class, float.class),
            new Coercing<Float, Float>() {

                private Float convertImpl(Object input) {
                    if (isNumberIsh(input)) {
                        try {
                            return Float.parseFloat(input.toString());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return null;
                }

                @Override
                public Float serialize(Object input) {
                    Float result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingSerializeException(
                                "Expected type 'Float' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Float parseValue(Object input) {
                    Float result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingParseValueException(
                                "Expected type 'Float' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Float parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return Float.parseFloat(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException(
                                    "Unable to turn AST input into a 'Float' : '" + String.valueOf(input) + "'"
                            );
                        }
                    } else if (input instanceof IntValue) {
                        return ((IntValue) input).getValue().floatValue();
                    } else if (input instanceof FloatValue) {
                        return ((FloatValue) input).getValue().floatValue();
                    }
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + typeName(input) + "'."
                    );
                }
            }
    );
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    public static final GraphQLTypeScalar GraphQLLong = new GraphQLTypeScalar(
            "Long", "Long type",
            Set.of(Long.class, long.class),
            new Coercing<Long, Long>() {

                private Long convertImpl(Object input) {
                    if (input instanceof Long) {
                        return (Long) input;
                    } else if (isNumberIsh(input)) {
                        BigDecimal value;
                        try {
                            value = new BigDecimal(input.toString());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                        try {
                            return value.longValueExact();
                        } catch (ArithmeticException e) {
                            return null;
                        }
                    } else {
                        return null;
                    }

                }

                @Override
                public Long serialize(Object input) {
                    Long result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingSerializeException(
                                "Expected type 'Long' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Long parseValue(Object input) {
                    Long result = convertImpl(input);
                    if (result == null) {
                        throw new CoercingParseValueException(
                                "Expected type 'Long' but was '" + typeName(input) + "'."
                        );
                    }
                    return result;
                }

                @Override
                public Long parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return Long.parseLong(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException(
                                    "Expected value to be a Long but it was '" + String.valueOf(input) + "'"
                            );
                        }
                    } else if (input instanceof IntValue) {
                        BigInteger value = ((IntValue) input).getValue();
                        if (value.compareTo(LONG_MIN) < 0 || value.compareTo(LONG_MAX) > 0) {
                            throw new CoercingParseLiteralException(
                                    "Expected value to be in the Long range but it was '" + value.toString() + "'"
                            );
                        }
                        return value.longValue();
                    }
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'IntValue' or 'StringValue' but was '" + typeName(input) + "'."
                    );
                }
            }
    );

    public static final GraphQLTypeScalar GraphQLInstant = new GraphQLTypeScalar(
            "Instant", "Built-in Instant",
            Instant.class,
            new Coercing() {

                @Override
                public Object serialize(Object input) {
                    if (input == null) return null;
                    if (input instanceof Instant) {
                        return ((Instant) input).toEpochMilli();
                    } else if (input instanceof Number) {
                        return ((Number) input).longValue();
                    } else {
                        throw new RuntimeException("Not support type argument: " + input);
                    }
                }

                @Override
                public Object parseValue(Object input) {
                    if (isConvertToNumber(input)) {
                        return Instant.ofEpochMilli(toNumber(input).longValue());
                    } else {
                        throw new RuntimeException("Not support type argument: " + input);
                    }
                }

                @Override
                public Object parseLiteral(Object input) {
                    if (input instanceof IntValue) {
                        return Instant.ofEpochMilli(((IntValue) input).getValue().longValue());
                    } else {
                        throw new GraphQLExecutorInvalidSyntaxException("Not support type argument: " + input);
                    }
                }
            }
    );


    public static boolean isConvertToNumber(Object input) {
        return input instanceof Number || input instanceof String;
    }

    public static Number toNumber(Object input) {
        if (input instanceof Number) {
            return (Number) input;
        }
        if (input instanceof String) {
            try {
                return Double.parseDouble((String) input);
            } catch (NumberFormatException e) {
                throw new GraphQLExecutorInvalidSyntaxException(e);
            }
        }
        throw new AssertException("Unexpected case - this call should be protected by a previous call to isNumberIsh()");
    }

    private static boolean isNumberIsh(Object input) {
        return input instanceof Number || input instanceof String;
    }

    private static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }
}