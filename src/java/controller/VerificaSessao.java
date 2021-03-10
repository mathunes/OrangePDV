package controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//Servlet para filtrar a URL e restringir acesso indevido

@WebFilter(filterName = "VerificaSessao", urlPatterns = {"/produtos.jsp", 
    "/vendas.jsp", "/clientes.jsp", "/navbar.jsp", "/ClientesController",     
    "/cliente.jsp", "/formcliente.jsp", "/venda.jsp", "/formvenda.jsp", 
    "/VendasController", "/ProdutosController"})
public class VerificaSessao implements Filter {
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        HttpSession session = req.getSession();
        
        Object logado = session.getAttribute("logado");
        
        if (logado != null) {

            if (((String)logado).equals("ok")) {

                chain.doFilter(request, response);
                
            }

        } else {
            res.sendRedirect("index.jsp");
        }
        
    }

    public void destroy() {        
    }

    public void init(FilterConfig filterConfig) {        
        
    }

}
