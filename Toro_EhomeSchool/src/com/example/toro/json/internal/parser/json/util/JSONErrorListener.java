package com.example.toro.json.internal.parser.json.util;

public interface JSONErrorListener {
    void start(String text);
    void error(String message, int column);
    void end();
}
