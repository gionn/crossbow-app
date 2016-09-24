package net.gionn.balestra;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static net.gionn.balestra.ParseUtils.parseValue;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    public static final String DB_JSON = "db.json";
    private List<Double> distanceList = new ArrayList<>();
    private List<Double> correctionList = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Measurement currentMeasurement;
    private MetricMeasurement metricMeasurement = new MetricMeasurement();
    private PointMeasurement pointMeasurement = new PointMeasurement();

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

        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        mLight = mSensorManager.getDefaultSensor( Sensor.TYPE_LIGHT );

        currentMeasurement = metricMeasurement;
        final Switch toggle = (Switch) findViewById(R.id.toggleButton);
        final TextView orizontalLabel = (TextView) findViewById(R.id.textView);
        final Button hMinus = (Button) findViewById(R.id.horizontalMinus);
        final Button hPlus = (Button) findViewById(R.id.horizontalPlus);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentMeasurement = pointMeasurement;
                    toggle.setText( "Punti" );
                    orizontalLabel.setText( "Punteggio" );
                    hMinus.setEnabled( false );
                    hPlus.setEnabled( false );
                } else {
                    currentMeasurement = metricMeasurement;
                    toggle.setText( "Centimetri" );
                    orizontalLabel.setText( "Distanza verticale (cm)" );
                    hMinus.setEnabled( true );
                    hPlus.setEnabled( true );

                }
                reset( buttonView );
            }
        });
    }

    private void initJsonData( String resource )
    {
        metricMeasurement.parseJson( resource );
        pointMeasurement.parseJson( resource );
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

    public void recalculateAndUpdateView( View view )
    {
        TextView verticalSizeText = (TextView) findViewById( R.id.verticalSize );
        TextView horizontalSizeText = (TextView) findViewById( R.id.horizontalSize );
        TextView verticalResult = (TextView) findViewById( R.id.verticalResult );
        TextView horizontalResult = (TextView) findViewById( R.id.horizontalResult );

        BigDecimal verticalInput = parseValue( verticalSizeText.getText().toString() );
        BigDecimal horizontalInput = parseValue( horizontalSizeText.getText().toString() );

        BigDecimal verticalOutput = getOrNear( verticalInput );
        BigDecimal horizontalOutput = getOrNear( horizontalInput );

        verticalResult.setText( verticalOutput.toString() );
        horizontalResult.setText( horizontalOutput.toString() );
    }

    private BigDecimal getOrNear( BigDecimal lookup )
    {
        if ( lookup.compareTo( BigDecimal.ZERO ) == -1 )
            return BigDecimal.ZERO;

        return currentMeasurement.getValue( lookup );
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

    public void reset( View view)
    {
        TextView sizeView = (TextView) findViewById( R.id.verticalSize );
        sizeView.setText( BigDecimal.ZERO.toString() );
        sizeView = (TextView) findViewById( R.id.horizontalSize );
        sizeView.setText( BigDecimal.ZERO.toString() );
        recalculateAndUpdateView( view );
    }

    private void add( View view, TextView sizeView )
    {
        BigDecimal number = parseValue( sizeView.getText().toString() );
        BigDecimal newValue = number.add( currentMeasurement.getStepFactor() );
        if ( newValue.compareTo( currentMeasurement.getMaxValue() ) == 1 )
            newValue = number;
        sizeView.setText( newValue.toString() );
        recalculateAndUpdateView( view );
    }

    private void subtract( View view, TextView sizeView )
    {
        BigDecimal number = parseValue( sizeView.getText().toString() );
        BigDecimal subtracted = number.subtract( currentMeasurement.getStepFactor() );
        if ( subtracted.compareTo( currentMeasurement.getMinValue() ) == -1)
            subtracted = BigDecimal.ZERO;
        sizeView.setText( subtracted.toString() );
        recalculateAndUpdateView( view );
    }



    @Override
    public void onSensorChanged( SensorEvent event )
    {
        if ( event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            TextView textView = (TextView) findViewById( R.id.currentLight );
            textView.setText( "" + (int) event.values[0] );
        }
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy )
    {

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
