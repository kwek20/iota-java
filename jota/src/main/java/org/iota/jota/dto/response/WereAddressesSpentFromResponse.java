package org.iota.jota.dto.response;

/**
 * Response of {@link org.iota.jota.dto.request.IotaWereAddressesSpentFromRequest}.
 **/
public class WereAddressesSpentFromResponse extends AbstractResponse {

    private boolean[] states;

    /**
     * Gets the states.
     *
     * @return The states.
     */
    public boolean[] getStates() {
        return states;
    }
}
