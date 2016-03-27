package net.gionn.balestra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    public static final BigDecimal STEP_FACTOR = new BigDecimal( "0.5" );
    public static final String DB_JSON = "db.json";
    private Map<BigDecimal, BigDecimal> data = new HashMap<>();

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        InputStream open = null;
        try
        {
            String json = IOUtils.toString( getAssets().open( DB_JSON ) );
            initJsonData( json );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private void initJsonData( String resource )
    {
        try
        {
            JSONObject jsonObject = new JSONObject( resource );
            Iterator<String> keys = jsonObject.keys();
            while ( keys.hasNext() )
            {
                String nextKey = keys.next();
                JSONArray dataArray = jsonObject.getJSONArray( nextKey );
                BigDecimal key = bigDecimalFactory( dataArray.getDouble( 0 ) );
                BigDecimal value = bigDecimalFactory( dataArray.getDouble( 1 ) );
                data.put( key, value );
            }
        }
        catch ( JSONException e )
        {


        }
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings )
        {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    public void calculate( View view )
    {
        TextView verticalSizeText = (TextView) findViewById( R.id.verticalSize );
        TextView horizontalSizeText = (TextView) findViewById( R.id.horizontalSize );
        TextView verticalResult = (TextView) findViewById( R.id.verticalResult );
        TextView horizontalResult = (TextView) findViewById( R.id.horizontalResult );

        BigDecimal verticalInput = bigDecimalFactory( verticalSizeText.getText().toString() );
        BigDecimal horizontalInput = bigDecimalFactory( horizontalSizeText.getText().toString() );

        BigDecimal verticalOutput = getOrNear( verticalInput );
        BigDecimal horizontalOutput = getOrNear( horizontalInput );


        verticalResult.setText( verticalOutput.toString() );
        horizontalResult.setText( horizontalOutput.toString() );
    }

    private BigDecimal getOrNear( BigDecimal lookup )
    {
        if ( lookup.compareTo( BigDecimal.ZERO ) == -1 )
            return zero();

        BigDecimal value = data.get( lookup );
        if ( value == null)
            value = getOrNear( lookup.subtract( STEP_FACTOR ) );
        return value;
    }

    public void verticalPlus( View view )
    {
        TextView sizeView = (TextView) findViewById( R.id.verticalSize );
        add( view, sizeView );
    }

    public void horizontalPlus( View view )
    {
        TextView sizeView = (TextView) findViewById( R.id.horizontalSize );
        add( view, sizeView );
    }

    public void verticalMinus( View view )
    {
        TextView sizeView = (TextView) findViewById( R.id.verticalSize );
        subtract( view, sizeView );
    }

    public void horizontalMinus( View view )
    {
        TextView sizeView = (TextView) findViewById( R.id.horizontalSize );
        subtract( view, sizeView );
    }

    private void add( View view, TextView sizeView )
    {
        BigDecimal number = bigDecimalFactory( sizeView.getText().toString() );
        sizeView.setText( number.add( STEP_FACTOR ).toString() );
        calculate( view );
    }

    private void subtract( View view, TextView sizeView )
    {
        BigDecimal number = bigDecimalFactory( sizeView.getText().toString() );
        BigDecimal subtracted = number.subtract( STEP_FACTOR );
        if ( subtracted.compareTo( BigDecimal.ZERO ) == -1)
            subtracted = zero();
        sizeView.setText( subtracted.toString() );
        calculate( view );
    }

    private BigDecimal bigDecimalFactory( String value )
    {
        return new BigDecimal( value ).setScale( 2, BigDecimal.ROUND_HALF_EVEN );
    }

    private BigDecimal bigDecimalFactory( double aDouble )
    {
        return bigDecimalFactory( String.valueOf( aDouble ) );
    }

    private BigDecimal zero()
    {
        return bigDecimalFactory( BigDecimal.ZERO );
    }

    private BigDecimal bigDecimalFactory( BigDecimal value )
    {
        return bigDecimalFactory( value.toString() );
    }
}
