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
package ch.hslu.swda.entities;

import java.util.Objects;

/**
 * Tuple for Month and Number of students.
 */
public final class MonthStat {

    private int month;
    private int studentCount;

    /**
     * Default Constructor.
     */
    public MonthStat() {
        this(0, 0);
    }


    /**
     * Constructor.
     *
     * @param month        month number, zero-based.
     * @param studentCount number of students born in this month.
     */
    public MonthStat(final int month, final int studentCount) {
        this.month = month;
        this.studentCount = studentCount;
    }

    /**
     * @return month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(final int month) {
        this.month = month;
    }

    /**
     * @return the number of students
     */
    public int getStudentCount() {
        return studentCount;
    }

    /**
     * @param studentCount the number of students to set
     */
    public void setStudentCount(final int studentCount) {
        this.studentCount = studentCount;
    }

    /**
     * identical if same month and student count {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof MonthStat other
                && other.month == this.month
                && other.studentCount == this.studentCount;
    }

    /**
     * Hashcode based on fields. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.month, this.studentCount);
    }

    /**
     * String representation of month stat. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Month Stat[month=" + this.month + ", studentCount='" + this.studentCount + "]";
    }
}
