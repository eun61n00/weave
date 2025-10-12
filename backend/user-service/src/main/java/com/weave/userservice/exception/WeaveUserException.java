package com.weave.userservice.exception;

import com.weave.common.exception.WeaveException;
import lombok.Getter;

@Getter
public class WeaveUserException extends WeaveException{

    public WeaveUserException(ErrorMap errorMap) {
        super(errorMap);
    }

    public WeaveUserException(ErrorMap errorMap, String args) {
        super(errorMap);
    }

}
