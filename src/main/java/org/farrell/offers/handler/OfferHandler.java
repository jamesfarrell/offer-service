package org.farrell.offers.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.farrell.offers.model.Offer;
import org.farrell.offers.util.OfferAlreadyExistsException;
import org.farrell.offers.util.OfferCancelledException;
import org.farrell.offers.util.OfferExpiredException;
import org.farrell.offers.util.OfferNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OfferHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OfferHandler.class);

    private Map<String, Offer> offers = new HashMap<>();

    public Offer get(final String offerCode) {
    	final Offer offer = offers.get(offerCode);
    	if (offer == null) {
    		LOG.error("No offer found for code [[}]", offerCode);
    		throw new OfferNotFoundException();
    	}

    	if (!offer.isActive()) {
    		LOG.error("Offer [{}] is no longer active", offer);
    		throw new OfferCancelledException();
    	}

    	if (LocalDateTime.now().isAfter(offer.getExpiry())) {
    		LOG.error("Offer [{}] has expired", offer);
    		throw new OfferExpiredException();
    	}
    	return offer;
    }

    public Offer cancel(final String offerCode) {
    	final Offer offer = offers.get(offerCode);
    	if (offer == null) {
    		LOG.error("No offer found for code [[}]", offerCode);
    		throw new OfferNotFoundException();
    	}

    	offer.cancel();
    	return offer;
    }

    public Offer create(final Offer offer) {
    	final String offerCode = offer.getOfferCode();
    	if (offers.get(offerCode) != null) {
    		LOG.error("Offer already exists with code [[}], cannot create a new one", offerCode);
    		throw new OfferAlreadyExistsException();
    	}

    	offers.put(offerCode, offer);
    	return offer;
    }
}
