package vn.edu.uit.chat_application.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomWsCorsFilter extends OncePerRequestFilter {
    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        doFilter(request, response, filterChain);
        String source = request.getHeader("Origin");
        if (source != null) {
            response.setHeader("Access-Control-Allow-Origin", source);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }
}
