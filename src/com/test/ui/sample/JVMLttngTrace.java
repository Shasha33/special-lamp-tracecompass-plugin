package com.test.ui.sample;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstTrace;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfTraceException;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.*;
import org.eclipse.tracecompass.tmf.core.event.aspect.ITmfEventAspect;
import org.eclipse.tracecompass.tmf.ctf.core.trace.CtfTmfTrace;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.*;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.*;

import org.eclipse.tracecompass.tmf.core.trace.TmfEventTypeCollectionHelper;
import org.eclipse.tracecompass.tmf.core.trace.TraceValidationStatus;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class JVMLttngTrace extends LttngUstTrace {
	private Collection<ITmfEventAspect<?>> fUstTraceAspects = ImmutableSet.copyOf(LTTNG_UST_ASPECTS);
	private JVMLttngEventLayout fLayout = null;
	 
	private static final Collection<ITmfEventAspect<?>> LTTNG_UST_ASPECTS;

	static {
		ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
	    builder.addAll(CtfTmfTrace.CTF_ASPECTS);
	    LTTNG_UST_ASPECTS = builder.build();
	 }
	
	public ILttngUstEventLayout getEventLayout() {
		JVMLttngEventLayout layout = fLayout;
        if (layout == null) {
            throw new IllegalStateException("Cannot get the layout of a non-initialized trace!");
        }
        return layout;
    }
	
	@Override
    public IStatus validate(final IProject project, final String path) {
		return new TraceValidationStatus(100, Activator.PLUGIN_ID);
	}
	
	@Override
	public Iterable<ITmfEventAspect<?>> getEventAspects() {
		return fUstTraceAspects;
	}
	
	@Override
    public void initTrace(IResource resource, String path,
            Class<? extends ITmfEvent> eventType) throws TmfTraceException {
        System.out.println("init trace");
		super.initTrace(resource, path, eventType);

        /* Determine the event layout to use from the tracer's version */
        JVMLttngEventLayout layout = (JVMLttngEventLayout) JVMLttngEventLayout.getInstance();
        fLayout = layout;

        ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
        builder.addAll(LTTNG_UST_ASPECTS);
        if (checkFieldPresent(layout.contextVtid())) {
        	System.out.println("vtid found");
            builder.add(new ContextVtidAspect(layout));
        }
        if (checkFieldPresent(layout.contextVpid())) {
        	System.out.println("vpid found");
        	builder.add(new ContextVpidAspect(layout));
        }
        builder.addAll(createCounterAspects(this));
        fUstTraceAspects = builder.build();
    }
	
	private boolean checkFieldPresent(String field) {
        final Multimap<String, String> traceEvents = TmfEventTypeCollectionHelper.getEventFieldNames((getContainedEventTypes()));
        
        return traceEvents.containsValue(field);
    }
	
}
