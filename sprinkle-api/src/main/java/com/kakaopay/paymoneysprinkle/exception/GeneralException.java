package com.kakaopay.paymoneysprinkle.exception;

public class GeneralException extends RuntimeException {

    private static final long serialVersionUID = -4167223085035608860L;
    protected String errorCode;

    public GeneralException() {
        super();
    }

    public GeneralException(Throwable e) {
        super(e);
    }

    public GeneralException(String errorMsg) {
        super(errorMsg);
    }

    public GeneralException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }

    public GeneralException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

    public GeneralException(String errorCode, String errorMsg, Throwable e) {
        super(errorMsg, e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
