package cz.cvut.fel.ear.posilovna.config.filters;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.FilterChain;
import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        logRequestDetails(request);
        filterChain.doFilter(request, response);
    }

    private void logRequestDetails(HttpServletRequest request) {
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Headers: " + getRequestHeaders(request));
        System.out.println("Request Parameters: " + getRequestParameters(request));
    }

    private String getRequestHeaders(HttpServletRequest request) {
        // Extract and format request headers
        // Modify this method based on your logging needs
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(header ->
                headers.append(header).append(": ").append(request.getHeader(header)).append(", ")
        );
        return headers.toString();
    }

    private String getRequestParameters(HttpServletRequest request) {
        // Extract and format request parameters
        // Modify this method based on your logging needs
        StringBuilder parameters = new StringBuilder();
        request.getParameterMap().forEach((param, values) ->
                parameters.append(param).append(": ").append(String.join(", ", values)).append(", ")
        );
        return parameters.toString();
    }

}