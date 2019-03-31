package org.farrell.offers.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.farrell.offers.handler.OfferHandler;
import org.farrell.offers.model.Offer;
import org.farrell.offers.util.OfferAlreadyExistsException;
import org.farrell.offers.util.OfferCancelledException;
import org.farrell.offers.util.OfferExpiredException;
import org.farrell.offers.util.OfferNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(OfferController.class)
public class OfferControllerTest {

	private static final String OFFER_CODE = "code";
	private static final String DESCRIPTION = "description";
	private static final String EXPIRATION = "2019-04-30T18:00:00";

	private static final String OFFER_JSON = new StringBuilder()
			.append("{")
			.append("\"offerCode\":\"").append(OFFER_CODE).append("\",")
			.append("\"description\":\"").append(DESCRIPTION).append("\",")
			.append("\"expiry\":\"").append(EXPIRATION).append("\"")
			.append("}")
			.toString();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private OfferHandler offerHandler;

	@Test
	public void givenAnOffer_whenGetOffer_thenReturnJson() throws Exception {

		given(offerHandler.get(OFFER_CODE)).willReturn(createOffer());

		mvc.perform(get("/offers/" + OFFER_CODE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("offerCode").value(OFFER_CODE))
		.andExpect(jsonPath("description").value(DESCRIPTION))
		.andExpect(jsonPath("expiry").value(EXPIRATION))
		.andExpect(jsonPath("active").value("true"));
	}

	@Test
	public void givenAnExpiredOffer_whenGetOffer_thenReturnNotFound() throws Exception {

		given(offerHandler.get(OFFER_CODE)).willThrow(OfferExpiredException.class);

		mvc.perform(get("/offers/" + OFFER_CODE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().string(""));
	}

	@Test
	public void givenACancelledOffer_whenGetOffer_thenReturnNotFound() throws Exception {

		given(offerHandler.get(OFFER_CODE)).willThrow(OfferCancelledException.class);

		mvc.perform(get("/offers/" + OFFER_CODE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().string(""));
	}

	@Test
	public void givenNoOffer_whenGetOffer_thenReturnNotFound() throws Exception {

		given(offerHandler.get(OFFER_CODE)).willThrow(OfferNotFoundException.class);

		mvc.perform(get("/offers/" + OFFER_CODE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().string(""));
	}

	@Test
	public void givenAnOffer_whenCancelOffer_thenReturnJson() throws Exception {

		given(offerHandler.cancel(OFFER_CODE)).willReturn(createCancelledOffer());

		mvc.perform(put("/offers/" + OFFER_CODE + "/cancel")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("offerCode").value(OFFER_CODE))
		.andExpect(jsonPath("description").value(DESCRIPTION))
		.andExpect(jsonPath("expiry").value(EXPIRATION))
		.andExpect(jsonPath("active").value("false"));
	}

	@Test
	public void givenNoOffer_whenCancelOffer_thenReturnNotFound() throws Exception {

		given(offerHandler.cancel(OFFER_CODE)).willThrow(OfferNotFoundException.class);

		mvc.perform(put("/offers/" + OFFER_CODE + "/cancel")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(content().string(""));
	}

	@Test
	public void givenNoExistingOffer_whenCreateOffer_thenReturnJson() throws Exception {

		given(offerHandler.create(any(Offer.class))).willReturn(createOffer());

		mvc.perform(post("/offers/")
				.content(OFFER_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("offerCode").value(OFFER_CODE))
		.andExpect(jsonPath("description").value(DESCRIPTION))
		.andExpect(jsonPath("expiry").value(EXPIRATION))
		.andExpect(jsonPath("active").value("true"));
	}

	@Test
	public void givenAnExistingOffer_whenCreateOffer_thenReturnJson() throws Exception {

		given(offerHandler.create(any(Offer.class))).willThrow(OfferAlreadyExistsException.class);

		mvc.perform(post("/offers/")
				.content(OFFER_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict())
		.andExpect(content().string(""));
	}

	private Offer createOffer() {
		return new Offer(OFFER_CODE, DESCRIPTION, createExpirationDate(EXPIRATION));
	}

	private Offer createCancelledOffer() {
		final Offer offer = createOffer();
		offer.cancel();
		return offer;
	}

	private LocalDateTime createExpirationDate(final String dateTime) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return LocalDateTime.parse(dateTime, formatter);
	}
}
