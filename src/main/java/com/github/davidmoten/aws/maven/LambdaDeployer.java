package com.github.davidmoten.aws.maven;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.CreateAliasRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

class LambdaDeployer {

    private final Log log;

    LambdaDeployer(Log log) {
        this.log = log;
    }

    void deploy(String region, String zipFilename, String functionName, String functionAlias, Proxy proxy) {
        long t = System.currentTimeMillis();

        AWSLambda lambda = AWSLambdaClientBuilder.standard()
                .withClientConfiguration(Util.createConfiguration(proxy)).withRegion(region).build();

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(new FileInputStream(zipFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DecimalFormat df = new DecimalFormat("0.000");
        log.info("deploying " + zipFilename + ", length=" + df.format(bytes.length / 1024.0 / 1024.0)
                + "MB, to functionName=" + functionName);
        UpdateFunctionCodeResult updateFunctionCodeResult = lambda.updateFunctionCode( //
                new UpdateFunctionCodeRequest() //
                        .withFunctionName(functionName) //
                        .withPublish(true) //
                        .withZipFile(ByteBuffer.wrap(bytes)));
        log.info("deployed in " + (System.currentTimeMillis() - t) + "ms");
        Optional<String> optionalFunctionAlias = Optional.ofNullable(functionAlias);
        if (optionalFunctionAlias.isPresent()) {
            // alias only likes underscores, have to strip out other characters if they are present
            String sanitisedFunctionAlias = sanitizeFunctionAlias(optionalFunctionAlias.get());
            lambda.createAlias( //
                new CreateAliasRequest() //
                    .withFunctionVersion(updateFunctionCodeResult.getVersion()) //
                    .withFunctionName(functionName) //
                    .withName(sanitisedFunctionAlias));
            log.info("created alias=" + sanitisedFunctionAlias + " to functionName=" + functionName + " for version=" + updateFunctionCodeResult.getVersion());
        }
    }

    @VisibleForTesting
    static String sanitizeFunctionAlias(String functionAlias) {
        Preconditions.checkNotNull(functionAlias);
        return functionAlias //
                // replace dash or dot with underscore
                .replaceAll("[-\\.]", "_") //
                // remove colons
                .replaceAll(":", "");
    }

}
