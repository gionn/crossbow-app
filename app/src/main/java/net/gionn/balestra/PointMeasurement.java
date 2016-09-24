package net.gionn.balestra;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;

import static net.gionn.balestra.ParseUtils.parseValue;

public class PointMeasurement extends Measurement
{
    @Override
    public BigDecimal getStepFactor()
    {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getMaxValue()
    {
        return new BigDecimal( 30 );
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
                BigDecimal value = parseValue( dataArray.getDouble( 1 ) );
                this.data.put( parseValue( nextKey ), value );
                // spline population
                keyList.add( parseValue( nextKey ).doubleValue() );
                valueList.add( value.doubleValue() );
            }
            Collections.reverse( keyList );
            Collections.reverse( valueList );
            this.setupSpline();
        }
        catch ( JSONException e )
        {
        }
    }
}
