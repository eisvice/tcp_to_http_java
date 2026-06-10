package com.httpfromtcp.internal.server;

public enum WriterState {
    Initialized, WriteStatusLine, WriteHeaders, WriteBody, Done
}
