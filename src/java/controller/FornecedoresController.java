package controller;

import aplicacao.Fornecedores;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.FornecedoresDAO;

@WebServlet(name = "FornecedoresController", urlPatterns = {"/FornecedoresController"})
public class FornecedoresController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        FornecedoresDAO dao = new FornecedoresDAO();
        String acao = (String) request.getParameter("acao");
        ArrayList<Fornecedores> fornecedores;
        int id;
        Fornecedores fornecedor;
        
        switch (acao) {
            //Requisição para exibir todos os fornecedores
            case "mostrar_fornecedores":
                fornecedores = dao.getFornecedores();
                request.setAttribute("fornecedores", fornecedores);
                RequestDispatcher mostrarFornecedores = getServletContext().getRequestDispatcher("/fornecedores.jsp");
                mostrarFornecedores.forward(request, response);
                break;
            
            //Requisição para exibir o fornecedor pelo id
            case "mostrar_fornecedor":
                id = Integer.parseInt(request.getParameter("id"));
                fornecedor = dao.getFornecedorId(id);
                request.setAttribute("fornecedor", fornecedor);
                RequestDispatcher mostrarFornecedor = getServletContext().getRequestDispatcher("/fornecedor.jsp");
                mostrarFornecedor.forward(request, response);
                break;
            
            //Requisição para exibir o fornecedor pelo razaoSocial - usado no campo de busca
            case "buscar_fornecedores":
                String razaoSocial = request.getParameter("razao_social");
                fornecedores = dao.getRazaoSocialFornecedor(razaoSocial);
                request.setAttribute("fornecedores", fornecedores);
                RequestDispatcher mostrarFornecedoresRazaoSocial = getServletContext().getRequestDispatcher("/fornecedores.jsp");
                mostrarFornecedoresRazaoSocial.forward(request, response);
                break;
                
            //Requisição para editar o fornecedor pelo id
            case "editar_fornecedor":
                id = Integer.parseInt(request.getParameter("id"));
                fornecedor = dao.getFornecedorId(id);
                
                request.setAttribute("fornecedor", fornecedor);
                RequestDispatcher editarFornecedor = request.getRequestDispatcher("/formfornecedor.jsp");
                editarFornecedor.forward(request, response);
                
                break;
                
            //Requisição para excluir o fornecedor pelo id
            case "excluir_fornecedor":
                id = Integer.parseInt(request.getParameter("id"));
                if (dao.excluir(id))
                    request.setAttribute("mensagem", "Fornecedor excluído");
                else
                    request.setAttribute("mensagem", "Esse fornecedor está sendo referenciado em alguma compra. Por favor, retire a referência e tente excluí-lo novamente.");
                
                //Enviando relação de fornecedores para evitar o reload e perder a mensagem
                fornecedores = dao.getFornecedores();
                request.setAttribute("fornecedores", fornecedores);
                
                RequestDispatcher excluirFornecedor = getServletContext().getRequestDispatcher("/fornecedores.jsp");
                excluirFornecedor.forward(request, response);
                break;
                
            //Requisição para cadastrar o fornecedor
            case "cadastrar_fornecedor":
                fornecedor = new Fornecedores();
                fornecedor.setId(0);
                fornecedor.setRazaoSocial("");
                fornecedor.setCnpj("");
                fornecedor.setEndereco("");
                fornecedor.setBairro("");
                fornecedor.setCidade("");
                fornecedor.setUf("");
                fornecedor.setCep("");
                fornecedor.setTelefone("");
                fornecedor.setEmail("");
                
                request.setAttribute("fornecedor", fornecedor);
                
                RequestDispatcher cadastrarFornecedor = getServletContext().getRequestDispatcher("/formfornecedor.jsp");
                cadastrarFornecedor.forward(request, response);
                break;
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
                
        String mensagem;
        
        Fornecedores fornecedor = new Fornecedores();
        FornecedoresDAO dao = new FornecedoresDAO();
        
        int id = Integer.parseInt(request.getParameter("id"));
        String razaoSocial = request.getParameter("razao_social");
        String cnpj = request.getParameter("cnpj");
        String endereco = request.getParameter("endereco");
        String bairro = request.getParameter("bairro");
        String cidade = request.getParameter("cidade");
        String uf = request.getParameter("uf");
        String cep = request.getParameter("cep");
        String telefone = request.getParameter("telefone");
        String email = request.getParameter("email");

        if (razaoSocial.isEmpty() || cnpj.isEmpty() || endereco.isEmpty() || bairro.isEmpty() || 
                cidade.isEmpty() || uf.isEmpty() || cep.isEmpty() || telefone.isEmpty() || 
                email.isEmpty())    
            mensagem = "Preencha todos os campos";
        else if (razaoSocial.length() > 50)
            mensagem = "Razao Social deve conter no máximo 50 caracteres";
        else if (!validaCNPJ(cnpj)) 
            mensagem = "CNPJ incorreto!";
        else if (endereco.length() > 50)
            mensagem = "Estado deve conter no máximo 50 caracteres";
        else if (bairro.length() > 50)
            mensagem = "Bairro deve conter no máximo 50 caracteres";
        else if (cidade.length() > 50)
            mensagem = "Cidade deve conter no máximo 50 caracteres";
        else if (uf.length() > 2)
            mensagem = "UF deve conter no máximo 2 caracteres";
        else if (!validaCEP(cep))
            mensagem = "CEP incorreto!";
        else if (telefone.length() > 20)
            mensagem = "Telefone deve conter no máximo 20 caracteres";
        else if (email.length() > 50)
            mensagem = "Email deve conter no máximo 50 caracteres";
        else {
            
            fornecedor.setId(id);
            fornecedor.setRazaoSocial(razaoSocial);
            fornecedor.setCnpj(cnpj);
            fornecedor.setEndereco(endereco);
            fornecedor.setBairro(bairro);
            fornecedor.setCidade(cidade);
            fornecedor.setUf(uf);
            fornecedor.setCep(cep);
            fornecedor.setTelefone(telefone);
            fornecedor.setEmail(email);

            if (dao.gravar(fornecedor))
                mensagem = "Fornecedor gravado com sucesso";
            else 
                mensagem = "Erro ao gravar fornecedor";
        }
        //Enviando relação de fornecedores para fornecedores.jsp
        ArrayList<Fornecedores> fornecedores;       
        fornecedores = dao.getFornecedores();
        request.setAttribute("fornecedores", fornecedores);
        request.setAttribute("mensagem", mensagem);
        RequestDispatcher rd = request.getRequestDispatcher("/fornecedores.jsp");
        rd.forward(request, response);
    }      

        public static boolean validaCEP(String cep) {
            
            if (cep.length() > 9)
                return false;
            
            return true;
        }
        
        public static boolean validaCNPJ(String cnpj) {
            
            if (cnpj.length() > 18)
                return false;
            
            if ((cnpj == "00.000.000/0000-00") || (cnpj == "11.111.111/1111-11") || (cnpj == "22.222.222/2222-22") 
                || (cnpj == "33.333.333/3333-33") || (cnpj == "44.444.444/4444-44") || (cnpj == "55.555.555/5555-55") 
                || (cnpj == "66.666.666/6666-66") || (cnpj == "77.777.777/7777-77") || (cnpj == "88.888.888/8888-88") 
                || (cnpj == "99.999.999/9999-99"))
                return false;
            return true;
        }
     

}
