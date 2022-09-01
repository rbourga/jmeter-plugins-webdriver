package com.googlecode.jmeter.plugins.webdriver.config.gui;

import com.googlecode.jmeter.plugins.webdriver.config.FirefoxDriverConfig;
import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.Grid;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FirefoxDriverConfigGui extends WebDriverConfigGui implements ItemListener {

    private static final long serialVersionUID = 100L;
    JTextField firefoxServicePath;
    JTextField userAgentOverrideText;
    JCheckBox legacyCheckbox;
    JCheckBox acceptInsecureCertsCheckbox;
    JCheckBox headlessCheckbox;
    JCheckBox userAgentOverrideCheckbox;
    JCheckBox ntlmOverrideCheckbox;
    private Grid extensions;
    private Grid preferences;

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("Firefox Driver Config");
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    protected JPanel createBrowserPanel() {
        return createProfilePanel();
    }

    @Override
    protected String browserName() {
        return "Firefox";
    }

    @Override
    protected String getWikiPage() {
        return "FirefoxDriverConfig";
    }

    @Override
    public TestElement createTestElement() {
        FirefoxDriverConfig element = new FirefoxDriverConfig();
        modifyTestElement(element);
        return element;
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof FirefoxDriverConfig) {
            FirefoxDriverConfig config = (FirefoxDriverConfig) element;
            firefoxServicePath.setText(config.getFirefoxDriverPath());
            acceptInsecureCertsCheckbox.setSelected(config.isAcceptInsecureCerts());
            headlessCheckbox.setSelected(config.isHeadless());
            userAgentOverrideCheckbox.setSelected(config.isUserAgentOverridden());
            userAgentOverrideText.setText(config.getUserAgentOverride());
            userAgentOverrideText.setEnabled(config.isUserAgentOverridden());

            JMeterProperty ext = config.getExtensions();
            if (!(ext instanceof NullProperty)) {
                JMeterPluginsUtils.collectionPropertyToTableModelRows((CollectionProperty) ext, extensions.getModel());
            }

            JMeterProperty pref = config.getPreferences();
            if (!(ext instanceof NullProperty)) {
                JMeterPluginsUtils.collectionPropertyToTableModelRows((CollectionProperty) pref, preferences.getModel());
            }
        }
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.modifyTestElement(element);
        if (element instanceof FirefoxDriverConfig) {
            FirefoxDriverConfig config = (FirefoxDriverConfig) element;
            config.setFirefoxDriverPath(firefoxServicePath.getText());
            config.setAcceptInsecureCerts(acceptInsecureCertsCheckbox.isSelected());
            config.setHeadless(headlessCheckbox.isSelected());
            config.setUserAgentOverridden(userAgentOverrideCheckbox.isSelected());
            config.setNtlmSetting(ntlmOverrideCheckbox.isSelected());
            if (userAgentOverrideCheckbox.isSelected()) {
                config.setUserAgentOverride(userAgentOverrideText.getText());
            }
            config.setExtensions(extensions.getModel());
            config.setPreferences(preferences.getModel());
        }
    }

    private JPanel createProfilePanel() {
        final JPanel firefoxPanel = new VerticalPanel();
        
        final JPanel firefoxServicePanel = new HorizontalPanel();
        final JLabel firefoxDriverServiceLabel = new JLabel("Path to Firefox Driver");
        firefoxServicePanel.add(firefoxDriverServiceLabel);
        firefoxServicePath = new JTextField();
        firefoxServicePanel.add(firefoxServicePath);
        firefoxPanel.add(firefoxServicePanel);

        legacyCheckbox = new JCheckBox("Legacy mode");
        legacyCheckbox.setSelected(false);
        legacyCheckbox.setEnabled(true);
        firefoxPanel.add(legacyCheckbox);

        acceptInsecureCertsCheckbox = new JCheckBox("Accept Insecure Certificates");
        acceptInsecureCertsCheckbox.setSelected(false);
        acceptInsecureCertsCheckbox.setEnabled(true);
        firefoxPanel.add(acceptInsecureCertsCheckbox);

        headlessCheckbox = new JCheckBox("Headless");
        headlessCheckbox.setSelected(false);
        headlessCheckbox.setEnabled(true);
        firefoxPanel.add(headlessCheckbox);

        userAgentOverrideCheckbox = new JCheckBox("Override User Agent");
        userAgentOverrideCheckbox.setSelected(false);
        userAgentOverrideCheckbox.setEnabled(true);
        userAgentOverrideCheckbox.addItemListener(this);
        firefoxPanel.add(userAgentOverrideCheckbox);

        userAgentOverrideText = new JTextField();
        userAgentOverrideText.setEnabled(false);
        firefoxPanel.add(userAgentOverrideText);

        ntlmOverrideCheckbox = new JCheckBox("Enable NTLM");
        ntlmOverrideCheckbox.setSelected(false);
        ntlmOverrideCheckbox.setEnabled(true);
        ntlmOverrideCheckbox.addItemListener(this);
        firefoxPanel.add(ntlmOverrideCheckbox);

        extensions = new Grid("Load Extensions", new String[]{"Path to XPI File"}, new Class[]{String.class}, new String[]{""});
        firefoxPanel.add(extensions);

        preferences = new Grid("Set Preferences", new String[]{"Name", "Value"}, new Class[]{String.class, String.class}, new String[]{"", ""});
        firefoxPanel.add(preferences);

        final JPanel browserPanel = new VerticalPanel();
        browserPanel.add(firefoxPanel);
        return browserPanel;
    }

    @Override
    public void clearGui() {
        super.clearGui();
        firefoxServicePath.setText("");
        headlessCheckbox.setSelected(false);
        userAgentOverrideCheckbox.setSelected(false);
        userAgentOverrideText.setText("");
        ntlmOverrideCheckbox.setSelected(false);
        extensions.getModel().clearData();
        preferences.getModel().clearData();
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == userAgentOverrideCheckbox) {
            userAgentOverrideText.setEnabled(userAgentOverrideCheckbox.isSelected());
        }
    }

    @Override
    protected boolean isProxyEnabled() {
        return true;
    }

    @Override
    protected boolean isExperimentalEnabled() {
        return true;
    }

}