package controller;

import aplicacao.Clientes;
import aplicacao.Produtos;
import aplicacao.Vendas;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ClientesDAO;
import model.VendasDAO;
import model.ProdutosDAO;

@WebServlet(name = "VendasController", urlPatterns = {"/VendasController"})
public class VendasController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
             
        VendasDAO daoVendas = new VendasDAO();
        ClientesDAO daoClientes = new ClientesDAO();
        ProdutosDAO daoProdutos = new ProdutosDAO();
        
        String acao = (String) request.getParameter("acao");
        ArrayList<Vendas> vendas;
        ArrayList<String> datas;
        int id;
        Vendas venda;
        
        ArrayList<Clientes> clientes;
        
        switch(acao) {
            //Requisição para exibir todas as vendas
            case "mostrar_vendas":
                vendas = daoVendas.getVendas();
                request.setAttribute("vendas", vendas);
                RequestDispatcher mostrarVendas = getServletContext().getRequestDispatcher("/vendas.jsp");
                mostrarVendas.forward(request, response);
                break;
                
            //Requisição para exibir todas as vendas e retornar para o administrador
            case "mostrar_vendas_adm":
                vendas = daoVendas.getVendas();
                request.setAttribute("vendas", vendas);
                
                datas = daoVendas.getDatasVendas();
                request.setAttribute("datas", datas);
                RequestDispatcher admMostrarVendas = getServletContext().getRequestDispatcher("/relatoriovendas.jsp");
                admMostrarVendas.forward(request, response);
                break;
                
            //Requisição para exibir a venda pelo id
            case "mostrar_venda":
                id = Integer.parseInt(request.getParameter("id"));
                venda = daoVendas.getVendaId(id);
                request.setAttribute("venda", venda);
                RequestDispatcher mostrarVenda = getServletContext().getRequestDispatcher("/venda.jsp");
                mostrarVenda.forward(request, response);
                break;
            
            //Requisição para exibir a venda pelo id e retornar para o administrador
            case "mostrar_venda_adm":
                id = Integer.parseInt(request.getParameter("id"));
                venda = daoVendas.getVendaId(id);
                request.setAttribute("venda", venda);
                RequestDispatcher admMostrarVenda = getServletContext().getRequestDispatcher("/vendaadm.jsp");
                admMostrarVenda.forward(request, response);
                break;
                
            //Requisição para exibir a venda pelo nome do cliente - usado no campo de busca
            case "mostrar_venda_busca":
                String busca = request.getParameter("busca");
                vendas = daoVendas.getVendaPesquisa(busca);
                request.setAttribute("vendas", vendas);
                RequestDispatcher mostrarVendasBusca = getServletContext().getRequestDispatcher("/vendas.jsp");
                mostrarVendasBusca.forward(request, response);
                break;
            
            case "mostrar_venda_data":
                vendas = daoVendas.getVendaData(request.getParameter("dataVenda"));
                datas = daoVendas.getDatasVendas();
                
                request.setAttribute("vendas", vendas);
                request.setAttribute("data", request.getParameter("dataVenda"));
                request.setAttribute("datas", datas);
                
                RequestDispatcher admMostrarVendasData = getServletContext().getRequestDispatcher("/relatoriovendas.jsp");
                admMostrarVendasData.forward(request, response);
                break;
                
            //Requisição para editar a venda pelo id
            case "editar_venda":
                id = Integer.parseInt(request.getParameter("id"));
                venda = daoVendas.getVendaId(id);
                
                Produtos produtoComprado = daoProdutos.getProdutoID(venda.getIdProduto());;
                clientes = daoClientes.getClientes();

                request.setAttribute("venda", venda);
                request.setAttribute("produto", produtoComprado);
                request.setAttribute("clientes", clientes);
                RequestDispatcher editarVenda = getServletContext().getRequestDispatcher("/formvenda.jsp");
                editarVenda.forward(request, response);
                break;
                
            //Requisição para excluir a venda pelo id
            case "excluir_venda":
                id = Integer.parseInt(request.getParameter("id"));
                if (daoVendas.excluir(id))
                    request.setAttribute("mensagem", "Venda excluída");
                else
                    request.setAttribute("mensagem", "Erro ao excluir venda");
                
                //Enviando relação de vendas para evitar o reload e perder a mensagem
                vendas = daoVendas.getVendas();
                request.setAttribute("vendas", vendas);
                    
                RequestDispatcher excluirVenda = getServletContext().getRequestDispatcher("/vendas.jsp");
                excluirVenda.forward(request, response);
                break;
                
            //Requisição para cadastrar a venda
            case "cadastrar_venda":
                int idProduto = Integer.parseInt(request.getParameter("produto"));
                if (!(idProduto > 0))
                    idProduto = 0;
                
                Produtos produto = daoProdutos.getProdutoID(idProduto);
                
                venda = new Vendas();
                
                venda.setId(0);
                venda.setIdCliente(0);
                venda.setIdProduto(idProduto);
                venda.setIdVendedor(0);
                venda.setNomeCliente("");
                venda.setNomeProduto("");
                venda.setNomeVendedor("");
                venda.setQuantidadeVenda(0);
                venda.setValorVenda(0);
                venda.setDataVenda("");
                
                clientes = daoClientes.getClientes();
                
                request.setAttribute("venda", venda);
                request.setAttribute("produto", produto);
                request.setAttribute("clientes", clientes);
                
                RequestDispatcher cadastrarVenda = getServletContext().getRequestDispatcher("/formvenda.jsp");
                cadastrarVenda.forward(request, response);
                break;
            
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
                
        String mensagem;
        
        Vendas venda = new Vendas();
        VendasDAO dao = new VendasDAO();
        
        int id = Integer.parseInt(request.getParameter("id"));
        int idProduto = Integer.parseInt(request.getParameter("idProduto"));
        int idVendedor = Integer.parseInt(request.getParameter("idVendedor"));
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String dataVenda = request.getParameter("dataVenda");
        int quantidade = Integer.parseInt(request.getParameter("quantidade"));
        Double desconto = Double.parseDouble(request.getParameter("desconto"));
        Double valorProduto = Double.parseDouble(request.getParameter("valorProduto"));
        Double valorVenda = Double.parseDouble(request.getParameter("valorTotal"));
        
        if (idProduto == 0 || idVendedor == 0 || idCliente == 0 || 
                dataVenda.isEmpty() || quantidade == 0 || valorVenda == 0)    
            mensagem = "Preencha todos os campos";
        else if (quantidade < 1)
            mensagem = "Deve haver pelo menos uma quantidade do produto escolhido";
        else if (valorVenda != ((valorProduto * quantidade) - desconto)) 
            mensagem = "Valor total inválido";
        else {
            
            venda.setId(id);
            venda.setDataVenda(dataVenda);
            venda.setIdCliente(idCliente);
            venda.setIdProduto(idProduto);
            venda.setIdVendedor(idVendedor);
            venda.setQuantidadeVenda(quantidade);
            venda.setValorVenda(valorVenda);

            if (dao.gravar(venda))
                mensagem = "Venda gravada com sucesso";
            else 
                mensagem = "Erro ao gravar venda";
        }
        
        //Enviando relação de vendas para vendas.jsp
        ArrayList<Vendas> vendas;
        vendas = dao.getVendas();
        request.setAttribute("vendas", vendas);
        request.setAttribute("mensagem", mensagem);
        RequestDispatcher rd = request.getRequestDispatcher("/vendas.jsp");
        rd.forward(request, response);
        
    }

}
