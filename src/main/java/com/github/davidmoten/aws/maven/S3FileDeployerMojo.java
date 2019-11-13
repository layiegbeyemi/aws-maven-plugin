package com.github.davidmoten.aws.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deployFileS3")
public final class S3FileDeployerMojo extends AbstractMojo {

    @Parameter(property = "region")
    private String region;

    @Parameter(property = "bucketName")
    private String bucketName;

    @Parameter(property = "file", required = true)
    private File file;

    @Parameter(property = "objectName")
    private String objectName;

    @Parameter(property = "create", defaultValue = "false")
    private boolean create;

    @Parameter(property = "httpsProxyHost")
    private String httpsProxyHost;

    @Parameter(property = "httpsProxyPort")
    private int httpsProxyPort;

    @Parameter(property = "httpsProxyUsername")
    private String httpsProxyUsername;

    @Parameter(property = "httpsProxyPassword")
    private String httpsProxyPassword;

    @Parameter(property = "awsKmsKeyId")
    private String awsKmsKeyId;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Proxy proxy = new Proxy(httpsProxyHost, httpsProxyPort, httpsProxyUsername, httpsProxyPassword);

        S3FileDeployer deployer = new S3FileDeployer(getLog());
        deployer.deploy(region, file, bucketName, objectName, proxy, create, awsKmsKeyId);
    }

}
