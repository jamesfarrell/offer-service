package org.farrell.offers.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;

import org.farrell.offers.model.Offer;
import org.farrell.offers.util.OfferAlreadyExistsException;
import org.farrell.offers.util.OfferCancelledException;
import org.farrell.offers.util.OfferExpiredException;
import org.farrell.offers.util.OfferNotFoundException;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class OfferHandlerTest {

	private Fixture fixture = new Fixture();

	@Test
	public void testGettingAnOffer() {
		fixture.givenAnOffer();
		fixture.whenWeGetAnOffer();
		fixture.thenTheOfferReturnedIsCorrect();
		fixture.thenTheOfferReturnedIsActive();
	}

	@Test(expected = OfferNotFoundException.class)
	public void testGettingAnOffer_NoneFound() {
		fixture.whenWeGetAnOffer();
	}

	@Test(expected = OfferCancelledException.class)
	public void testGettingAnOffer_Cancelled() {
		fixture.givenACancelledOffer();
		fixture.whenWeGetAnOffer();
	}

	@Test(expected = OfferExpiredException.class)
	public void testGettingAnOffer_Expired() {
		fixture.givenAnExpiredOffer();
		fixture.whenWeGetAnOffer();
	}


	@Test
	public void testCancellingAnOffer() {
		fixture.givenAnOffer();
		fixture.whenWeCancelAnOffer();
		fixture.thenTheOfferReturnedIsCorrect();
		fixture.thenTheOfferReturnedIsNotActive();
	}

	@Test(expected = OfferNotFoundException.class)
	public void testCancellingAnOffer_NoneFound() {
		fixture.whenWeCancelAnOffer();
	}


	@Test
	public void testCreatingAnOffer() {
		fixture.whenWeCreateAnOffer();
		fixture.thenTheOfferReturnedIsCorrect();
		fixture.thenTheOfferReturnedIsActive();
	}

	@Test(expected = OfferAlreadyExistsException.class)
	public void testCreatingAnOffer_AlreadyExists() {
		fixture.givenAnOffer();
		fixture.whenWeCreateAnOffer();
	}

	private class Fixture {
		private static final String OFFER_CODE = "code";
		private static final String DESCRIPTION = "description";
		private LocalDateTime EXPIRATION = LocalDateTime.now().plusDays(14);
		private LocalDateTime EXPIRED = LocalDateTime.now().minusDays(14);

		private OfferHandler handler;

		private Offer returnedOffer = null;

		private Fixture() {
			handler = new OfferHandler();
		}

		private Offer createOffer() {
			return new Offer(OFFER_CODE, DESCRIPTION, EXPIRATION);
		}

		private Offer createExpiredOffer() {
			return new Offer(OFFER_CODE, DESCRIPTION, EXPIRED);
		}

		@SuppressWarnings("unchecked")
		private Map<String, Offer> getCurrentOffers() {
			return (Map<String, Offer>) ReflectionTestUtils.getField(handler, "offers");
		}

		private void givenAnOffer() {
			final Map<String, Offer> offers = getCurrentOffers();
			offers.put(OFFER_CODE, createOffer());
		}

		private void givenAnExpiredOffer() {
			final Map<String, Offer> offers = getCurrentOffers();
			offers.put(OFFER_CODE, createExpiredOffer());
		}

		private void givenACancelledOffer() {
			final Map<String, Offer> offers = getCurrentOffers();
			final Offer offer = createOffer();
			offer.cancel();
			offers.put(OFFER_CODE, offer);
		}

		private void whenWeGetAnOffer() {
			returnedOffer = handler.get(OFFER_CODE);
		}

		private void whenWeCancelAnOffer() {
			returnedOffer = handler.cancel(OFFER_CODE);
		}

		private void whenWeCreateAnOffer() {
			returnedOffer = handler.create(createOffer());
		}

		private void thenTheOfferReturnedIsCorrect() {
			assertNotNull(returnedOffer);
			assertEquals("Offer Code", OFFER_CODE, returnedOffer.getOfferCode());
			assertEquals("Description", DESCRIPTION, returnedOffer.getDescription());
			assertTrue("Expiration Date", EXPIRATION.compareTo(returnedOffer.getExpiry()) == 0);
		}

		private void thenTheOfferReturnedIsActive() {
			assertTrue("Active", returnedOffer.isActive());
		}

		private void thenTheOfferReturnedIsNotActive() {
			assertFalse("Active", returnedOffer.isActive());
		}
	}
}
