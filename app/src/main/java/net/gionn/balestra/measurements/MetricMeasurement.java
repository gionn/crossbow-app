package net.gionn.balestra.measurements;

import static net.gionn.balestra.ParseUtils.parseValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;

public class MetricMeasurement extends BaseMeasurement {
    private BigDecimal maxValue;

    public MetricMeasurement(String configurationJson) {
        super(configurationJson);
    }

    @Override
    public BigDecimal getStepFactor() {
        return new BigDecimal("0.5");
    }

    @Override
    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public BigDecimal getMinValue() {
        return BigDecimal.ZERO;
    }

    @Override
    protected void parseJson(String resource) {
        try {
            JSONObject jsonObject = new JSONObject(resource);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String nextKey = keys.next();
                JSONArray dataArray = jsonObject.getJSONArray(nextKey);
                BigDecimal key = parseValue(dataArray.getDouble(0));
                BigDecimal value = parseValue(dataArray.getDouble(1));
                this.data.put(key, value);
                this.setMaxValue(key);
                // spline population
                keyList.add(key.doubleValue());
                valueList.add(value.doubleValue());
            }
            this.setupSpline();
        } catch (JSONException e) {
            throw new IllegalArgumentException("Cannot parse json structure", e);
        }
    }
}
