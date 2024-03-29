package controller;

import aplicacao.Usuarios;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.UsuariosDAO;

@WebServlet(name = "UsuariosController", urlPatterns = {"/UsuariosController"})
public class UsuariosController extends HttpServlet {
    private UsuariosDAO dao = new UsuariosDAO();
    private Usuarios usuario;

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = (String) request.getParameter("acao");
        ArrayList<Usuarios> usuarios;
        int id;
        
        switch (acao) {
            //Requisição para exibir todos os usuários
            case "mostrar_usuarios":
                usuarios = dao.getUsuarios();
                request.setAttribute("usuarios", usuarios);
                RequestDispatcher mostrarUsuarios = getServletContext().getRequestDispatcher("/usuarios.jsp");
                mostrarUsuarios.forward(request, response);
                break;
            
            //Requisição para exibir o usuario pelo id
            case "mostrar_usuario":
                id = Integer.parseInt(request.getParameter("id"));
                usuario = dao.getUsuarioId(id);
                request.setAttribute("usuario", usuario);      
                RequestDispatcher mostrarUsuario = getServletContext().getRequestDispatcher("/usuario.jsp");
                mostrarUsuario.forward(request, response);
                break;
            
            //Requisição para exibir o usuario pelo nome - usado no campo de busca
            case "mostrar_usuario_nome":
                String nome = request.getParameter("nome");
                usuarios = dao.getUsuarioNome(nome);
                request.setAttribute("usuarios", usuarios);
                RequestDispatcher mostrarUsuariosNome = getServletContext().getRequestDispatcher("/usuarios.jsp");
                mostrarUsuariosNome.forward(request, response);
                break;
                
            //Requisição para editar o usuario pelo id
            case "editar_usuario":
                id = Integer.parseInt(request.getParameter("id"));
                usuario = dao.getUsuarioId(id);
                request.setAttribute("usuario", usuario);
                RequestDispatcher editarUsuario = request.getRequestDispatcher("/formusuario.jsp");
                editarUsuario.forward(request, response);
                break;
                
            //Requisição para excluir o usuario pelo id
            case "excluir_usuario":
                id = Integer.parseInt(request.getParameter("id"));
                if (dao.excluir(id))
                    request.setAttribute("mensagem", "Usuario excluído");
                else
                    request.setAttribute("mensagem", "Erro ao excluir usuario");
                
                //Enviando relação de usuarios para evitar o reload e perder a mensagem
                usuarios = dao.getUsuarios();
                request.setAttribute("usuarios", usuarios);
                
                RequestDispatcher excluirUsuario = getServletContext().getRequestDispatcher("/usuarios.jsp");
                excluirUsuario.forward(request, response);
                break;
                
            //Requisição para cadastrar o usuario
            case "cadastrar_usuario":
                usuario = new Usuarios();
                usuario.setId(0);
                usuario.setNome("");
                usuario.setCpf("");
                usuario.setSenha("");
                usuario.setTipo("");
                
                request.setAttribute("usuario", usuario);
                
                RequestDispatcher cadastrarUsuario = getServletContext().getRequestDispatcher("/formusuario.jsp");
                cadastrarUsuario.forward(request, response);
                break;
        }
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        //Identificador da acao no método POST
        String acao = request.getParameter("acao");
        String cpf = request.getParameter("cpf");
        String senha = request.getParameter("senha");
        switch (acao) {
            case "logout":
                logout(request, response);
                break;
            case "cadastrar_usuario":
                String mensagem;

                int id = Integer.parseInt(request.getParameter("id"));
                String nome = request.getParameter("nome");
                String tipo = request.getParameter("tipo");
                
                if (nome.isEmpty() || cpf.isEmpty() || senha.isEmpty() || tipo.isEmpty()){
                    mensagem = "Preencha todos os campos";
                }else if (nome.length() > 50)
                    mensagem = "Nome deve conter no máximo 50 caracteres";
                else if (cpf.length() > 14) 
                    mensagem = "CPF deve conter no máximo 14 caracteres";
                else if (senha.length() > 10)
                    mensagem = "Senha deve conter no máximo 10 caracteres";
                else if (tipo.length() > 1)
                    mensagem = "Tipo deve conter no máximo 1 caractere";                
                else if (!validaCPF(cpf))
                    mensagem = "CPF inválido";
                else {

                    usuario.setId(id);
                    usuario.setNome(nome);
                    usuario.setCpf(cpf);
                    usuario.setSenha(senha);
                    usuario.setTipo(tipo);

                    if (dao.gravar(usuario))
                        mensagem = "Usuario gravado com sucesso";
                    else 
                        mensagem = "Erro ao gravar usuario";
                }
                //Enviando relação de usuarios para usuarios.jsp
                ArrayList<Usuarios> usuarios;       
                usuarios = dao.getUsuarios();
                request.setAttribute("usuarios", usuarios);
                request.setAttribute("mensagem", mensagem);
                RequestDispatcher rd = request.getRequestDispatcher("/usuarios.jsp");
                rd.forward(request, response);
                break;
            
        }
            
    }
    
    public static boolean validaCPF(String cpf) {
        double soma = 0;
        double resto;
        
        cpf = cpf.replaceAll("\\.", "");
        cpf = cpf.replaceAll("\\-", "");
        
        if ((cpf.equals("00000000000")) || (cpf.equals("11111111111")) || (cpf.equals("22222222222")) 
            || (cpf.equals("33333333333")) || (cpf.equals("44444444444")) || (cpf.equals("55555555555")) 
            || (cpf.equals("66666666666")) || (cpf.equals("77777777777")) || (cpf.equals("88888888888")) 
            || (cpf.equals("99999999999")))
            return false;
        
        for (int i=1; i<=9; i++)
            soma += (Integer.parseInt(cpf.substring(i-1, i)) * (11 - i));
        resto = (soma * 10) % 11;
        
        if ((resto == 10) || (resto == 11))
            resto = 0;
        if (resto != Integer.parseInt(cpf.substring(9,10))) 
            return false;
        
        soma = 0;
        for (int i = 1; i <= 10; i++)
            soma += (Integer.parseInt(cpf.substring(i-1, i)) * (12 - i));
        resto = (soma * 10) % 11;

        if ((resto == 10) || (resto == 11))
            resto = 0;
        if (resto != Integer.parseInt(cpf.substring(10,11))) 
            return false;
        return true;

    }

    private void logout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.jsp");
    }

}