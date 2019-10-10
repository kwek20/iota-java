package org.iota.jota.account.deposits;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class DepositRequest implements Serializable {
    
    private static final long serialVersionUID = -1214895100919711824L;

    Date timeOut;
    
    boolean multiUse;
    
    long expectedAmount;
    
    String message;

    /**
     * Used in json de/construction
     */
    @SuppressWarnings("unused")
    private DepositRequest() {
        
    }
    
    public DepositRequest(Date timeOut, boolean multiUse, long expectedAmount) {
        this(timeOut, multiUse, expectedAmount, "");
    }
    
    public DepositRequest(Date timeOut, boolean multiUse, long expectedAmount, Optional<String> message) {
        this(timeOut, multiUse, expectedAmount, message.orElse(""));
    }
    
    public DepositRequest(Date timeOut, boolean multiUse, long expectedAmount, String message) {
        this.timeOut = timeOut;
        this.multiUse = multiUse;
        this.expectedAmount = expectedAmount;
        this.message = message;
    }

    /**
     * The timeout after this deposit address becomes invalid (creation+timeout)
     * 
     * @return
     */
    public Date getTimeOut() {
        return timeOut;
    }
    
    /**
     * 
     * @return
     */
    public boolean hasTimeOut() {
        return timeOut != null;
    }
    
    public boolean getMultiUse() {
        return multiUse;
    }

    /**
     * Whether to expect multiple deposits to this address
     * in the given timeout.
     * If this flag is false, the deposit address is considered
     * in the input selection as soon as one deposit is available
     * (if the expected amount is set and also fulfilled)
     * 
     * @return
     */
    public boolean isMultiUse() {
        return multiUse;
    }
    
    /**
     * The message linked to this deposit request, can be empty but not <code>null</code>
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * The expected amount which gets deposited.
     * If the timeout is hit, the address is automatically
     * considered in the input selection.
     * 
     * @return
     */
    public long getExpectedAmount() {
        return expectedAmount;
    }
    
    /**
     * 
     * Checks if we are expecting a specific amount in this request
     * 
     * @return <code>true</code> if we expect anything but 0, otherwise <code>false</code>
     */
    public boolean hasExpectedAmount() {
        return getExpectedAmount() != 0;
    }
    
    
    
    @Override
    public String toString() {
        return "DepositRequest [timeOut=" + timeOut + ", multiUse=" + multiUse + ", expectedAmount=" + expectedAmount
                + ", message=" + message + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedAmount, message, multiUse, timeOut);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DepositRequest other = (DepositRequest) obj;
        return expectedAmount == other.expectedAmount && Objects.equals(message, other.message)
                && multiUse == other.multiUse && Objects.equals(timeOut, other.timeOut);
    }

    @Override
    public DepositRequest clone() throws CloneNotSupportedException {
        return (DepositRequest) super.clone();
    }
}
