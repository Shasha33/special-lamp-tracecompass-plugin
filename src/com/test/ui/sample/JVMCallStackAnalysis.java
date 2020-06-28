package com.test.ui.sample;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.profiling.core.callstack.CallStackAnalysis;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstTrace;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.ILttngUstEventLayout;
import org.eclipse.tracecompass.tmf.core.analysis.requirements.TmfAbstractAnalysisRequirement;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfAnalysisException;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableSet;

import org.eclipse.tracecompass.internal.lttng2.ust.core.callstack.*;

public class JVMCallStackAnalysis extends LttngUstCallStackAnalysis {
	public static final String ID = "com.test.ui.sample.callstack"; //$NON-NLS-1$

    private @Nullable Set<@NonNull TmfAbstractAnalysisRequirement> fAnalysisRequirements = null;

    @Override
    protected ITmfStateProvider createStateProvider() {
        return new JVMCallStackProvider(getTrace());
    }

    @Override
    public @NonNull Iterable<@NonNull TmfAbstractAnalysisRequirement> getAnalysisRequirements() {

        Set<@NonNull TmfAbstractAnalysisRequirement> requirements = fAnalysisRequirements;
        if (requirements == null) {
        	JVMLttngTrace trace = (JVMLttngTrace) getTrace();
        	JVMLttngEventLayout layout = (JVMLttngEventLayout) JVMLttngEventLayout.getInstance();
            if (trace != null) {
                layout = (JVMLttngEventLayout) trace.getEventLayout();
            }
            requirements = ImmutableSet.of(new LttngUstCallStackAnalysisRequirement(layout));
            fAnalysisRequirements = requirements;
        }
        return requirements;
    }
}
