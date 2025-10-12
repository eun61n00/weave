package com.weave.authservice.exception;

import com.weave.common.exception.WeaveException;

import java.util.Arrays;

public class WeaveAuthException extends WeaveException {

    public WeaveAuthException(ErrorMap errorMap) {
        super(errorMap);
    }

    public WeaveAuthException(ErrorMap errorMap, String args) {
        super(errorMap, args);
    }
}
