package com.googlecode.jmeter.plugins.webdriver.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeDriverConfig extends WebDriverConfig<ChromeDriver> {

    private static final long serialVersionUID = 100L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverConfig.class);
    private static final String CHROME_SERVICE_PATH = "ChromeDriverConfig.chromedriver_path";
    private static final String ANDROID_ENABLED = "ChromeDriverConfig.android_enabled";
    private static final String HEADLESS_ENABLED = "ChromeDriverConfig.headless_enabled";
    private static final String INSECURECERTS_ENABLED = "ChromeDriverConfig.insecurecerts_enabled";
    private static final String INCOGNITO_ENABLED = "ChromeDriverConfig.incognito_enabled";
    private static final String NO_SANDBOX_ENABLED = "ChromeDriverConfig.no_sandbox_enabled";
    private static final String ADDITIONAL_ARGS = "ChromeDriverConfig.additional_args";
    private static final String BINARY_PATH = "ChromeDriverConfig.binary_path";
    private static final String DISABLE_DEV_SHM_USAGE="ChromeDriverConfig.disable_dev_shm_usage";

    private static final Map<String, ChromeDriverService> services = new ConcurrentHashMap<String, ChromeDriverService>();

    public void setChromeDriverPath(String path) {
        setProperty(CHROME_SERVICE_PATH, path);
    }

    public String getChromeDriverPath() {
        return getPropertyAsString(CHROME_SERVICE_PATH);
    }

    public void setBinaryPath(String binaryPath) {
        setProperty(BINARY_PATH, binaryPath);
    }

    public String getBinaryPath() {
        return getPropertyAsString(BINARY_PATH);
    }

    ChromeOptions createOptions() {
    	ChromeOptions chromeOptions = new ChromeOptions();
    	chromeOptions.setCapability(CapabilityType.PROXY, createProxy());

        LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		chromeOptions.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);
        
        final String additionalArgs = trimmed(getAdditionalArgs());
        final String binaryPath = trimmed(getBinaryPath());
        if(isAndroidEnabled() || isHeadlessEnabled() || isIncognitoEnabled() || isNoSandboxEnabled() || (null != additionalArgs && !additionalArgs.isEmpty()) || (null != binaryPath && !binaryPath.isEmpty()) || isDisableDevShmUsage()) {
            if (isAndroidEnabled()) {
                chromeOptions.setExperimentalOption("androidPackage", "com.android.chrome");
            }
            if (isHeadlessEnabled()) {
                chromeOptions.addArguments("--headless");
                //Adding the options to whitelist all IPs to allow the WebDriverSampler to call ChromeDriver from Docker in headless mode
                chromeOptions.addArguments("--whitelisted-ips");
            }
            if (isNoSandboxEnabled()) {
                chromeOptions.addArguments("--no-sandbox");
            }
            if (isDisableDevShmUsage()){
                chromeOptions.addArguments("--disable-dev-shm-usage");
            }
            if (isIncognitoEnabled()) {
                chromeOptions.addArguments("--incognito");
            }
            if(null != additionalArgs && !additionalArgs.isEmpty()) {
                chromeOptions.addArguments(additionalArgs.split("\\s+"));
            }
            if(null != binaryPath && !binaryPath.isEmpty()) {
                chromeOptions.setBinary(binaryPath);
            }
        }

        if(isInsecureCertsEnabled()) {
        	chromeOptions.setCapability("acceptInsecureCerts", true);
        }

        return chromeOptions;
    }

    private String trimmed(String str) {
        return null == str ? null : str.trim();
    }

    Map<String, ChromeDriverService> getServices() {
        return services;
    }

    @Override
    protected ChromeDriver createBrowser() {
        final ChromeDriverService service = getThreadService();
        ChromeOptions options = createOptions();
        return service != null ? new ChromeDriver(service, options) : null;
    }

    @Override
    public void quitBrowser(final ChromeDriver browser) {
        super.quitBrowser(browser);
        final ChromeDriverService service = services.remove(currentThreadName());
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }

    private ChromeDriverService getThreadService() {
        ChromeDriverService service = services.get(currentThreadName());
        if (service != null) {
            return service;
        }
        try {
            service = new ChromeDriverService.Builder().usingDriverExecutable(new File(getChromeDriverPath())).build();
            service.start();
            services.put(currentThreadName(), service);
        } catch (IOException e) {
            LOGGER.error("Failed to start chrome service");
            service = null;
        }
        return service;
    }

    public boolean isAndroidEnabled() {
        return getPropertyAsBoolean(ANDROID_ENABLED);
    }

    public void setAndroidEnabled(boolean enabled) {
        setProperty(ANDROID_ENABLED, enabled);
    }

    public boolean isHeadlessEnabled() {
        return getPropertyAsBoolean(HEADLESS_ENABLED);
    }

    public void setHeadlessEnabled(boolean enabled) {
        setProperty(HEADLESS_ENABLED, enabled);
    }

    public boolean isInsecureCertsEnabled() {
        return getPropertyAsBoolean(INSECURECERTS_ENABLED);
    }

    public void setInsecureCertsEnabled(boolean enabled) {
        setProperty(INSECURECERTS_ENABLED, enabled);
    }

    public boolean isIncognitoEnabled() {
        return getPropertyAsBoolean(INCOGNITO_ENABLED);
    }

    public void setIncognitoEnabled(boolean enabled) {
        setProperty(INCOGNITO_ENABLED, enabled);
    }

    public boolean isNoSandboxEnabled() {
        return getPropertyAsBoolean(NO_SANDBOX_ENABLED);
    }

    public void setNoSandboxEnabled(boolean enabled) { setProperty(NO_SANDBOX_ENABLED, enabled); }

    public String getAdditionalArgs() {
        return getPropertyAsString(ADDITIONAL_ARGS);
    }

    public void setAdditionalArgs(String additionalArgs) {
        setProperty(ADDITIONAL_ARGS, additionalArgs);
    }

    public boolean isDisableDevShmUsage(){
        return getPropertyAsBoolean(DISABLE_DEV_SHM_USAGE);
    }

    public void setDisableDevShmUsage(boolean enabled){
        setProperty(DISABLE_DEV_SHM_USAGE,enabled);
    }
}
