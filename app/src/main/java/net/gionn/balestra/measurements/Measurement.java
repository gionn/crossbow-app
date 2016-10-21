package net.gionn.balestra.measurements;

import java.math.BigDecimal;

public interface Measurement {
    BigDecimal getStepFactor();

    BigDecimal getMaxValue();

    BigDecimal getMinValue();

    BigDecimal getDefaultValue();

    BigDecimal getValue(BigDecimal key);
}
