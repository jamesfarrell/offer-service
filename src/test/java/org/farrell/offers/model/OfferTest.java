package org.farrell.offers.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

public class OfferTest {

	@Test
	public void testCancellingAnOffer() {
		final Offer offer = new Offer("code", "description", LocalDateTime.now());
		assertTrue("Offer should be created as active", offer.isActive());
		offer.cancel();
		assertFalse("Offer should now be cancelled", offer.isActive());
	}
}
