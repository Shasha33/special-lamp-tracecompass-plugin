package com.test.ui.sample;

import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.DefaultUstEventLayout;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst28EventLayout;

@SuppressWarnings("restriction")
public class JVMLttngEventLayout extends LttngUst20EventLayout {
	private static final JVMLttngEventLayout INSTANCE = new JVMLttngEventLayout();
	
	    @Override
    public String eventCygProfileFuncEntry() {
        return "hotspot:method__entry";
    }

    @Override
    public String eventCygProfileFastFuncEntry() {
        return "hotspot:method__entry";
    }

    @Override
    public String eventCygProfileFuncExit() {
        return "hotspot:method__return";
    }

    @Override
    public String eventCygProfileFastFuncExit() {
        return "hotspot:method__return";
    }
}
