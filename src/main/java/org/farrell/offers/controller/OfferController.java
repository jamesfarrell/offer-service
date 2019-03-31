package org.farrell.offers.controller;

import org.farrell.offers.handler.OfferHandler;
import org.farrell.offers.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offers")
public class OfferController {

	private static final Logger LOG = LoggerFactory.getLogger(OfferController.class);

	@Autowired
	private OfferHandler offerHandler;

	@GetMapping("/{offerCode}")
	@ResponseBody
	public Offer get(@PathVariable final String offerCode) {
		LOG.debug("Getting offer with code [{}]", offerCode);
		return offerHandler.get(offerCode);
	}

	@PutMapping("/{offerCode}/cancel")
	@ResponseBody
	public Offer cancel(@PathVariable final String offerCode) {
		LOG.debug("Cancelling offer with code [{}]", offerCode);
		return offerHandler.cancel(offerCode);
	}

	@PostMapping("/")
	@ResponseBody
	public Offer create(@RequestBody final Offer offer) {
		LOG.debug("Creating offer [{}]", offer);
		return offerHandler.create(offer);
	}
}
