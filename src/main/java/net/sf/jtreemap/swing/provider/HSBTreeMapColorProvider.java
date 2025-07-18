/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting JTreeMap.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 69 2006-10-24 16:20:20Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.jtreemap.swing.provider;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.DefaultValue;
import net.sf.jtreemap.swing.JXTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.Value;

/**
 * An HSB color space color provider for JXTreeMap. Uses a specified function to map the values onto the HSB color
 * space. The default is a linear function, but in my experience one of the logarithmic ones works best for this color
 * space.
 * 
 * @author Andy Adamczak
 */
public class HSBTreeMapColorProvider extends ColorProvider {
    private static final int HSBVAL_SIZE = 3;

    /**
     * 
     */
    private static final long serialVersionUID = 5009655580804320847L;

    private static final Logger log = LoggerFactory.getLogger(HSBTreeMapColorProvider.class);

    /**
     * @author Andy Adamczak
     */
    public enum ColorDistributionTypes {
        Linear, Log, Exp, SquareRoot, CubicRoot
    }

    /**
     * @param treeMap
     * @param color
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final Color color) {
        this(treeMap, ColorDistributionTypes.Linear, color, color);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param color
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final ColorDistributionTypes colorDistribution,
        final Color color) {
        this(treeMap, colorDistribution, color, color);
    }

    /**
     * @param treeMap
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final Color positiveColor, final Color negativeColor) {
        this(treeMap, ColorDistributionTypes.Linear, positiveColor, negativeColor);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final ColorDistributionTypes colorDistribution,
        final Color positiveColor, final Color negativeColor) {
        super();
        jTreeMap = treeMap;
        this.colorDistribution = colorDistribution;
        adjustColor(positiveColor, negativeColor);
    }

    /**
     * @param treeMap
     * @param hue
     * @param saturation
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final float hue, final float saturation) {
        this(treeMap, ColorDistributionTypes.Linear, hue, saturation, hue, saturation);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param hue
     * @param saturation
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final ColorDistributionTypes colorDistribution,
        final float hue, final float saturation) {
        this(treeMap, colorDistribution, hue, saturation, hue, saturation);
    }

    /**
     * @param treeMap
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final float positiveHue, final float positiveSaturation,
        final float negativeHue, final float negativeSaturation) {
        this(treeMap, ColorDistributionTypes.Linear, positiveHue, positiveSaturation, negativeHue, negativeSaturation);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public HSBTreeMapColorProvider(final JXTreeMap treeMap, final ColorDistributionTypes colorDistribution,
        final float positiveHue, final float positiveSaturation, final float negativeHue,
        final float negativeSaturation) {
        super();
        jTreeMap = treeMap;
        this.colorDistribution = colorDistribution;
        adjustColor(positiveHue, positiveSaturation, negativeHue, negativeSaturation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.ColorProvider#getLegendPanel()
     */
    @Override
    public JPanel getLegendPanel() {
        if (legend == null) {
            legend = new Legend();
        }

        return legend;
    }

    /**
     * @param color
     */
    public void adjustColor(final Color color) {
        adjustColor(color, color);
    }

    /**
     * @param positiveColor
     * @param negativeColor
     */
    public void adjustColor(final Color positiveColor, final Color negativeColor) {
        // Figure out the hue of the passed in colors. Note, greys will map to
        // reds in this color space, so use the
        // hue/saturation
        // constructions for grey scales.
        float[] hsbvals = new float[HSBVAL_SIZE];

        hsbvals = Color.RGBtoHSB(positiveColor.getRed(), positiveColor.getGreen(), positiveColor.getBlue(), hsbvals);
        positiveHue = hsbvals[0];
        positiveSaturation = 1f;

        hsbvals = Color.RGBtoHSB(negativeColor.getRed(), negativeColor.getGreen(), negativeColor.getBlue(), hsbvals);
        negativeHue = hsbvals[0];
        negativeSaturation = 1f;
    }

    /**
     * @param hue
     * @param saturation
     */
    public void adjustColor(final float hue, final float saturation) {
        adjustColor(hue, saturation, hue, saturation);
    }

