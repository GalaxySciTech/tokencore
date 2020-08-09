package org.consenlabs.tokencore.wallet.model;

/**
 *
 * Created by pie on 2020/8/9 18: 37.
 */
public class MultiTo{

    public MultiTo(String to, long amount) {
        this.to = to;
        this.amount = amount;
    }

    private String to;

    private long amount;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
