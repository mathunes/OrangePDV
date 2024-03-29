package controller;

import aplicacao.Categorias;
import aplicacao.Produtos;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.CategoriasDAO;
import model.ProdutosDAO;

//Tratamento dos produtos para área restrita
@WebServlet(name = "ProdutosController", urlPatterns = {"/ProdutosController"})
public class ProdutosController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ProdutosDAO dao = new ProdutosDAO();
        ArrayList<Produtos> produtos = dao.getProdutos();
        request.setAttribute("produtos", produtos);
        
        CategoriasDAO daoCategorias = new CategoriasDAO();
        ArrayList<Categorias> categorias = daoCategorias.getCategorias();
        request.setAttribute("categorias", categorias);

        Produtos produto;
        int id;
        String acaoRestrito = (String)request.getParameter("acaoRestrito");
        
        switch(acaoRestrito){
            
            //Enviar para usuário Comprador
            case "mostrar_produto_busca":
                String busca = request.getParameter("busca");
                produtos = dao.getProdutoPesquisa(busca);
                request.setAttribute("produtos", produtos);
                RequestDispatcher mostrarProdutosBusca = getServletContext().getRequestDispatcher("/produtoscomprador.jsp");
                mostrarProdutosBusca.forward(request, response);
                break;
                
            //Enviar para usuário Administrador
            case "mostrar_produto_busca_adm":
                String admBusca = request.getParameter("busca");
                produtos = dao.getProdutoPesquisa(admBusca);
                request.setAttribute("produtos", produtos);
                RequestDispatcher mostrarProdutosBuscaAdm = getServletContext().getRequestDispatcher("/relatorioestoque.jsp");
                mostrarProdutosBuscaAdm.forward(request, response);
                break;
            
            //Enviar para usuário Comprador
            case "mostrar_produto":
                id = Integer.parseInt(request.getParameter("id"));
                produto = dao.getProdutoID(id);
                request.setAttribute("produto", produto);
                RequestDispatcher mostrarProduto = getServletContext().getRequestDispatcher("/produto.jsp");
                mostrarProduto.forward(request, response);
                break;
                
            //Enviar para usuário Administrador
            case "mostrar_produto_adm":
                id = Integer.parseInt(request.getParameter("id"));
                produto = dao.getProdutoID(id);
                request.setAttribute("produto", produto);
                RequestDispatcher mostrarProdutoAdm = getServletContext().getRequestDispatcher("/produtoadm.jsp");
                mostrarProdutoAdm.forward(request, response);
                break;
                
            //Enviar para usuário Comprador
            case "mostrar_produtos":
                RequestDispatcher rd = request.getRequestDispatcher("/produtoscomprador.jsp");
                rd.forward(request, response);
                break;
            
            //Enviar para usuário Vendedor
            case "mostrar_produtos_restrito":
                RequestDispatcher rdRestrito = request.getRequestDispatcher("/produtos.jsp");
                rdRestrito.forward(request, response);
                break;

            //Enviar para usuário Administrador
            case "mostrar_produtos_restrito_adm":
                RequestDispatcher rdRestritoAdm = request.getRequestDispatcher("/relatorioestoque.jsp");
                rdRestritoAdm.forward(request, response);
                break;
                
            case "pesquisar_produtos_restrito":
                String nomeProduto = request.getParameter("nomeProduto");
                produtos = dao.getNomeProduto(nomeProduto);
                request.setAttribute("produtos", produtos);
                RequestDispatcher mostrarNomeProduto = getServletContext().getRequestDispatcher("/produtos.jsp");
                mostrarNomeProduto.forward(request, response);
                break;
                
            //Requisição para editar o produto pelo id
            case "editar_produto":
                id = Integer.parseInt(request.getParameter("id"));
                produto = dao.getProdutoID(id);
                
                categorias = daoCategorias.getCategorias();

                request.setAttribute("produto", produto);
                request.setAttribute("categorias", categorias);
                RequestDispatcher editarCompra = getServletContext().getRequestDispatcher("/formproduto.jsp");
                editarCompra.forward(request, response);
                break;
                
            //Requisição para excluir o produto pelo id
            case "excluir_produto":
                id = Integer.parseInt(request.getParameter("id"));
                if (dao.excluir(id))
                    request.setAttribute("mensagem", "Produto excluído");
                else
                    request.setAttribute("mensagem", "Esse produto está sendo referenciado em alguma compra/venda. Por favor, retire a referência e tente excluí-lo novamente.");
                
                //Enviando relação de produtos para evitar o reload e perder a mensagem
                produtos = dao.getProdutos();
                request.setAttribute("produtos", produtos);
                    
                RequestDispatcher excluirProduto = getServletContext().getRequestDispatcher("/produtoscomprador.jsp");
                excluirProduto.forward(request, response);
                break;
                
            case "cadastrar_produto":
                int idProduto = Integer.parseInt(request.getParameter("produto"));
                if (!(idProduto > 0))
                    idProduto = 0;
                
                produto = new Produtos();
                
                produto.setId(0);
                produto.setNomeProduto("");
                produto.setDescricao("");
                produto.setPrecoCompra(0);
                produto.setPrecoVenda(0);
                produto.setQuantidadeDisponivel(0);
                produto.setLiberadoVenda("N");
                produto.setIdCategoria(0);
                
                request.setAttribute("produto", produto);
                request.setAttribute("categorias", categorias);
                
                RequestDispatcher cadastrarProduto = getServletContext().getRequestDispatcher("/formproduto.jsp");
                cadastrarProduto.forward(request, response);
                break;
            
            //Enviar para usuário Comprador
            case "liberar_produto":
                id = Integer.parseInt(request.getParameter("id"));
                String bloquear = request.getParameter("bloquear");
                if (dao.liberar(id, bloquear))
                    request.setAttribute("mensagem", "Produto " + ((bloquear.equals("S")) ? "bloqueado" : "liberado") + " para venda");
                else
                    request.setAttribute("mensagem", "Erro ao liberar produto");
                
                //Enviando relação de produtos para evitar o reload e perder a mensagem
                produtos = dao.getProdutos();
                request.setAttribute("produtos", produtos);
                    
                RequestDispatcher liberarProduto = getServletContext().getRequestDispatcher("/produtoscomprador.jsp");
                liberarProduto.forward(request, response);
                break;
            
            //Enviar para usuário Administrador
            case "liberar_produto_adm":
                id = Integer.parseInt(request.getParameter("id"));
                String admBloquear = request.getParameter("bloquear");
                if (dao.liberar(id, admBloquear))
                    request.setAttribute("mensagem", "Produto " + ((admBloquear.equals("S")) ? "bloqueado" : "liberado") + " para venda");
                else
                    request.setAttribute("mensagem", "Erro ao liberar produto");
                
                //Enviando relação de produtos para evitar o reload e perder a mensagem
                produtos = dao.getProdutos();
                request.setAttribute("produtos", produtos);
                    
                RequestDispatcher liberarProdutoAdm = getServletContext().getRequestDispatcher("/relatorioestoque.jsp");
                liberarProdutoAdm.forward(request, response);
                break;
                
            }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        String mensagem;
        
        Produtos produto = new Produtos();
        ProdutosDAO dao = new ProdutosDAO();
        CategoriasDAO daoCategorias = new CategoriasDAO();
        
        int id = Integer.parseInt(request.getParameter("id"));
        String nomeProduto = request.getParameter("nomeProduto");
        String descricao = request.getParameter("descricao");
        Double precoCompra = Double.parseDouble(request.getParameter("precoCompra"));
        Double precoVenda = Double.parseDouble(request.getParameter("precoVenda"));
        int quantidadeDisponivel = Integer.parseInt(request.getParameter("quantidadeDisponivel"));
        String liberadoVenda = request.getParameter("liberadoVenda");
        int idCategoria = Integer.parseInt(request.getParameter("idCategoria"));
        
        if (idCategoria == 0 || nomeProduto.isEmpty() || 
                descricao.isEmpty() || liberadoVenda.isEmpty())    
            mensagem = "Preencha todos os campos";
        else if ((!liberadoVenda.equals("S")) && (!liberadoVenda.equals("N"))) 
            mensagem = "Valor do campo Liberado Venda inválido";
        else {
            
            produto.setId(id);
            produto.setNomeProduto(nomeProduto);
            produto.setDescricao(descricao);
            produto.setPrecoCompra(precoCompra);
            produto.setPrecoVenda(precoVenda);
            produto.setQuantidadeDisponivel(quantidadeDisponivel);
            produto.setLiberadoVenda(liberadoVenda);
            produto.setIdCategoria(idCategoria);

            if (dao.gravar(produto))
                mensagem = "Produto gravado com sucesso";
            else 
                mensagem = "Erro ao gravar produto";
        }
        
        //Enviando relação de produtos para produtoscomprador.jsp
        ArrayList<Produtos> produtos;
        produtos = dao.getProdutos();
        request.setAttribute("produtos", produtos);
        request.setAttribute("mensagem", mensagem);
        RequestDispatcher rd = request.getRequestDispatcher("/produtoscomprador.jsp");
        rd.forward(request, response);
        
    }

}
