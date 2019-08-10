package com.willwinder.ugs.nbm.visualizer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.willwinder.ugs.nbm.visualizer.actions.MoveCameraAction;
import com.willwinder.ugs.nbm.visualizer.shared.GcodeRenderer;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.uielements.helpers.MachineStatusFontManager;
import com.willwinder.universalgcodesender.uielements.helpers.ThemeColors;
import net.miginfocom.swing.MigLayout;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static com.willwinder.universalgcodesender.utils.GUIHelpers.displayErrorDialog;

public class VisualizerPanel extends JPanel implements UGSEventListener {

    private final BackendAPI backend;
    private final GcodeRenderer gcodeRenderer = Lookup.getDefault().lookup(GcodeRenderer.class);
    /**
     * The minimum width and height of the jog buttons.
     */
    private static final int MINIMUM_BUTTON_SIZE = 16;

    private static final float FONT_SIZE_LABEL_SMALL = 8;
    private static final float FONT_SIZE_LABEL_MEDIUM = 10;
    private static final float FONT_SIZE_LABEL_LARGE = 14;

    /**
     * How long should the jog button be pressed before continuous
     * jog is activated. Given in milliseconds
     */
    private static final int LONG_PRESS_DELAY = 500;

    /**
     * A list of listeners
     */
    private final Set<VisualizerPanelListener> listeners = new HashSet<>();

    /**
     * A map with all buttons that allows bi-directional lookups with key->value and value->key
     */
    private final BiMap<VisualizerPanelButtonEnumR, JButton> buttons = HashBiMap.create();

    JButton buttonReset = createImageButton("icons/XYZ32.png", Localization.getString("platform.visualizer.popup.presets.reset"), SwingConstants.BOTTOM, SwingConstants.CENTER);
    JButton buttonLeft = createImageButton("icons/Y32.png", Localization.getString("platform.visualizer.popup.presets.left"), SwingConstants.BOTTOM, SwingConstants.CENTER);
    JButton buttonFront =createImageButton("icons/X32.png",Localization.getString("platform.visualizer.popup.presets.front") , SwingConstants.BOTTOM, SwingConstants.CENTER);
    JButton buttonTop =createImageButton("icons/Z32.png", Localization.getString("platform.visualizer.popup.presets.top"), SwingConstants.BOTTOM, SwingConstants.CENTER);
    JButton buttonLogo =createImageButton("icons/LogoDeta150.png", "", SwingConstants.BOTTOM, SwingConstants.CENTER);

    @Deprecated
    public VisualizerPanel() {
        this(null);
    }
    public VisualizerPanel(BackendAPI backend) {
        this.backend = backend;
        if (backend != null) {
            backend.addUGSEventListener(this);
        }
        initComponents();
        initPanels();
       // initListeners();
    }

    @Override
    public void UGSEvent(UGSEvent evt) {
        if (evt.isStateChangeEvent()) {
            updateControls();
        }
    }
    private static boolean isDarkLaF() {
        return UIManager.getBoolean("nb.dark.theme"); //NOI18N
    }
    private void updateControls() {
        this.updateWorkflowControls(backend.isIdle());
    }


    private void initComponents() {
        String fontPath = "/resources/";
        // https://www.fontsquirrel.com
        String fontName = "OpenSans-Regular.ttf";
        InputStream is = getClass().getResourceAsStream(fontPath + fontName);
        Font font = MachineStatusFontManager.createFont(is, fontName).deriveFont(Font.PLAIN, FONT_SIZE_LABEL_LARGE);

        // Create our buttons

        buttonReset.addActionListener(this::buttonReset);
        buttonLeft.addActionListener(this::buttonLeft);
        buttonFront.addActionListener(this::buttonFront);
        buttonTop.addActionListener(this::buttonTop);

        buttons.put(VisualizerPanelButtonEnumR.BUTTON_RESET,buttonReset);
        buttons.put(VisualizerPanelButtonEnumR.BUTTON_LEFT,buttonLeft );
        buttons.put(VisualizerPanelButtonEnumR.BUTTON_FRONT,buttonFront );
        buttons.put(VisualizerPanelButtonEnumR.BUTTON_TOP,buttonTop );
        buttons.put(VisualizerPanelButtonEnumR.BUTTON_LOGO,buttonLogo );

        if (isDarkLaF()) {
            buttons.values().forEach(button -> setForeground(ThemeColors.LIGHT_BLUE));
        }
    }
    private void updateWorkflowControls(boolean enabled) {
        this.buttonReset.setEnabled(enabled);
        this.buttonFront.setEnabled(enabled);
        this.buttonLeft.setEnabled(enabled);
        this.buttonTop.setEnabled(enabled);

    }


