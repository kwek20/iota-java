package org.iota.jota.dto.request;

import org.iota.jota.IotaAPICommands;

/**
 * This class represents the core API request 'getTransactionsToApprove'.
 **/
public class IotaGetTransactionsToApproveRequest extends IotaCommandRequest {

    private Integer depth;
    private String reference;

    /**
     * Initializes a new instance of the IotaGetTransactionsToApproveRequest class.
     */
    private IotaGetTransactionsToApproveRequest(final Integer depth, final String reference) {
        super(IotaAPICommands.GET_TRANSACTIONS_TO_APPROVE);
        this.depth = depth;
        this.reference = reference;
    }

    /**
     * Create a new instance of the IotaGetTransactionsToApproveRequest class.
     */
    public static IotaGetTransactionsToApproveRequest createIotaGetTransactionsToApproveRequest(Integer depth, final String reference) {
        return new IotaGetTransactionsToApproveRequest(depth, reference);
    }

    /**
     * Gets the depth.
     *
     * @return The depth.
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Sets the depth.
     *
     * @param depth The depth.
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getReference() {
        return reference;
    }

    /**
     * Sets a reference transaction for tip selection
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
}
