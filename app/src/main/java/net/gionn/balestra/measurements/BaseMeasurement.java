package net.gionn.balestra.measurements;

import static net.gionn.balestra.ParseUtils.parseValue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class BaseMeasurement implements Measurement {
    Map<BigDecimal, BigDecimal> data = new HashMap<>();
    List<Double> keyList = new ArrayList<>();
    List<Double> valueList = new ArrayList<>();
    private PolynomialSplineFunction splineFunction;

    BaseMeasurement(String configurationJson) {
        parseJson(configurationJson);
    }

    protected abstract void parseJson(String resource);

    @Override
    public BigDecimal getValue(BigDecimal key) {
        BigDecimal value = this.data.get(key);
        if (value == null) {
            value = parseValue(splineFunction.value(key.doubleValue()));
        }
        return value;
    }

    void setupSpline() {
        splineFunction = new SplineInterpolator().interpolate(toDoubleArray(keyList),
                toDoubleArray(valueList));
    }

    private double[] toDoubleArray(List<Double> list) {
        Double[] doubles = list.toArray(new Double[list.size()]);
        return ArrayUtils.toPrimitive(doubles);
    }
}
