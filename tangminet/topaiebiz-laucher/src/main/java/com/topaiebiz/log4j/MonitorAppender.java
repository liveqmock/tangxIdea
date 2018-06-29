package com.topaiebiz.log4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Sends log events over HTTP.
 */
@Plugin(name = "Monitor", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class MonitorAppender extends AbstractAppender {
    private static Charset utf8 = Charset.forName("UTF-8");
    private static int timeout = 1000;
    private String apiUrl;

    @AllArgsConstructor
    @Getter
    enum EnvEnum {
        PROD("prod", "http://api.motherbuy.com/"),
        TEST("test", "http://test1.mamago.com/"),
        LOCAL("local", "http://localhost/"),;
        private String code;
        private String value;

        public static String getValueByCode(String code) {
            for (EnvEnum e : EnvEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getValue();
                }
            }
            return null;
        }
    }

    /**
     * Builds HttpAppender instances.
     *
     * @param <B> The type to build
     */
    public static class Builder<B extends MonitorAppender.Builder<B>> extends AbstractAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<MonitorAppender> {

        /**
         * 配置的环境
         */
        @PluginBuilderAttribute
        private String env;

        private String apiUrl;

        @Override
        public MonitorAppender build() {
            apiUrl = StringUtils.join(EnvEnum.getValueByCode(env), "monitor/log/error");
            return new MonitorAppender(getName(), getLayout(), getFilter(), isIgnoreExceptions(), apiUrl);
        }
    }

    /**
     * @return a builder for a HttpAppender.
     */
    @PluginBuilderFactory
    public static <B extends MonitorAppender.Builder<B>> B newBuilder() {
        return new MonitorAppender.Builder<B>().asBuilder();
    }


    private MonitorAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter,
                            final boolean ignoreExceptions, String apiUrl) {
        super(name, filter, layout, ignoreExceptions);
        this.apiUrl = apiUrl;
        Objects.requireNonNull(layout, "layout");
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void append(final LogEvent event) {
        try {
            String exceptionContent = new String(getLayout().toByteArray(event), utf8);
            String content = Request.Post(this.apiUrl)
                    .bodyForm(Form.form()
                            .add("data", exceptionContent)
                            .add("loggerName", event.getLoggerName())
                            .build(), utf8)
                    .connectTimeout(timeout)
                    .socketTimeout(timeout)
                    .execute()
                    .returnContent()
                    .asString(utf8);
        } catch (final Exception e) {
            error("Unable to send HTTP in appender [" + getName() + "]", event, e);
        }
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();
        boolean stopped = super.stop(timeout, timeUnit, false);
        setStopped();
        return stopped;
    }

    @Override
    public String toString() {
        return "MonitorAppender{" +
                "name=" + getName() +
                ", state=" + getState() +
                '}';
    }

}
