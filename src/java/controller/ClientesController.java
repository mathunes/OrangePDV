package controller;

import aplicacao.Clientes;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ClientesDAO;

@WebServlet(name = "ClientesController", urlPatterns = {"/ClientesController"})
public class ClientesController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Impedir que a página seja armazena em cache, impedindo a função "voltar" do navegador
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        
        ClientesDAO dao = new ClientesDAO();
        String acao = (String) request.getParameter("acao");
        ArrayList<Clientes> clientes;
        
        switch (acao) {
            case "clientes":
                clientes = dao.getClientes();
                request.setAttribute("clientes", clientes);
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/aeclientes.jsp");
                rd.forward(request, response);
                break;
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
    }

}
