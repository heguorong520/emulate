package com.emulate.core.filter;


import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public MyHttpServletRequest(HttpServletRequest request)  {
        super(request);
        try{
            BufferedReader reader  =  new BufferedReader(new InputStreamReader(request.getInputStream()));
            this.body = IOUtils.toByteArray(reader,"utf-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
