package org.iota.jota.dto.response;

/**
 * Response of {@link org.iota.jota.dto.request.IotaCommandRequest}.
 **/
public class GetTipsResponse extends AbstractResponse {

    private String[] hashes;

    /**
     * Gets the hashes.
     *
     * @return The hashes.
     */
    public String[] getHashes() {
        return hashes;
    }
}
