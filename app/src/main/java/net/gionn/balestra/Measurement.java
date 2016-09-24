package net.gionn.balestra;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.gionn.balestra.ParseUtils.parseValue;

public abstract class Measurement
{
    protected Map<BigDecimal, BigDecimal> data = new HashMap<>();
    protected List<Double> keyList = new ArrayList<>();
    protected List<Double> valueList = new ArrayList<>();
    private PolynomialSplineFunction splineFunction;

    public abstract BigDecimal getStepFactor();

    public abstract BigDecimal getMaxValue();

    public abstract BigDecimal getMinValue();

    public abstract void parseJson( String resource );

    public BigDecimal getValue( BigDecimal key )
    {
        BigDecimal value = this.data.get( key );
        if ( value == null)
            value = parseValue( splineFunction.value( key.doubleValue() ) );
        return value;
    }

    protected void setupSpline()
    {
        splineFunction = new SplineInterpolator().interpolate( toDoubleArray( keyList ), toDoubleArray( valueList ) );
    }

    private double[] toDoubleArray( List<Double> list)
    {
        Double[] doubles = list.toArray( new Double[list.size()] );
        return ArrayUtils.toPrimitive( doubles );
    }
}
