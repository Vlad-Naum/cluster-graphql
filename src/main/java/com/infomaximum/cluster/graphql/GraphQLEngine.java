package com.infomaximum.cluster.graphql;

import com.infomaximum.cluster.core.remote.Remotes;
import com.infomaximum.cluster.graphql.exception.GraphQLExecutorException;
import com.infomaximum.cluster.graphql.executor.GraphQLExecutor;
import com.infomaximum.cluster.graphql.executor.builder.GraphQLExecutorBuilder;
import com.infomaximum.cluster.graphql.executor.component.GraphQLComponentExecutor;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.infomaximum.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.infomaximum.cluster.graphql.preparecustomfield.PrepareCustomField;
import com.infomaximum.cluster.graphql.remote.graphql.executor.RControllerGraphQLExecutorImpl;
import com.infomaximum.cluster.graphql.remote.graphql.subscribe.RControllerGraphQLSubscribeImpl;
import com.infomaximum.cluster.graphql.schema.GraphQLSchemaType;
import com.infomaximum.cluster.graphql.schema.build.graphqltype.TypeGraphQLFieldConfigurationBuilder;
import com.infomaximum.cluster.graphql.schema.datafetcher.ComponentDataFetcher;
import com.infomaximum.cluster.graphql.schema.scalartype.GraphQLScalarTypeCustom;
import com.infomaximum.cluster.graphql.schema.scalartype.GraphQLTypeScalar;
import com.infomaximum.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.infomaximum.cluster.struct.Component;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.parser.ParserOptions;

import java.lang.reflect.Constructor;
import java.util.*;

public class GraphQLEngine {

    private final ArrayList<String> sdkPackagePaths;

    private final TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder;

    private final GraphQLSchemaType graphQLSchemaType;

    private final Constructor customRemoteDataFetcher;
    private final DataFetcherExceptionHandler dataFetcherExceptionHandler;

    private boolean introspectionDisabled = true;

    private GraphQLEngine(
            ArrayList<String> sdkPackagePaths,

            TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder,

            GraphQLSchemaType graphQLSchemaType,

            Constructor customRemoteDataFetcher,
            DataFetcherExceptionHandler dataFetcherExceptionHandler
    ) {

        this.sdkPackagePaths = sdkPackagePaths;

        this.fieldConfigurationBuilder = fieldConfigurationBuilder;

        this.graphQLSchemaType = graphQLSchemaType;

        this.customRemoteDataFetcher = customRemoteDataFetcher;
        this.dataFetcherExceptionHandler = dataFetcherExceptionHandler;
    }

    public GraphQLSchemaType getGraphQLSchemaType() {
        return graphQLSchemaType;
    }

    public GraphQLSubscribeEngine buildSubscribeEngine() {
        return new GraphQLSubscribeEngineImpl();
    }

    public void setIntrospectionDisabled(boolean introspectionDisabled) {
        this.introspectionDisabled = introspectionDisabled;
    }

    public boolean isIntrospectionDisabled() {
        return introspectionDisabled;
    }

    public GraphQLExecutor buildExecutor(Component component, GraphQLSubscribeEngine graphQLSubscribeEngine) throws GraphQLExecutorException {
        return new GraphQLExecutorBuilder(
                component,
                sdkPackagePaths,
                customRemoteDataFetcher,
                fieldConfigurationBuilder,
                graphQLSchemaType,
                (GraphQLSubscribeEngineImpl) graphQLSubscribeEngine,
                dataFetcherExceptionHandler
        ).build();
    }

    public RControllerGraphQLSubscribeImpl buildRemoteControllerGraphQLSubscribe(Component component, GraphQLSubscribeEngine graphQLSubscribeEngine) throws GraphQLExecutorException {
        return new RControllerGraphQLSubscribeImpl(component, (GraphQLSubscribeEngineImpl) graphQLSubscribeEngine);
    }

    public RControllerGraphQLExecutorImpl buildRemoteControllerGraphQLExecutor(Component component) throws GraphQLExecutorException {
        return new RControllerGraphQLExecutorImpl(component, fieldConfigurationBuilder, graphQLSchemaType);
    }

