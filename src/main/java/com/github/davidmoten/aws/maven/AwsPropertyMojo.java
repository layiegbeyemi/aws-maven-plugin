package com.github.davidmoten.aws.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;

@Mojo(name = "property")
public class AwsPropertyMojo extends AbstractMojo {

    @Parameter(property = "region")
    private String region;

    @Parameter(property = "httpsProxyHost")
    private String httpsProxyHost;

    @Parameter(property = "httpsProxyPort")
    private int httpsProxyPort;

    @Parameter(property = "httpsProxyUsername")
    private String httpsProxyUsername;

    @Parameter(property = "httpsProxyPassword")
    private String httpsProxyPassword;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Proxy proxy = new Proxy(httpsProxyHost, httpsProxyPort, httpsProxyUsername,
                httpsProxyPassword);
        ClientConfiguration cc = Util.createConfiguration(proxy);
        AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder //
                .standard() //
                .withRegion(region) //
                .withClientConfiguration(cc) //
                .build();
        String accountId = iam.getUser().getUser().getUserId();
        project.getProperties().setProperty("aws.account.id", accountId);
        getLog().info("The following properties have been set for the project");
        getLog().info("aws.account.id=" + accountId);
    }
}
