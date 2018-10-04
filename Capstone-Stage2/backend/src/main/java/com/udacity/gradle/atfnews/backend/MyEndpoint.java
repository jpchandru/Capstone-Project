package com.udacity.gradle.atfnews.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.atfnews.gradle.udacity.com",
                ownerName = "backend.atfnews.gradle.udacity.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "jokes")
    public MyBean printJokes() {
        MyBean response = new MyBean();
        //response.setData("Hi, " + name);
        response.getData();
        return response;
    }

}