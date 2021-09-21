package com.emulate.core.filter;


import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public MyHttpServletRequest(HttpServletRequest request,String body)  {
        super(request);
        try{
            this.body = body.getBytes(StandardCharsets.UTF_8);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayIns = new ByteArrayInputStream(body);
        ServletInputStream servletIns = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {

            }
            @Override
            public int read() throws IOException {
                return byteArrayIns.read();
            }
        };
        return  servletIns;
    }
}
