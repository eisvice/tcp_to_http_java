package com.httpfromtcp.internal.request;

public enum RequestState {
    INITIALIZED, PARSING_HEADERS, PARSING_BODY, DONE
}
