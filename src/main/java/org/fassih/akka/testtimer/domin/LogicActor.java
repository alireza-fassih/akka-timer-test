package org.fassih.akka.testtimer.domin;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.fassih.akka.testtimer.worker.DbWorker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.Duration;

import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LogicActor  extends AbstractActorWithTimers {

    private static String KEY = "DO-Some";
    private static SecureRandom RANDOM = new SecureRandom();

    public static Props props(ActorRef db, long id) {
        return Props.create(LogicActor.class, ()-> new LogicActor(db, id));
    }

    private static class Load { }




    private final LoggingAdapter log =
        Logging.getLogger(getContext().getSystem(), this);


    private final ActorRef db;
    private final long jobId;


    /**
     * hold the time that next job should run at
     */
    private long nextScheduled;


    @Override
    public void preStart() throws Exception {
        super.preStart();
        scheduledRandom();
    }

    private void scheduledRandom() {
        int time = RANDOM.nextInt(5000);
        log.info("scheduled for {}", time);
        nextScheduled = currentTimeMillis() + ( time * 1000 );
        timers().startSingleTimer(KEY, new Load(), Duration.ofSeconds(time));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Load.class, this::load)
            .match(DbWorker.CommandResult.class, this::handleResult)
            .build();
    }

    private void handleResult(DbWorker.CommandResult result) {
        scheduledRandom();
    }


    private void load(Load l) {
        log.info("job run with delay {} ms", ( currentTimeMillis() - nextScheduled ) );
        db.tell(new DbWorker.Command(jobId), getSelf());
    }
}