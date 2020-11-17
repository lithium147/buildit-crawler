package com.solubris.buildit.crawler;

import java.util.logging.LogRecord;

/**
 * java util logging doesn't seem to support thread name
 */
public class ThreadNameFormatter extends java.util.logging.SimpleFormatter {
    public String format(LogRecord record) {
        String formatted = super.format(record);
        return Thread.currentThread().getName() + "   " + formatted;
    }
}