package net.gionn.balestra;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.gionn.balestra.ParseUtils.parseValue;

public class MetricMeasurement extends Measurement
{
    private BigDecimal maxValue;

    @Override
    public BigDecimal getStepFactor()
    {
        return new BigDecimal( "0.5" );
    }

    @Override
    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue( BigDecimal maxValue )
    {
        this.maxValue = maxValue;
    }

    @Override
    public BigDecimal getMinValue()
    {
        return BigDecimal.ZERO;
    }

    @Override
    public void parseJson( String resource )
    {
        try
        {
            JSONObject jsonObject = new JSONObject( resource );
            Iterator<String> keys = jsonObject.keys();
            while ( keys.hasNext() )
            {
                String nextKey = keys.next();
                JSONArray dataArray = jsonObject.getJSONArray( nextKey );
                BigDecimal key = parseValue( dataArray.getDouble( 0 ) );
                BigDecimal value = parseValue( dataArray.getDouble( 1 ) );
                this.data.put( key, value );
                this.setMaxValue( key );
                // spline population
                keyList.add( key.doubleValue() );
                valueList.add( value.doubleValue() );
            }
            this.setupSpline();
        }
        catch ( JSONException e )
        {
        }
    }


}
