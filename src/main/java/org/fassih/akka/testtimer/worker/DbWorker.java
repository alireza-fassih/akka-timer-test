package org.fassih.akka.testtimer.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

public class DbWorker extends AbstractActor {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Getter
    @RequiredArgsConstructor
    public static class Command {
        private final long id;
    }

    @Getter
    @RequiredArgsConstructor
    public static class CommandResult {
        private final long id;
        private final String result;
    }


    private final LoggingAdapter log =
        Logging.getLogger(getContext().getSystem(), this);


    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Command.class, this::handleCommand)
            .build();
    }


    private void handleCommand(Command command) {
        try {
            Thread.sleep(RANDOM.nextInt(200));
            getSender().tell( new CommandResult(command.getId(), "Some result from database !"), getSelf() );
        } catch (InterruptedException e) {
            log.error(e, "interrupted !");
            Thread.currentThread().interrupt();
        }
    }
}