    public static class Builder {

        private ArrayList<String> sdkPackagePaths;

        private Set<PrepareCustomField> prepareCustomFields;
        private TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder;

        private Set<CustomFieldArgument> customArguments;

        private Constructor customRemoteDataFetcher;
        private DataFetcherExceptionHandler dataFetcherExceptionHandler;

        private Set<GraphQLTypeScalar> typeScalars;

        public Builder() {

            //TODO DELETE
            //-----------------DELETE AFTER 01.05.2024------------------
            ParserOptions parserOptions = ParserOptions.newParserOptions()
                    .captureIgnoredChars(false)
                    .captureSourceLocation(true)
                    .captureLineComments(false) // #comments are not useful in query parsing
                    .maxCharacters(Integer.MAX_VALUE)
                    .maxTokens(Integer.MAX_VALUE) // to prevent a billion laughs style attacks, we set a default for graphql-java
                    .maxWhitespaceTokens(Integer.MAX_VALUE)
                    .maxRuleDepth(Integer.MAX_VALUE)
                    .build();
            graphql.parser.ParserOptions.setDefaultOperationParserOptions(parserOptions);
            //-----------------DELETE AFTER 01.06.2023------------------

            typeScalars = new HashSet<GraphQLTypeScalar>();
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLBoolean);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLString);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLInt);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLLong);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLDouble);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLBigDecimal);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLFloat);
            typeScalars.add(GraphQLScalarTypeCustom.GraphQLInstant);

            dataFetcherExceptionHandler = new SimpleDataFetcherExceptionHandler();
        }

        public Builder withSDKPackage(Package sdkPackage) {
            if (sdkPackagePaths == null) {
                sdkPackagePaths = new ArrayList<>();
            }
            sdkPackagePaths.add(sdkPackage.getName());
            return this;
        }

        public Builder withSDKPackage(String sdkPackagePath) {
            if (sdkPackagePaths == null) {
                sdkPackagePaths = new ArrayList<>();
            }
            sdkPackagePaths.add(sdkPackagePath);
            return this;
        }

        public Builder withPrepareCustomField(PrepareCustomField prepareCustomField) {
            if (prepareCustomFields == null) prepareCustomFields = new HashSet<>();
            prepareCustomFields.add(prepareCustomField);
            return this;
        }

        public Builder withFieldConfigurationBuilder(TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder) {
            this.fieldConfigurationBuilder = fieldConfigurationBuilder;
            return this;
        }

        public Builder withCustomArgument(CustomFieldArgument customArgument) {
            if (customArguments == null) customArguments = new HashSet<>();
            customArguments.add(customArgument);
            return this;
        }

        public Builder withDataFetcher(Class<? extends ComponentDataFetcher> clazzComponentDataFetcher) throws GraphQLExecutorException {
            Constructor constructor;
            try {
                constructor = clazzComponentDataFetcher.getConstructor(Remotes.class, GraphQLComponentExecutor.class, GraphQLSubscribeEngineImpl.class, String.class, RGraphQLObjectTypeField.class);
            } catch (NoSuchMethodException e) {
                throw new GraphQLExecutorException("Not found constructor from ComponentDataFetcher", e);
            }
            constructor.setAccessible(true);

            customRemoteDataFetcher = constructor;
            return this;
        }

        public Builder withDataFetcherExceptionHandler(DataFetcherExceptionHandler dataFetcherExceptionHandler) {
            this.dataFetcherExceptionHandler = dataFetcherExceptionHandler;
            return this;
        }

        public Builder withTypeScalar(GraphQLTypeScalar typeScalar) {
            typeScalars.add(typeScalar);
            return this;
        }

        public GraphQLEngine build() {
            return new GraphQLEngine(
                    sdkPackagePaths,

                    fieldConfigurationBuilder,

                    new GraphQLSchemaType(
                            typeScalars,
                            (prepareCustomFields == null) ? Collections.emptySet() : prepareCustomFields,
                            (customArguments == null) ? Collections.emptySet() : customArguments
                    ),

                    customRemoteDataFetcher,
                    dataFetcherExceptionHandler
            );
        }
    }
}
