/*
    Copyright 2015-2018 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbm.visualizer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import com.willwinder.ugs.nbm.visualizer.options.VisualizerOptionsPanel;
import com.willwinder.ugs.nbm.visualizer.shared.GcodeRenderer;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.ugs.nbp.lib.services.TopComponentLocalizer;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.uielements.helpers.MachineStatusFontManager;
import net.miginfocom.swing.MigLayout;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.modules.OnStart;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.prefs.Preferences;

import static com.willwinder.ugs.nbp.lib.services.LocalizingService.lang;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

import javax.swing.*;


/**
 * Setup JOGL canvas, GcodeRenderer and RendererInputHandler.
 */
@TopComponent.Description(
        preferredID = "VisualizerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = Visualizer2TopComponent.VisualizerCategory, id = Visualizer2TopComponent.VisualizerActionId)
@ActionReference(path = Visualizer2TopComponent.VisualizerWindowPath)
@TopComponent.OpenActionRegistration(
        displayName = "<Not localized:VisualizerTopComponent>",
        preferredID = "VisualizerTopComponent"
)
public final class Visualizer2TopComponent extends TopComponent {
    private static final Logger logger = Logger.getLogger(Visualizer2TopComponent.class.getName());

    private static final int MINIMUM_BUTTON_SIZE = 52;

    private static final float FONT_SIZE_LABEL_SMALL = 8;
    private static final float FONT_SIZE_LABEL_MEDIUM = 10;
    private static final float FONT_SIZE_LABEL_LARGE = 14;

    public enum VisualizerPanelButtonEnum {

        BUTTON_UP
        //,
        // BUTTON_TOGGLE_UNIT
    }


    /**
     * A map with all buttons that allows bi-directional lookups with key->value and value->key
     */
    private final BiMap<VisualizerPanelButtonEnum, JButton> buttons = HashBiMap.create();

    private GLJPanel panel;
    private RendererInputHandler rih;
    private final BackendAPI backend;

    public final static String VisualizerTitle = Localization.getString("platform.window.visualizer", lang);
    public final static String VisualizerTooltip = Localization.getString("platform.window.visualizer.tooltip", lang);
    public final static String VisualizerWindowPath = LocalizingService.MENU_WINDOW;
    public final static String VisualizerActionId = "com.willwinder.ugs.nbm.visualizer.Visualizer2TopComponent";
    public final static String VisualizerCategory = LocalizingService.CATEGORY_WINDOW;

    @OnStart
    public static class Localizer extends TopComponentLocalizer {
      public Localizer() {
        super(VisualizerCategory, VisualizerActionId, VisualizerTitle);
      }
    }

    public Visualizer2TopComponent() {
        backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        
        setMinimumSize(new java.awt.Dimension(50, 50));
        setPreferredSize(new java.awt.Dimension(200, 200));
        setLayout(new java.awt.BorderLayout());
       // createComponents();
       // add(createVisualizerButtonsPanel(), "grow, wrap");

    }
    private JButton getButtonFromEnum(VisualizerPanelButtonEnum buttonEnum) {
        return buttons.get(buttonEnum);
    }

    private JPanel createVisualizerButtonsPanel() {
        JPanel visualizerButtonsPanel = new JPanel();
        visualizerButtonsPanel.setLayout(new MigLayout("fill, wrap 4, inset 0, gap 2", "[25%, center][25%, center][25%, center][25%, center]", "[33%, center][33%, center][33%, center]"));

        visualizerButtonsPanel.add(getButtonFromEnum(VisualizerPanelButtonEnum.BUTTON_UP), "UP");

        return visualizerButtonsPanel;
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

    private void createComponents() {
        String fontPath = "/resources/";
        // https://www.fontsquirrel.com
        String fontName = "OpenSans-Regular.ttf";
        InputStream is = getClass().getResourceAsStream(fontPath + fontName);
        Font font = MachineStatusFontManager.createFont(is, fontName).deriveFont(Font.PLAIN, FONT_SIZE_LABEL_LARGE);
        buttons.put(VisualizerPanelButtonEnum.BUTTON_UP, createImageButton("icons/fire.png", "Zapal", SwingConstants.CENTER, SwingConstants.RIGHT));
        // Create our buttons
    }
    /**
     * Fixes for commit: dd68a4ef9fd211642f284024bb651fa9bf0be64c
     * 1. No longer using custom "visualizer" mode.
     */
    private void cleanup() {
        Mode mode = WindowManager.getDefault().findMode(this);
        if (mode != null && StringUtils.equals("visualizer", mode.getName())) {
            this.close();
        }
    }

    @Override
    protected void componentOpened() {
        cleanup();

        setName(VisualizerTitle);
        setToolTipText(VisualizerTooltip);
        super.componentOpened();
        panel = makeWindow();
        add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();

        if (rih != null) {
            backend.removeControllerListener(rih);
            backend.removeUGSEventListener(rih);
        }

        logger.log(Level.INFO, "Component closed, panel = " + panel);
        if (panel == null) return;

        remove(panel);
        //dispose of panel and native resources
        panel.destroy();
        panel = null;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        if (panel != null) {
            panel.setSize(getSize());
            //need to update complete component tree
            invalidate();
            
            if (getTopLevelAncestor() != null) {
                getTopLevelAncestor().invalidate();
                getTopLevelAncestor().revalidate();
            }
        }
    }



    private GLJPanel makeWindow() {
        GLCapabilities glCaps = new GLCapabilities(null);
        final GLJPanel p = new GLJPanel(glCaps);

        GcodeRenderer renderer = Lookup.getDefault().lookup(GcodeRenderer.class);
        if (renderer == null) {
            throw new IllegalArgumentException("Failed to access GcodeRenderer.");
        }

        FPSAnimator animator = new FPSAnimator(p, 15);
        this.rih = new RendererInputHandler(renderer, animator,
                new VisualizerPopupMenu(backend, renderer),
                backend.getSettings());

        Preferences pref = NbPreferences.forModule(VisualizerOptionsPanel.class);
        pref.addPreferenceChangeListener(this.rih);

        File f = (backend.getProcessedGcodeFile() != null) ?
                backend.getProcessedGcodeFile() : backend.getGcodeFile();
        if (f != null) {
            this.rih.setGcodeFile(f.getAbsolutePath());
        }

        // Install listeners...
        backend.addControllerListener(this.rih);
        backend.addUGSEventListener(this.rih);

        // shutdown hook...
        //frame.addWindowListener(this.rih);

        // key listener...
        p.addKeyListener(this.rih);

        // mouse wheel...
        p.addMouseWheelListener(this.rih);

        // mouse motion...
        p.addMouseMotionListener(this.rih);

        // mouse...
        p.addMouseListener(this.rih);

        p.addGLEventListener(renderer);

        return p;
    }
}
