package org.fassih.akka.testtimer.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.*;

import java.util.ArrayList;
import java.util.List;

public class DbRouter extends AbstractActor {


    private final LoggingAdapter log =
        Logging.getLogger(getContext().getSystem(), this);


    private Router router;
    private int    newNodeIndex = 0;


    private ActorRef createNode() {
        ActorRef ref = getContext().actorOf(
            Props.create(DbWorker.class).withDispatcher( "db-dispatcher" ) , "dbWorker-" + newNodeIndex++);
        getContext().watch(ref);
        return ref;
    }



    @Override
    public void preStart() throws Exception {
        super.preStart();

        List<Routee> routes = new ArrayList<>();
        for( int i = 0; i < 10; i ++ ) {
            routes.add(new ActorRefRoutee( createNode() ));
        }
        router = new Router(new RoundRobinRoutingLogic(), routes);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Terminated.class, this::nodeTerminated)
            .match(DbWorker.Command.class, o -> this.router.route(o, getSender()))
            .build();
    }

    private void nodeTerminated(Terminated nodeInformation) {
        this.router = this.router.removeRoutee(nodeInformation.actor());
        this.router = this.router.addRoutee(new ActorRefRoutee(createNode()));
    }


}
