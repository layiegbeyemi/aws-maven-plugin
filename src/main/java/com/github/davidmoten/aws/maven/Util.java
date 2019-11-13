package com.github.davidmoten.aws.maven;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import com.amazonaws.ClientConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

final class Util {

    static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    static ClientConfiguration createConfiguration(Proxy proxy) {
        ClientConfiguration cc = new ClientConfiguration();
        if (proxy.host != null) {
            cc.setProxyHost(proxy.host);
            cc.setProxyPort(proxy.port);
            if (proxy.username != null) {
                cc.setProxyUsername(proxy.username);
                cc.setProxyPassword(proxy.password);
            }
        }
        return cc;
    }

    public static String formatJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter() //
                    .writeValueAsString(mapper.readValue(json, Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
