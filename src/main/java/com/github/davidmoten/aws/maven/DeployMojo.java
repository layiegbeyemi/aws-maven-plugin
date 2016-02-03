package com.github.davidmoten.aws.maven;

import java.io.File;
import java.util.Date;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deploy")
public final class DeployMojo extends AbstractMojo {

    @Parameter(property = "awsAccessKey")
    private String awsAccessKey;

    @Parameter(property = "awsSecretAccessKey")
    private String awsSecretAccessKey;

    @Parameter(property = "applicationName")
    private String applicationName;

    @Parameter(property = "environmentName")
    private String environmentName;

    @Parameter(property = "region")
    private String region;

    @Parameter(property = "artifact")
    private File artifact;

    @Parameter(property = "httpsProxyHost")
    private String httpsProxyHost;

    @Parameter(property = "httpsProxyPort")
    private int httpsProxyPort;

    @Parameter(property = "httpsProxyUsername")
    private String httpsProxyUsername;

    @Parameter(property = "httpsProxyPassword")
    private String httpsProxyPassword;

    @Parameter(property = "versionLabel")
    private String versionLabel;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Proxy proxy = new Proxy(httpsProxyHost, httpsProxyPort, httpsProxyUsername,
                httpsProxyPassword);

        if (versionLabel == null) {
            versionLabel = createVersionLabel(applicationName, new Date(), project.getVersion());
        }

        Deployer deployer = new Deployer(getLog());
        deployer.deploy(artifact, awsAccessKey, awsSecretAccessKey, region, applicationName,
                environmentName, versionLabel, proxy);
    }

    private static String createVersionLabel(String applicationName, Date date, String version) {
        // construct version label using application name and dateTime
        return applicationName + "_" + version + "_" + Util.formatDateTime(date);
    }

}
