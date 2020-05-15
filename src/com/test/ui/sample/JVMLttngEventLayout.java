package com.test.ui.sample;

import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.DefaultUstEventLayout;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst28EventLayout;

@SuppressWarnings("restriction")
public class JVMLttngEventLayout extends LttngUst28EventLayout {
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
    
    @Override
    public String contextVpid() {
    	return "context._vpid";
    }
    
    @Override
    public String contextVtid() {
    	return "context._vtid";
    }
    
    @Override
    public String contextProcname() {
    	return "context._procname";
    }
    
    @Override
    public String contextIp() {
    	return "context._ip";
    }
    
    
}
