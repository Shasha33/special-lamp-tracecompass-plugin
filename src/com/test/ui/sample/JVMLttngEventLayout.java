package com.test.ui.sample;

import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.DefaultUstEventLayout;

@SuppressWarnings("restriction")
public class JVMLttngEventLayout extends DefaultUstEventLayout {
	private static final JVMLttngEventLayout INSTANCE = new JVMLttngEventLayout();
	
	@Override
    public String eventCygProfileFuncEntry() {
        return "method__entry";
    }

    @Override
    public String eventCygProfileFastFuncEntry() {
        return eventCygProfileFuncEntry();
    }

    @Override
    public String eventCygProfileFuncExit() {
        return "method__return";
    }

    @Override
    public String eventCygProfileFastFuncExit() {
        return eventCygProfileFuncExit();
    }
    
    public static JVMLttngEventLayout getInstance() {
        return INSTANCE;
    }
}