    /**
     * @param posHue
     * @param posSaturation
     * @param negHue
     * @param negSaturation
     */
    public void adjustColor(
        final float posHue, final float posSaturation, final float negHue, final float negSaturation) {
        this.positiveHue = posHue;
        this.positiveSaturation = posSaturation;
        this.negativeHue = negHue;
        this.negativeSaturation = negSaturation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.ColorProvider#getColor(net.sf.jtreemap.swing.Value)
     */
    @Override
    public Color getColor(final Value value) {
        // Figure out the current range of colors, map that range into a scale
        // from 0 to 1,
        // using the specified distribution type
        if (maxValue == null || minValue == null) {
            setValues(jTreeMap.getRoot());
        }
        final double max = this.maxValue.getValue();
        final double min = this.minValue.getValue();
        double val = (value != null ? value.getValue() : 0.00);

        if (val >= 0) {
            // Value is greater than 0, use the positive colors
            double range = max - Math.max(0, min);
            val -= Math.max(0, min);
            range = adjustValue(range);
            return Color.getHSBColor(positiveHue, positiveSaturation, (float) (adjustValue(val) / range));
        }

        // Value is less than 0, use the negative colors
        double range = Math.abs(min - Math.min(0, max));
        val += Math.min(0, max);
        val = Math.abs(val);
        // Value and range are not positive values, we need them to be for the
        // math functions
        range = adjustValue(range);
        return Color.getHSBColor(negativeHue, negativeSaturation, (float) (adjustValue(val) / range));
    }

    /**
     * Given a value, maps that value to a new value using the specified math function
     * 
     * @param value
     *            the value to convert
     * @return the converted value
     */
    private double adjustValue(final double value) {
        double ret = value;
        switch (colorDistribution) {
            case Log:
                ret = Math.log1p(value);
                break;
            case Exp:
                ret = Math.exp(value);
                break;
            case SquareRoot:
                ret = Math.sqrt(value);
                break;
            case CubicRoot:
                ret = Math.cbrt(value);
                break;
            default:
                // Linear
                ret = value;
                break;
        }
        return ret;
    }

    /**
     * Set the max and the min values in the tree map
     * 
     * @param root
     *            root of the JXTreeMap
     */
    private void setValues(final TreeMapNode root) {
        if (root.isLeaf()) {
            final Value value = root.getValue();

            if (value == null) {
                return;
            }

            setMaxValue(value);
            setMinValue(value);
        }
        else {
            for (final Enumeration e = root.children(); e.hasMoreElements();) {
                final TreeMapNode node = (TreeMapNode) e.nextElement();
                setValues(node);
            }
        }
    }

    private void setMinValue(final Value value) {
        if (minValue == null || value.getValue() <= minValue.getValue()) {
            try {
                final Class c = value.getClass();
                if (minValue == null) {
                    minValue = (Value) c.newInstance();
                }
                minValue.setValue(value.getValue());
            }
            catch (final IllegalAccessException iae) {
                // ignore
            }
            catch (final InstantiationException ie) {
                // Ignore
                log.error("Instantiation Issue", ie);
            }
        }
    }

    private void setMaxValue(final Value value) {
        if (maxValue == null || value.getValue() >= maxValue.getValue()) {
            try {
                final Class c = value.getClass();
                if (maxValue == null) {
                    maxValue = (Value) c.newInstance();
                }
                maxValue.setValue(value.getValue());
            }
            catch (final IllegalAccessException iae) {
                // ignore
            }
            catch (final InstantiationException ie) {
                // Ignore
                log.error("Instantiation Issue", ie);
            }
        }
    }

    private final JXTreeMap jTreeMap;

    private JPanel legend;

    private Value maxValue;

    private Value minValue;

    private float positiveHue;

    private float negativeHue;

    private float positiveSaturation = 1f;

    private float negativeSaturation = 1f;

    private ColorDistributionTypes colorDistribution = ColorDistributionTypes.Linear;

    /**
     * Panel with the legend
     * 
     * @author Laurent Dutheil
     */
    private class Legend extends JPanel {
        private static final int Y_INSET = 7;

        private static final int X_INSET = 15;

        private static final long serialVersionUID = 6371342387871103592L;

        private static final int HEIGHT = 20;

        private static final int WIDTH = 150;

        private static final int X = 20;

        private static final int Y = 25;

        /**
         * Constructor of Legend
         */
        public Legend() {
            this.setSize(new java.awt.Dimension(2 * Legend.X + Legend.WIDTH, 2 * Legend.Y + Legend.HEIGHT));
            this.setPreferredSize(new java.awt.Dimension(2 * Legend.X + Legend.WIDTH, 2 * Legend.Y + Legend.HEIGHT));
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (HSBTreeMapColorProvider.this.minValue == null || HSBTreeMapColorProvider.this.maxValue == null) {
                setValues(HSBTreeMapColorProvider.this.jTreeMap.getRoot());
            }
            final Value min = HSBTreeMapColorProvider.this.minValue;
            final Value max = HSBTreeMapColorProvider.this.maxValue;

            g.setColor(Color.black);
            if (min != null && max != null) {
                g.drawString(min.getLabel(), Legend.X - X_INSET, Legend.Y - Y_INSET);
                g.drawString(max.getLabel(), Legend.X + Legend.WIDTH - X_INSET, Legend.Y - Y_INSET);

                final double step = (max.getValue() - min.getValue()) / Legend.WIDTH;
                final Value value = new DefaultValue(min.getValue());
                for (int i = 0; i < Legend.WIDTH; i++) {
                    g.setColor(HSBTreeMapColorProvider.this.getColor(value));
                    g.fillRect(Legend.X + i, Legend.Y, 1, Legend.HEIGHT);
                    value.setValue(value.getValue() + step);
                }
            }
        }
    }
}
/*
 * ObjectLab is supporing JXTreeMap
 * 
 * Based in London, we are world leaders in the design and development of bespoke applications for the securities
 * financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more about us</a> ___ _ _ _ _ _ / _ \| |__ (_) ___ ___|
 * |_| | __ _| |__ | | | | '_ \| |/ _ \/ __| __| | / _` | '_ \ | |_| | |_) | | __/ (__| |_| |__| (_| | |_) |
 * \___/|_.__// |\___|\___|\__|_____\__,_|_.__/ |__/
 *
 * www.ObjectLab.co.uk
 */
