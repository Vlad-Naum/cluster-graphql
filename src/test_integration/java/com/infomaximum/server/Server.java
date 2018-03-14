package com.infomaximum.server;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.ComponentBuilder;
import com.infomaximum.cluster.builder.transport.MockTransportBuilder;
import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.graphql.GraphQLEngine;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.cluster.querypool.QueryPoolExecutor;
import com.infomaximum.cluster.struct.Component;
import com.infomaximum.server.components.component1.Component1;
import com.infomaximum.server.components.frontend.FrontendComponent;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.graphql.GraphQLQuery;
import com.infomaximum.subsystems.querypool.Query;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.querypool.QueryTransaction;
import com.infomaximum.subsystems.querypool.ResourceProvider;

import java.util.concurrent.ExecutionException;

public class Server implements AutoCloseable  {

    private final Cluster cluster;
    private final GraphQLEngine graphQLEngine;

    public Server() throws ClusterException {
        INSTANCE = this;

        graphQLEngine = new GraphQLEngine.Builder()
                .withQueryPoolExecutor(new QueryPoolExecutor() {
                    @Override
                    public Object execute(Component component, GRequest request, RemoteObject source, com.infomaximum.cluster.querypool.GraphQLQuery query) {
                        GraphQLQuery graphQLQuery = (GraphQLQuery) query;

                        try {
                            return new QueryPool().execute(new Query<Object>() {

                                @Override
                                public void prepare(ResourceProvider resources) {

                                }

                                @Override
                                public Object execute(QueryTransaction transaction) throws SubsystemException {
                                    return graphQLQuery.execute(
                                            null, null, null, transaction
                                    );
                                }
                            }).get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .build();

        cluster = new Cluster.Builder()
                .withTransport(
                        new MockTransportBuilder()
                )
                .withComponent(
                        new ComponentBuilder(Component1.class)
                )
                .withComponent(
                        new ComponentBuilder(FrontendComponent.class)
                )
                .build();

    }

    public Cluster getCluster() {
        return cluster;
    }

    public GraphQLEngine getGraphQLEngine() {
        return graphQLEngine;
    }

    @Override
    public void close() {
        cluster.close();

        INSTANCE = null;
    }

    public static Server INSTANCE;
}
