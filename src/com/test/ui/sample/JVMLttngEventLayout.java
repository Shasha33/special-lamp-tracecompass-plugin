package com.test.ui.sample;

import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.DefaultUstEventLayout;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst20EventLayout;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst28EventLayout;

@SuppressWarnings("restriction")
public class JVMLttngEventLayout extends LttngUst20EventLayout {
	private static JVMLttngEventLayout INSTANCE = new JVMLttngEventLayout();
	
	public static synchronized LttngUst20EventLayout getInstance() {
		JVMLttngEventLayout instance = INSTANCE;
        if (instance == null) {
            instance = new JVMLttngEventLayout();
            INSTANCE = instance;
        }
        return instance;
    }
	
	@Override
    public String eventCygProfileFuncEntry() {
        return "hotspot:method__entry";
    }

    @Override
    public String eventCygProfileFastFuncEntry() {
        return "hs_private:safepoint__begin";
    }

    @Override
    public String eventCygProfileFuncExit() {
        return "hotspot:method__return";
    }

    @Override
    public String eventCygProfileFastFuncExit() {
        return "hs_private:safepoint__end";
    }
}
