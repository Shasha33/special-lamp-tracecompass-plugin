//package com.test.ui.sample;
//
//import  org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.controlflow.*;
//import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderManager;
//import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
//
//import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
//import org.eclipse.core.runtime.jobs.ISchedulingRule;
//import org.eclipse.core.runtime.jobs.Job;
//import org.eclipse.jdt.annotation.NonNull;
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.action.IMenuManager;
//import org.eclipse.jface.action.IToolBarManager;
//import org.eclipse.jface.action.MenuManager;
//import org.eclipse.jface.action.Separator;
//import org.eclipse.jface.dialogs.IDialogSettings;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.StructuredSelection;
//import org.eclipse.jface.viewers.ViewerFilter;
//import org.eclipse.jface.window.Window;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.tracecompass.analysis.os.linux.core.kernel.KernelTidAspect;
//import org.eclipse.tracecompass.internal.analysis.os.linux.core.threadstatus.ThreadEntryModel;
//import org.eclipse.tracecompass.internal.analysis.os.linux.core.threadstatus.ThreadStatusDataProvider;
//import org.eclipse.tracecompass.internal.analysis.os.linux.ui.Activator;
//import org.eclipse.tracecompass.internal.analysis.os.linux.ui.Messages;
//import org.eclipse.tracecompass.internal.analysis.os.linux.ui.actions.FollowThreadAction;
//import org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.controlflow.filters.ActiveThreadsFilter;
//import org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.controlflow.filters.DynamicFilterDialog;
//import org.eclipse.tracecompass.internal.tmf.core.model.filters.FetchParametersUtils;
//import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderManager;
//import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
//import org.eclipse.tracecompass.tmf.core.model.filters.SelectionTimeQueryFilter;
//import org.eclipse.tracecompass.tmf.core.model.filters.TimeQueryFilter;
//import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphRowModel;
//import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphState;
//import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphModel;
//import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
//import org.eclipse.tracecompass.tmf.core.response.ITmfResponse;
//import org.eclipse.tracecompass.tmf.core.response.TmfModelResponse;
//import org.eclipse.tracecompass.tmf.core.signal.TmfSelectionRangeUpdatedSignal;
//import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
//import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
//import org.eclipse.tracecompass.tmf.core.signal.TmfTraceClosedSignal;
//import org.eclipse.tracecompass.tmf.core.signal.TmfTraceSelectedSignal;
//import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimestamp;
//import org.eclipse.tracecompass.tmf.core.trace.ITmfContext;
//import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
//import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;
//import org.eclipse.tracecompass.tmf.ui.views.FormatTimeUtils;
//import org.eclipse.tracecompass.tmf.ui.views.FormatTimeUtils.Resolution;
//import org.eclipse.tracecompass.tmf.ui.views.FormatTimeUtils.TimeFormat;
//import org.eclipse.tracecompass.tmf.ui.views.timegraph.BaseDataProviderTimeGraphView;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ILinkEvent;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.TimeGraphControl;
//import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils;
//import org.eclipse.ui.IWorkbenchActionConstants;
//import org.eclipse.ui.PlatformUI;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Iterables;
//import com.google.common.collect.Maps;
//
//
//public class JVMControlFlow extends ControlFlowView {
//	@Override
//    protected void buildEntryList(final ITmfTrace trace, final ITmfTrace parentTrace, final IProgressMonitor monitor) {
//        ThreadStatusDataProvider dataProvider = DataProviderManager.getInstance()
//                .getDataProvider(trace, ThreadStatusDataProvider.ID, ThreadStatusDataProvider.class);
//        if (dataProvider == null) {
//            return;
//        }
//
//        boolean complete = false;
//        TraceEntry traceEntry = null;
//        while (!complete && !monitor.isCanceled()) {
//            TmfModelResponse<TmfTreeModel<@NonNull ThreadEntryModel>> response = dataProvider.fetchTree(FetchParametersUtils.timeQueryToMap(new TimeQueryFilter(0, Long.MAX_VALUE, 2)), monitor);
//            if (response.getStatus() == ITmfResponse.Status.FAILED) {
//                Activator.getDefault().logError("Thread Status Data Provider failed: " + response.getStatusMessage()); //$NON-NLS-1$
//                return;
//            } else if (response.getStatus() == ITmfResponse.Status.CANCELLED) {
//                return;
//            }
//            complete = response.getStatus() == ITmfResponse.Status.COMPLETED;
//
//            TmfTreeModel<@NonNull ThreadEntryModel> model = response.getModel();
//            if (model != null && !model.getEntries().isEmpty()) {
//                synchronized (fEntries) {
//                    for (ThreadEntryModel entry : model.getEntries()) {
//                        if (entry.getThreadId() != Integer.MIN_VALUE) {
//                            if (traceEntry == null) {
//                                break;
//                            }
//                            TimeGraphEntry e = fEntries.get(traceEntry.getProvider(), entry.getId());
//                            if (e != null) {
//                                e.updateModel(entry);
//                            } else {
//                                fEntries.put(traceEntry.getProvider(), entry.getId(), new ControlFlowEntry(entry));
//                            }
//                        } else {
//                            setStartTime(Long.min(getStartTime(), entry.getStartTime()));
//                            setEndTime(Long.max(getEndTime(), entry.getEndTime() + 1));
//
//                            if (traceEntry != null) {
//                                traceEntry.updateModel(entry);
//                            } else {
//                                traceEntry = new TraceEntry(entry, trace, dataProvider);
//                                addToEntryList(parentTrace, Collections.singletonList(traceEntry));
//                            }
//                        }
//                    }
//                }
//
//                Objects.requireNonNull(traceEntry, "ControlFlow tree model should have a trace entry with PID=Integer.MIN_VALUE"); //$NON-NLS-1$
//                Collection<TimeGraphEntry> controlFlowEntries = fEntries.row(getProvider(traceEntry)).values();
//                synchronized (fFlatTraces) {
//                    if (fFlatTraces.contains(parentTrace)) {
//                        addEntriesToFlatTree(controlFlowEntries, traceEntry);
//                    } else {
//                        addEntriesToHierarchicalTree(controlFlowEntries, traceEntry);
//                    }
//                }
//                Iterable<TimeGraphEntry> entries = Iterables.filter(controlFlowEntries, TimeGraphEntry.class);
//                final long resolution = Long.max(1, (traceEntry.getEndTime() - traceEntry.getStartTime()) / getDisplayWidth());
//                zoomEntries(entries, traceEntry.getStartTime(), traceEntry.getEndTime(), resolution, monitor);
//            }
//            if (parentTrace.equals(getTrace())) {
//                refresh();
//            }
//
//            if (!complete) {
//                try {
//                    Thread.sleep(BUILD_UPDATE_TIMEOUT);
//                } catch (InterruptedException e) {
//                    Activator.getDefault().logError("Failed to wait for analysis to finish", e); //$NON-NLS-1$
//                }
//            }
//        }
//    }
//}	
