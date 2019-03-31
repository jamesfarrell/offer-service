package org.farrell.offers.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public final class OfferAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

}
