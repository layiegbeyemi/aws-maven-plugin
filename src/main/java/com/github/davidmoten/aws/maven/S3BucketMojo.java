package com.github.davidmoten.aws.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.SettingsDecrypter;

@Mojo(name = "createS3Bucket")
public final class S3BucketMojo extends AbstractMojo {

	@Parameter(property = "region")
	private String region;

	@Parameter(property = "bucketName")
	private String bucketName;

	@Parameter(property = "httpsProxyHost")
	private String httpsProxyHost;

	@Parameter(property = "httpsProxyPort")
	private int httpsProxyPort;

	@Parameter(property = "httpsProxyUsername")
	private String httpsProxyUsername;

	@Parameter(property = "httpsProxyPassword")
	private String httpsProxyPassword;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		Proxy proxy = new Proxy(httpsProxyHost, httpsProxyPort, httpsProxyUsername, httpsProxyPassword);

		S3Bucket bucket = new S3Bucket(getLog());
		bucket.create(region, bucketName, proxy);
	}

}
