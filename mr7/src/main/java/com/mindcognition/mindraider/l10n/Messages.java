/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mindcognition.mindraider.l10n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.panels.ProfileJPanel;

/**
 * Localized messages.
 *
 * @author <a href="mailto:fgiust@users.sourceforge.net">Fabrizio Giustina</a>
 */
public final class Messages {

    /**
     * The bundle name constant.
     */
    private static final String BUNDLE_NAME = "com.mindcognition.mindraider.l10n.messages"; //$NON-NLS-1$

    /**
     * The ResourceBundle constant.
     */
    private static final ResourceBundle RESOURCE_BUNDLE;

    static {
        // determine whether system locale should be overriden
        if(MindRaider.profile!=null && MindRaider.profile.isOverrideSystemLocale()) {
            if(MindRaider.profile.getCustomLocale()!=null) {
                if(MindRaider.profile.getCustomLocale().equals(ProfileJPanel.CZECH)) {
                    Locale.setDefault(new Locale("cs","CZ"));
                    RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
                } else {
                    if(MindRaider.profile.getCustomLocale().equals(ProfileJPanel.ENGLISH)) {
                        Locale.setDefault(Locale.ENGLISH);
                        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
                    } else {
                        if(MindRaider.profile.getCustomLocale().equals(ProfileJPanel.ITALIAN)) {
                            Locale.setDefault(Locale.ITALIAN);
                            RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ITALIAN);
                        } else {
                            if(MindRaider.profile.getCustomLocale().equals(ProfileJPanel.FRENCH)) {
                                Locale.setDefault(Locale.FRENCH);
                                RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.FRENCH);
                            } else {
                                RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
                            }
                        }
                    }
                }
            } else {
                RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
            }
        } else {
            RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
        }
    }

    /**
     * Private constructor.
     */
    private Messages() {
    }

    /**
     * Returns the message string for given key.
     *
     * @param key
     *            the key.
     * @return Returns the message.
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the message string for given key and parameters.
     *
     * @param key
     *            the key.
     * @param params
     *            the parameters array.
     * @return Returns the message.
     */
    public static String getString(String key, Object[] params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the message string for given key and parameter.
     *
     * @param key
     *            the key.
     * @param param
     *            the parameter
     * @return Returns the message.
     */
    public static String getString(String key, Object param) {
        return getString(key, new Object[] { param });
    }
}
