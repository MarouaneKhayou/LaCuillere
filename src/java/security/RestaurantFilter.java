/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import bean.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marouane
 */
public class RestaurantFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession s = ((HttpServletRequest) request).getSession();

        if (s.getAttribute("Utilisateur") == null) {
            ((HttpServletResponse) response).sendRedirect(request.getServletContext().getContextPath() + "/faces/user/login.xhtml");
        } else if (((User) s.getAttribute("Utilisateur")).getProfil().equals("N")) {
            ((HttpServletResponse) response).sendRedirect(request.getServletContext().getContextPath() + "/faces/index.xhtml");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
