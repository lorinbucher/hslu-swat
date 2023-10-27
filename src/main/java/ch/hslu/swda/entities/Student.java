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
 * Einfaches Datenmodell eines Studenten.
 */
public final class Student {

    private static final int NOID = -1;

    private int id;
    private String firstName;
    private String lastName;
    private int monthOfBirth;

    /**
     * Default Konstruktor.
     */
    public Student() {
        this(NOID, "unknown", "unknown", 1);
    }

    /**
     * Konstruktor ohne Id.
     * @param firstName Vorname.
     * @param lastName Nachname.
     * @param monthOfBirth Geburtsmonat.
     */
    public Student(final String firstName, final String lastName, final int monthOfBirth) {
        this(NOID, firstName, lastName, monthOfBirth);
    }

    /**
     * Konstruktor.
     * @param id Eindeutig id.
     * @param firstName Vorname.
     * @param lastName Nachname.
     * @param monthOfBirth Geburtsmonat.
     */
    public Student(final int id, final String firstName, final String lastName, final int monthOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.monthOfBirth = monthOfBirth;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the monthOfBirth
     */
    public int getMonthOfBirth() {
        return monthOfBirth;
    }

    /**
     * @param monthOfBirth the monthOfBirth to set
     */
    public void setMonthOfBirth(final int monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    /**
     * Studenten mit identischer ID sind gleich. {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Student stud
                && this.id == stud.id;
    }

    /**
     * Liefert Hashcode auf Basis der ID. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Liefert eine String-Repräsentation ders Studenten. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Student[id=" + this.id + ", firstName='" + this.firstName + "', lastname='" + this.lastName
                    + ", monthOfBirth=" + this.monthOfBirth + "]";
    }
}
