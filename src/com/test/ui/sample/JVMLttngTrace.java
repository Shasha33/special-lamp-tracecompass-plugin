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
	private ILttngUstEventLayout fLayout = null;
	 
	private static final Collection<ITmfEventAspect<?>> LTTNG_UST_ASPECTS;

	static {
		ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
	    builder.addAll(CtfTmfTrace.CTF_ASPECTS);
//	    builder.add(UstDebugInfoBinaryAspect.INSTANCE);
//	    builder.add(UstDebugInfoFunctionAspect.INSTANCE);
//	    builder.add(UstDebugInfoSourceAspect.INSTANCE);
	    LTTNG_UST_ASPECTS = builder.build();
	 }
	
	public ILttngUstEventLayout getEventLayout() {
        ILttngUstEventLayout layout = fLayout;
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
    public void initTrace(IResource resource, String path,
            Class<? extends ITmfEvent> eventType) throws TmfTraceException {
        super.initTrace(resource, path, eventType);

        /* Determine the event layout to use from the tracer's version */
        ILttngUstEventLayout layout = JVMLttngEventLayout.getInstance();
        fLayout = layout;

        ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
        builder.addAll(LTTNG_UST_ASPECTS);
        if (checkFieldPresent(layout.contextVtid())) {
        	System.out.println("vtid found");
            builder.add(new ContextVtidAspect(layout));
        }
        if (checkFieldPresent(layout.contextVpid())) {
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
