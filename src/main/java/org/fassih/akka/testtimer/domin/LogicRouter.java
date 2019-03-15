package org.fassih.akka.testtimer.domin;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LogicRouter extends AbstractActor {


    public static Props props(ActorRef db) {
        return Props.create(LogicRouter.class, ()-> new LogicRouter(db));
    }

    private final LoggingAdapter log =
            Logging.getLogger(getContext().getSystem(), this);

    private final ActorRef db;

    private int   newNodeIndex = 0;


    private void createNode() {
        int currentIndex = this.newNodeIndex++;
        ActorRef ref = getContext().actorOf(
                LogicActor.props(db, currentIndex) , "job-" + currentIndex);
        getContext().watch(ref);
    }



    @Override
    public void preStart() throws Exception {
        super.preStart();
        int jobCount = getContext().getSystem().settings().config().getConfig("app").getInt("job-count");
        for( int i = 0; i < jobCount; i ++ ) {
            createNode();
        }
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
            .build();
    }


}
