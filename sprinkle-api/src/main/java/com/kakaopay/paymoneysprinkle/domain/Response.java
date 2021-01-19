package com.kakaopay.paymoneysprinkle.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private String code;
    private String message;
    @JsonUnwrapped
    private Object result;

    public Response() {
        super();
    }

    public Response(String code) {
        super();
        this.code = code;
    }

    public Response(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Response(String code, Object result) {
        super();
        this.code = code;
        this.result = result;
    }

    public Response(String code, String message, Object result) {
        super();
        this.code = code;
        this.message = message;
        this.result = result;
    }

}
