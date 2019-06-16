package com.willwinder.ugs.nbm.visualizer;

import com.willwinder.ugs.nbm.visualizer.actions.JogToHereAction;
import com.willwinder.ugs.nbm.visualizer.actions.MoveCameraAction;
import com.willwinder.ugs.nbm.visualizer.shared.GcodeRenderer;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerListener;
import com.willwinder.universalgcodesender.listeners.ControllerStatus;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.Alarm;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.services.JogService;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.utils.SwingHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Visualizer control panel in NetBeans
 *
 * @author Rastislav Piovarci
 */
@TopComponent.Description(
        preferredID = "VisualizerPanelTopComponent"
)
@TopComponent.Registration(
        mode = "bottom_right",
        openAtStartup = true)
@ActionID(
        category = VisualizerPanelTopComponent.CATEGORY,
        id = VisualizerPanelTopComponent.ACTION_ID)
@ActionReference(
        path = VisualizerPanelTopComponent.WINOW_PATH)
@TopComponent.OpenActionRegistration(
        displayName = "Visualizer Panel Controller Rasti",
        preferredID = "VisualizerPanelTopComponentRasti"
)

public class VisualizerPanelTopComponent extends TopComponent implements UGSEventListener, ControllerListener, VisualizerPanelListener {

    //private final JogToHereAction jogToHereAction;
    //private final JMenuItem jogToHere = new JMenuItem();
    private final DecimalFormat decimalFormatter = new DecimalFormat("#.#####", Localization.dfs);

    private final GcodeRenderer gcodeRenderer = Lookup.getDefault().lookup(GcodeRenderer.class);
    /*if (gcodeRenderer == null)

    {
        throw new IllegalArgumentException("Failed to access GcodeRenderer.");
    }*/
    //jogToHereAction = new JogToHereAction(backend);

        //jogToHere.setText(String.format(Localization.getString("platform.visualizer.jogToHere"), 0, 0));

        //jogToHere.setAction(jogToHereAction);





    public static final String WINOW_PATH = LocalizingService.MENU_WINDOW_PLUGIN;
    public static final String CATEGORY = LocalizingService.CATEGORY_WINDOW;
    public static final String ACTION_ID = "com.willwinder.ugs.nbp.visualizer.VisualizerPanelTopComponent";

    /**
     * The inteval in milliseconds to send jog commands to the controller when
     * continuous jog is activated. This should be long enough so that the queue
     * isn't filled up.
     */
    private static final int LONG_PRESS_JOG_INTERVAL = 500;

    /**
     * The step size for continuous jog commands. These should be long enough
     * to keep the controller jogging before a new jog command is queued.
     */
    //private static final double LONG_PRESS_MM_STEP_SIZE = 5;
    //private static final double LONG_PRESS_INCH_STEP_SIZE = 0.2;

    private final BackendAPI backend;
    private final VisualizerPanel visualizerPanel;
    //private final JogService jogService;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
   //private ScheduledFuture<?> continuousJogSchedule;

    public VisualizerPanelTopComponent() {
        backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        //jogService = CentralLookup.getDefault().lookup(JogService.class);
        //UseSeparateStepSizeAction action = Lookup.getDefault().lookup(UseSeparateStepSizeAction.class);

        visualizerPanel = new VisualizerPanel();
        visualizerPanel.addListener(this);

        backend.addUGSEventListener(this);
        backend.addControllerListener(this);

        setLayout(new BorderLayout());
        setName(LocalizingService.VisualizerControlTitle);
        setToolTipText(LocalizingService.VisualizerControlTooltip);

        setPreferredSize(new Dimension(250, 270));

        add(visualizerPanel, BorderLayout.CENTER);

        /*if (action != null) {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(action);
            SwingHelpers.traverse(this, (comp) -> comp.setComponentPopupMenu(popupMenu));
        }*/
        visualizerPanel.setEnabled(true);
    }
    @Override
    protected void componentClosed() {
        super.componentClosed();
        backend.removeUGSEventListener(this);
        backend.removeControllerListener(this);
    }

    @Override
    public void UGSEvent(UGSEvent event) {
    }
    @Override
    public void controlStateChange(UGSEvent.ControlState state) {
    }

    @Override
    public void fileStreamComplete(String filename, boolean success) {

    }

    @Override
    public void receivedAlarm(Alarm alarm) {

    }

    @Override
    public void commandSkipped(GcodeCommand command) {

    }

    @Override
    public void commandSent(GcodeCommand command) {

    }

    @Override
    public void commandComplete(GcodeCommand command) {

    }

    @Override
    public void commandComment(String comment) {

    }

    @Override
    public void probeCoordinates(Position p) {

    }

    @Override
    public void statusStringListener(ControllerStatus status) {

    }

    @Override
    public void postProcessData(int numRows) {

    }

    @Override
    public void onButtonClicked(VisualizerPanelButtonEnumR button) {
        ActionEvent e=null;
        MoveCameraAction action = null;
        switch (button) {
            case BUTTON_RESET:
                action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_ISOMETRIC);
                action.actionPerformed(e);
                break;

            case BUTTON_TOP:
                action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_TOP);
                action.actionPerformed(e);
                break;
            case BUTTON_LEFT:
                action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_LEFT);
                action.actionPerformed(e);
                break;
            case BUTTON_FRONT:
                action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_FRONT);
                action.actionPerformed(e);
                break;

            default:
                break;
        }

    }
    /*public void setJogLocation(double x, double y) {
        String strX = decimalFormatter.format(x);
        String strY = decimalFormatter.format(y);

        jogToHereAction.setJogLocation(strX, strY);
        String jogToHereString = Localization.getString("platform.visualizer.popup.jogToHere");
        jogToHereString = jogToHereString.replaceAll("%f", "%s");
        jogToHere.setText(String.format(jogToHereString, strX, strY));
    }*/
}
