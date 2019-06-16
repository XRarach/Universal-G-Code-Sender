package com.willwinder.ugs.nbm.visualizer;
/**
 * A listener for button events in the {@link VisualizerPanel}.
 *
 * @author Rastislav Piovarci
 */
public interface VisualizerPanelListener {

    /**
     * Is called when the button was single clicked
     *
     * @param button the enum for the button
     */
    void onButtonClicked(VisualizerPanelButtonEnumR button);


}
