package org.farrell.offers.model;

import java.time.LocalDateTime;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Offer {

    private final String offerCode;
    private final String description;
    private final LocalDateTime expiry;
    private boolean active;

    public Offer(final String offerCode, final String description, final LocalDateTime expiry) {
    	this.offerCode = offerCode;
    	this.description = description;
    	this.expiry = expiry;
    	this.active = true;
    }

	public String getOfferCode() {
		return offerCode;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getExpiry() {
		return expiry;
	}

	public void cancel() {
		this.active = false;
	}

	public boolean isActive() {
		return active;
	}

	@Override
    public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
