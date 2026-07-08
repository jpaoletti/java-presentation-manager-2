package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;

/**
 * Contract for entities that can be exported to / imported from JSON through the
 * generic export/import operations (see the ExportExec / ImportExec executors).
 *
 * {@code export()} must return a JSON array (a serialized org.json JSONArray),
 * normally containing a single object for an individual item. {@code importData()}
 * receives the JSON of a single object of that array and rebuilds the entity state.
 *
 * @author jpaoletti
 */
public interface Exportable {

    String export() throws PMException;

    void importData(String json) throws PMException;
}
