package com.test.ui.sample;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.ISeries.SeriesType;
import org.eclipse.swt.SWT;
import org.eclipse.tracecompass.tmf.core.trace.*;
import org.eclipse.tracecompass.tmf.core.signal.*;
import org.eclipse.tracecompass.tmf.core.request.*;
import org.eclipse.tracecompass.tmf.core.event.*;
import org.eclipse.tracecompass.tmf.core.timestamp.*;
import org.eclipse.swt.widgets.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

import org.swtchart.*;

public class View extends ViewPart {

	private static final String SERIES_NAME = "Series";
	private static final String FIELD = "thread_id";
    private static final String Y_AXIS_TITLE = "Signal";
    private static final String X_AXIS_TITLE = "Time";
    private Chart chart;
    private ITmfTrace currentTrace;
    private static Logger log = Logger.getLogger(View.class.getName());
    private double maxY = -Double.MAX_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = -Double.MAX_VALUE;
    private double minX = Double.MAX_VALUE;

    public View() {
        super();
    }

    public class TmfChartTimeStampFormat extends SimpleDateFormat {
        private static final long serialVersionUID = 1L;
        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            long time = date.getTime();
            toAppendTo.append(TmfTimestampFormat.getDefaulTimeFormat().format(time));
            return toAppendTo;
        }
    }
    
    @TmfSignalHandler
    public void timestampFormatUpdated(TmfTimestampFormatUpdateSignal signal) {
        // Called when the time stamp preference is changed
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfChartTimeStampFormat());
        chart.redraw();
    }
    
    @Override
    public void createPartControl(Composite parent) {
    	System.out.println("AAAAAAAAa");
        chart = new Chart(parent, SWT.BORDER);
        chart.getTitle().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTitle().setText(X_AXIS_TITLE);
        chart.getAxisSet().getYAxis(0).getTitle().setText(Y_AXIS_TITLE);
        chart.getSeriesSet().createSeries(SeriesType.LINE, SERIES_NAME);
        chart.getLegend().setVisible(false);
        
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfChartTimeStampFormat());
        ITmfTrace trace = TmfTraceManager.getInstance().getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
    }

    @Override
    public void setFocus() {
        chart.setFocus();
    }
    
    @TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        if (currentTrace == signal.getTrace()) {
//        	log.info("trace found");
            return;
        }
        currentTrace = signal.getTrace();

        // Create the request to get data from the trace

        ArrayList<Double> xValues = new ArrayList<Double>();
        ArrayList<Long> yValues = new ArrayList<Long>();
        TmfEventRequest req = new TmfEventRequest(TmfEvent.class,
                TmfTimeRange.ETERNITY, 0, ITmfEventRequest.ALL_DATA,
                ITmfEventRequest.ExecutionType.BACKGROUND) {

            @Override
            public void handleData(ITmfEvent data) {
//            	System.out.println("start");
//            	log.info("start handling");
                // Called for each event
                super.handleData(data);
                ITmfEventField field = data.getContent().getField(FIELD);
                for (String s : data.getContent().getFieldNames()) {
                	log.info(s);
                }
                if (field != null) {
                    Long yValue = (Long) field.getValue();
                    System.out.println(yValue);
                	double xValue = (double) data.getTimestamp().getValue();
                	System.out.println(yValue);
                	maxY = Math.max(yValue, maxY);
                	maxX = Math.max(xValue, maxX);
                	minY = Math.min(minY, yValue);
                	minX = Math.max(minX, xValue);
                	
                	yValues.add(yValue);
                    xValues.add(xValue);
                } else {                	
                	System.out.println("field is null");
                }
            }

            @Override
            public void handleSuccess() {
                // Request successful, not more data available
                super.handleSuccess();
                double[] x = xValues.stream().mapToDouble(i -> i).toArray();
                double[] y = yValues.stream().mapToDouble(i -> i).toArray();

                // This part needs to run on the UI thread since it updates the chart SWT control
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        chart.getSeriesSet().getSeries()[0].setXSeries(x);
                        chart.getSeriesSet().getSeries()[0].setYSeries(y);

                     // Set the new range
                        if (!xValues.isEmpty() && !yValues.isEmpty()) {
                            chart.getAxisSet().getXAxis(0).setRange(new Range(0, x[x.length - 1]));
                            chart.getAxisSet().getYAxis(0).setRange(new Range(minY, maxY));
                        } else {
                            chart.getAxisSet().getXAxis(0).setRange(new Range(-1, 1));
                            chart.getAxisSet().getYAxis(0).setRange(new Range(-1, 1));
                        }
                        chart.getAxisSet().adjustRange();
                        
                        chart.redraw();
                    }

                });
            }

            @Override
            public void handleFailure(){
                // Request failed, not more data available
                super.handleFailure();
            }
        };
        ITmfTrace trace = signal.getTrace();
        trace.sendRequest(req);
    }

}
