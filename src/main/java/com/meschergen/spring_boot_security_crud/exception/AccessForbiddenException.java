package com.meschergen.spring_boot_security_crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 21.12.2020
 *
 * @author MescheRGen
 */

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessForbiddenException extends RuntimeException {

}
