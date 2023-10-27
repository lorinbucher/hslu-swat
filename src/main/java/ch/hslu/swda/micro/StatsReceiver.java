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

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.entities.MonthStat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.TreeMap;

public final class StatsReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(StatsReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;

    public StatsReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    /**
     * @see ch.hslu.swda.bus.MessageReceiver#onMessageReceived(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        // log event
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);

        // unpack received message data
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<TreeMap<Integer, Integer>> typeRef = new TypeReference<>() {
            // empty
        };
        try {

            // process message data
            TreeMap<Integer, Integer> statistics = mapper.readValue(message, typeRef);
            Integer topMonth = this.findTopMonth(statistics);

            //
            this.broadcastTopBirthMonth(topMonth, statistics.get(topMonth));

            //Thread.sleep(5000);
            Thread.sleep(0);

        } catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            LOG.debug("[Thread: {}] End message processing", threadName);
        }


    }

    private Integer findTopMonth(final TreeMap<Integer, Integer> statistics) {
        Integer topMonth = 0;
        Integer maxStudents = 0;
        for (Integer month : statistics.keySet()) {
            Integer studentCount = statistics.get(month);
            if (studentCount > maxStudents) {
                maxStudents = studentCount;
                topMonth = month;
            }
        }

        return topMonth;

    }

    /**
     * @param topMonth
     * @param studentCount
     * @throws IOException
     * @throws InterruptedException
     */
    private void broadcastTopBirthMonth(final Integer topMonth, final Integer studentCount) throws IOException, InterruptedException {

        // month most students born in
        String monthName = new DateFormatSymbols().getMonths()[topMonth - 1];
        LOG.debug("Most students were born in {}: currently {} students", monthName, studentCount);
        MonthStat monthStat = new MonthStat(topMonth, studentCount);

        // broadcast top birth month event, async communication
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(monthStat);
        LOG.debug("Sending asynchronous message to broker with routing [{}]", Routes.STATISTICS_TOP_MONTH);
        bus.talkAsync(exchangeName, Routes.STATISTICS_TOP_MONTH, data);
    }


}
