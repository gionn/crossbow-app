package net.gionn.balestra;

import java.math.BigDecimal;

public class ParseUtils
{
    public static BigDecimal parseValue( String value )
    {
        return new BigDecimal( value ).setScale( 2, BigDecimal.ROUND_HALF_EVEN );
    }

    public static BigDecimal parseValue( double aDouble )
    {
        return parseValue( String.valueOf( aDouble ) );
    }

    public static BigDecimal parseValue( BigDecimal value )
    {
        return parseValue( value.toString() );
    }
}
