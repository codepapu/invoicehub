package com.invoicehub.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formats amounts as {@code 1,234.50}. One place for the screen and the PDF. */
public final class MoneyFormatter {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);

    private MoneyFormatter() {
    }

    public static String format(BigDecimal value) {
        // DecimalFormat is not thread safe, so create one per call (cheap) instead of sharing it.
        return new DecimalFormat("#,##0.00", SYMBOLS).format(value == null ? BigDecimal.ZERO : value);
    }
}