    private void buttonReset(java.awt.event.ActionEvent evt) {
        try {
            MoveCameraAction action = null;
            action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_ISOMETRIC);
            action.actionPerformed(evt);
        } catch (Exception ex) {
            displayErrorDialog(ex.getMessage());
        }
    }

    private void buttonLeft(java.awt.event.ActionEvent evt) {
        try {
            MoveCameraAction action = null;
            action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_LEFT);
            action.actionPerformed(evt);
        } catch (Exception ex) {
            displayErrorDialog(ex.getMessage());
        }
    }
    private void buttonFront(java.awt.event.ActionEvent evt) {
        try {
            MoveCameraAction action = null;
            action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_FRONT);
            action.actionPerformed(evt);
        } catch (Exception ex) {
            displayErrorDialog(ex.getMessage());
        }
    }
    private void buttonTop(java.awt.event.ActionEvent evt) {
        try {
            MoveCameraAction action = null;
            action = new MoveCameraAction(gcodeRenderer, MoveCameraAction.ROTATION_TOP);
            action.actionPerformed(evt);
        } catch (Exception ex) {
            displayErrorDialog(ex.getMessage());
        }
    }



    private void initPanels() {
        setLayout(new MigLayout("fill, inset 5, gap 7"));
        add(createButtonsPanel(), "grow, wrap");

    }
    private JPanel createButtonsPanel() {
        JPanel visualizerButtonsPanel = new JPanel();
        visualizerButtonsPanel.setLayout(new MigLayout("fill"   ));

        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnumR.BUTTON_RESET), "grow");
        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnumR.BUTTON_LEFT), "grow");
        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnumR.BUTTON_FRONT), "grow");
        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnumR.BUTTON_TOP), "grow");

       /* JPanel space = new JPanel();
        space.setOpaque(false);
        visualizerButtonsPanel.add(space, "grow");
*/
        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnumR.BUTTON_LOGO), "grow");
        return visualizerButtonsPanel;
    }


    protected void onMouseClicked(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e)) {
            return; // ignore RMB
        }
        VisualizerPanelButtonEnumR buttonEnum = getButtonEnumFromMouseEvent(e);
        listeners.forEach(a -> a.onButtonClicked(buttonEnum));
    }

    ///*** zna stara definicia
    /*private final JButton xReset = new JButton(Localization.getString("platform.visualizer.popup.presets.reset"));
    private final JButton xTop = new JButton(Localization.getString("platform.visualizer.popup.presets.top"));
    private final JButton yLeft = new JButton(Localization.getString("platform.visualizer.popup.presets.left"));
    private final JButton yFront = new JButton(Localization.getString("platform.visualizer.popup.presets.front"));

    public void xReset() { test(); }
    public void xTop() {
        test();
    }
    public void yLeft() {
        test();
    }
    public void yFront() {
        test();
    }


    public void test() { ;}


    xReset.addActionListener(e-> xReset());
    xTop.addActionListener(this -> xTop());
    yLeft.addActionListener(this -> yLeft());
    yFront.addActionListener(this -> yFront());*/

   /* private void initListeners() {

        // Creates a window size listener
        SteppedSizeManager sizer = new SteppedSizeManager(this,
                new Dimension(230, 0), // Scaling fonts to extra small
                new Dimension(250, 0)  // Scaling fonts to small
        );
        sizer.addListener(this);


        LongPressMouseListener longPressMouseListener = new LongPressMouseListener(LONG_PRESS_DELAY) {
            @Override
            protected void onMouseClicked(MouseEvent e) {
                if(!SwingUtilities.isLeftMouseButton(e)) {
                    return; // ignore RMB
                }
                VisualizerPanelButtonEnumR buttonEnum = getButtonEnumFromMouseEvent(e);
                listeners.forEach(a -> a.onButtonClicked(buttonEnum));
            }

            @Override
            protected void onMouseLongClicked(MouseEvent e) {

            }

            @Override
            protected void onMousePressed(MouseEvent e) {

            }

            @Override
            protected void onMouseRelease(MouseEvent e) {

            }

            @Override
            protected void onMouseLongPressed(MouseEvent e) {
                if(!SwingUtilities.isLeftMouseButton(e)) {
                    return; // ignore RMB
                }
                JogPanelButtonEnum buttonEnum = getButtonEnumFromMouseEvent(e);
                listeners.forEach(a -> a.onButtonLongPressed(buttonEnum));
            }

            @Override
            protected void onMouseLongRelease(MouseEvent e) {
                if(!SwingUtilities.isLeftMouseButton(e)) {
                    return; // ignore RMB
                }
                JogPanelButtonEnum buttonEnum = getButtonEnumFromMouseEvent(e);
                listeners.forEach(a -> a.onButtonLongReleased(buttonEnum));
            }
        };

        buttons.values().forEach(button -> button.addMouseListener(longPressMouseListener));


    }*/

    /**
     * Finds the button enum based on the mouse event source
     *
     * @param mouseEvent the event that we want to extract the button enum from
     * @return the enum for the button
     */
    private VisualizerPanelButtonEnumR getButtonEnumFromMouseEvent(MouseEvent mouseEvent) {
        JButton releasedButton = (JButton) mouseEvent.getSource();
        return buttons.inverse().get(releasedButton);
    }

    /**
     * Returns the button from the button map using a button enum
     *
     * @param buttonEnum the button enum
     * @return the button
     */
    private JButton getButtonFromEnum(VisualizerPanelButtonEnumR buttonEnum) {
        return buttons.get(buttonEnum);
    }


    /**
     * Creates a image button with a text.
     *
     * @param baseUri            the base uri of the image
     * @param text               the text to be shown togheter with the icon
     * @param verticalAligment   Sets the vertical position of the text relative to the icon
     *                           and can have one of the following values
     *                           <ul>
     *                           <li>{@code SwingConstants.CENTER} (the default)
     *                           <li>{@code SwingConstants.TOP}
     *                           <li>{@code SwingConstants.BOTTOM}
     *                           </ul>
     * @param horisontalAligment Sets the horizontal position of the text relative to the
     *                           icon and can have one of the following values:
     *                           <ul>
     *                           <li>{@code SwingConstants.RIGHT}
     *                           <li>{@code SwingConstants.LEFT}
     *                           <li>{@code SwingConstants.CENTER}
     *                           <li>{@code SwingConstants.LEADING}
     *                           <li>{@code SwingConstants.TRAILING} (the default)
     *                           </ul>
     * @return the button
     */
    private JButton createImageButton(String baseUri, String text, int verticalAligment, int horisontalAligment) {
        JButton button = createImageButton(baseUri);
        button.setText(text);
        button.setVerticalTextPosition(verticalAligment);
        button.setHorizontalTextPosition(horisontalAligment);
        return button;
    }

    /**
     * Creates a image button.
     *
     * @param baseUri the base uri of the image
     * @return the button
     */
    private JButton createImageButton(String baseUri) {
        ImageIcon imageIcon = ImageUtilities.loadImageIcon(baseUri, false);
        JButton button = new JButton(imageIcon);
        button.setMinimumSize(new Dimension(MINIMUM_BUTTON_SIZE, MINIMUM_BUTTON_SIZE));
        button.setFocusable(false);
        return button;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        buttons.values().forEach(button -> button.setEnabled(enabled));
    }

    /*
    @Override
    public void onSizeChange(int size) {
        switch (size) {
            case 0:
                setFontSizeExtraSmall();
                break;
            case 1:
                setFontSizeSmall();
                break;
            default:
                setFontSizeNormal();
                break;
        }
    }
*/
    private void setFontSizeExtraSmall() {
       /* JButton unitToggleButton = getButtonFromEnum(JogPanelButtonEnum.BUTTON_TOGGLE_UNIT);
        Font font = unitToggleButton.getFont().deriveFont(FONT_SIZE_LABEL_MEDIUM);
        unitToggleButton.setFont(font);*/
    }

    private void setFontSizeSmall() {
     /*   JButton unitToggleButton = getButtonFromEnum(JogPanelButtonEnum.BUTTON_TOGGLE_UNIT);
        Font font = unitToggleButton.getFont().deriveFont(FONT_SIZE_LABEL_MEDIUM);
        unitToggleButton.setFont(font);*/
    }

    private void setFontSizeNormal() {
       /* JButton unitToggleButton = getButtonFromEnum(JogPanelButtonEnum.BUTTON_TOGGLE_UNIT);
        Font font = unitToggleButton.getFont().deriveFont(FONT_SIZE_LABEL_MEDIUM);
        unitToggleButton.setFont(font);*/
    }

    public void addListener(VisualizerPanelListener listener) {
        this.listeners.add(listener);
    }
}
