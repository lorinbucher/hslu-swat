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

/**
 * Holds all constants for message routes.
 */
public final class Routes {

    static final String STUDENT_REGISTER = "student.register";
    static final String STATISTICS_TOP_MONTH = "statistics.top-month";
    static final String STATISTICS_CHANGED = "statistics.changed";
    static final String DEEP_THOUGHT_ASK = "deep-thought.ask";
    static final String TEMPLATE_CHAT = "template.chat";

    /**
     * No instance allowed.
     */
    private Routes() {
    }
}
