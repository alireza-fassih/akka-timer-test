package org.fassih.akka.testtimer;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.fassih.akka.testtimer.domin.LogicRouter;
import org.fassih.akka.testtimer.worker.DbRouter;

public class Main {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("akka-test");
        try {

            ActorRef dbRouter = system.actorOf(Props.create(DbRouter.class), "db-router");

            system.actorOf(LogicRouter.props(dbRouter), "job-supervisor");

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
