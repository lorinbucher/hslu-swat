/*
 * Copyright 2023 Roland Christen, HSLU Informatik, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.micro;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo für Applikationsstart.
 */
public final class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * TimerTask für periodische Ausführung.
     */
    private static final class HeartBeat extends TimerTask {

        private static final Logger LOG = LoggerFactory.getLogger(HeartBeat.class);

        private ServiceTemplate service;

        HeartBeat() {
            try {
                this.service = new ServiceTemplate();
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        @Override
        public void run() {
            try {
                service.registerStudent();
                service.askAboutUniverse();
            } catch (IOException | InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Privater Konstruktor.
     */
    private Application() {
    }

    /**
     * main-Methode. Startet einen Timer für den HeartBeat.
     *
     * @param args not used.
     */
    public static void main(final String[] args) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        LOG.info("Service starting...");
        if (!"OFF".equals(System.getenv("RABBIT"))) {
            final Timer timer = new Timer();
            timer.schedule(new HeartBeat(), 0, 10000);
        } else {
            LOG.atWarn().log("RabbitMQ disabled for testing.");
        }
        LOG.atInfo().addArgument(System.currentTimeMillis() - startTime).log("Service started in {}ms.");
        Thread.sleep(60_000);
    }
}
