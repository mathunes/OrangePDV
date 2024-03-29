package model;

import aplicacao.Vendas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(name = "VendasDAO", urlPatterns = {"/VendasDAO"})
public class VendasDAO extends HttpServlet {

    private Connection conexao;

    public VendasDAO() {
        try {
            conexao = Conexao.criaConexao();
        } catch (SQLException ex) {
            System.out.println("Erro na criação da conexao DAO: " + ex.getMessage());
        } 
    }
    
    public ArrayList<Vendas> getVendas() {
        ArrayList<Vendas> vendas = new ArrayList<>();
        
        try {
            Statement stmt = conexao.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM "
                    + "vendas as v, "
                    + "clientes as c, "
                    + "produtos as p, "
                    + "usuarios as u"
                + " WHERE "
                    + "v.id_cliente = c.id AND "
                    + "v.id_produto = p.id AND "
                    + "v.id_vendedor = u.id "
                + "ORDER BY v.id");
            
            while (rs.next()) {
                Vendas venda = new Vendas();
                
                venda.setDataVenda(rs.getString("v.data_venda"));
                venda.setId(rs.getInt("v.id"));
                venda.setIdCliente(rs.getInt("v.id_cliente"));
                venda.setIdProduto(rs.getInt("v.id_produto"));
                venda.setIdVendedor(rs.getInt("v.id_vendedor"));
                venda.setNomeCliente(rs.getString("c.nome"));
                venda.setNomeProduto(rs.getString("p.nome_produto"));
                venda.setNomeVendedor(rs.getString("u.nome"));
                venda.setQuantidadeVenda(rs.getInt("v.quantidade_venda"));
                venda.setValorVenda(rs.getDouble("v.valor_venda"));
                
                vendas.add(venda);
            }
            
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
        }
        
        return vendas;
    }
    
    public Vendas getVendaId(int id) {
        Vendas venda = new Vendas();
        
        try {
            String sql = "SELECT * FROM "
                    + "vendas as v, "
                    + "clientes as c, "
                    + "produtos as p, "
                    + "usuarios as u"
                + " WHERE "
                    + "v.id_cliente = c.id AND "
                    + "v.id_produto = p.id AND "
                    + "v.id_vendedor = u.id AND "
                    + "v.id = ?";
            
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                venda.setDataVenda(rs.getString("v.data_venda"));
                venda.setId(rs.getInt("v.id"));
                venda.setIdCliente(rs.getInt("v.id_cliente"));
                venda.setIdProduto(rs.getInt("v.id_produto"));
                venda.setIdVendedor(rs.getInt("v.id_vendedor"));
                venda.setNomeCliente(rs.getString("c.nome"));
                venda.setNomeProduto(rs.getString("p.nome_produto"));
                venda.setNomeVendedor(rs.getString("u.nome"));
                venda.setQuantidadeVenda(rs.getInt("v.quantidade_venda"));
                venda.setValorVenda(rs.getDouble("v.valor_venda"));
            }
            
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
        }
        
        return venda;
    }

    public ArrayList<Vendas> getVendaPesquisa(String nomeCliente) {
        ArrayList<Vendas> vendas = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM "
                    + "vendas as v, "
                    + "clientes as c, "
                    + "produtos as p, "
                    + "usuarios as u"
                + " WHERE "
                    + "v.id_cliente = c.id AND "
                    + "v.id_produto = p.id AND "
                    + "v.id_vendedor = u.id AND "
                    + "c.nome LIKE ?";
            
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, '%' + nomeCliente + '%');
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Vendas venda = new Vendas();
                
                venda.setDataVenda(rs.getString("v.data_venda"));
                venda.setId(rs.getInt("v.id"));
                venda.setIdCliente(rs.getInt("v.id_cliente"));
                venda.setIdProduto(rs.getInt("v.id_produto"));
                venda.setIdVendedor(rs.getInt("v.id_vendedor"));
                venda.setNomeCliente(rs.getString("c.nome"));
                venda.setNomeProduto(rs.getString("p.nome_produto"));
                venda.setNomeVendedor(rs.getString("u.nome"));
                venda.setQuantidadeVenda(rs.getInt("v.quantidade_venda"));
                venda.setValorVenda(rs.getDouble("v.valor_venda"));
                
                vendas.add(venda);
            }
                    
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
        }
        
        return vendas;
    }
    
    public boolean excluir(int id) {
        try {
            String sql = "DELETE FROM vendas WHERE id = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
            
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean gravar(Vendas venda) {
        
        try {
            String sql, sqlAtualizarQuantidade;
            
            if (venda.getId() == 0) {
                //String para inserir venda
                sql = "INSERT INTO vendas "
                        + "(quantidade_venda, data_venda, valor_venda, id_cliente, id_produto, id_vendedor) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                //String para atualizar venda
                sql = "UPDATE vendas SET "
                        + "quantidade_venda=?, data_venda=?, valor_venda=?, id_cliente=?, id_produto=?, id_vendedor=? "
                        + "WHERE id=?";
            }
            
            //Preparando string de inserção/atualização
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, venda.getQuantidadeVenda());
            ps.setString(2, venda.getDataVenda());
            ps.setDouble(3, venda.getValorVenda());
            ps.setInt(4, venda.getIdCliente());
            ps.setInt(5, venda.getIdProduto());
            ps.setInt(6, venda.getIdVendedor());
            
            //Se for atualização, inserir o sétimo parâmetro
            if (venda.getId() > 0)
                ps.setInt(7, venda.getId());
            
            //Executando inserção/atualização
            ps.execute();
            
            //Condicional para atualizar a quantidade do produto apenas na inserção
            if (venda.getId() == 0) {
                //String para pegar quantidade disponível do produto
                String sqlQuantidadeDisponivel = "SELECT quantidade_disponível FROM produtos WHERE id=?";
                PreparedStatement psQuantidadeDisponivel = conexao.prepareStatement(sqlQuantidadeDisponivel);  
                psQuantidadeDisponivel.setInt(1, venda.getIdProduto());
                ResultSet rs = psQuantidadeDisponivel.executeQuery();

                //String para atualizar quantidade disponível do produto
                sqlAtualizarQuantidade = "UPDATE produtos SET "
                        +"quantidade_disponível=? "
                        +"WHERE id=?";

                if(rs.next()){
                    int quantidadeDisponivel = rs.getInt("quantidade_disponível");
                    PreparedStatement psAtualizarQuantidade = conexao.prepareStatement(sqlAtualizarQuantidade);

                    //Atualizando quantidade disponivel com a quantidade disponivel atual menos a quantidade vendida
                    psAtualizarQuantidade.setInt(1, quantidadeDisponivel - venda.getQuantidadeVenda());
                    psAtualizarQuantidade.setInt(2, venda.getIdProduto());
                    psAtualizarQuantidade.execute();
                }
            }
            
            return true;
            
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
            
            return false;
        }
        
    }
    
    public ArrayList<String> getDatasVendas() {
        ArrayList<String> datas = new ArrayList<>();
        
        try {
            Statement stmt = conexao.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT data_venda FROM vendas");
            
            while (rs.next()) {
                String data = rs.getString("data_venda");
                
                datas.add(data);
            }
            
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
        }
        
        return datas;
    }
    
            
    public ArrayList<Vendas> getVendaData(String dataVenda) {
        ArrayList<Vendas> vendas = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM "
                    + "vendas as v, "
                    + "clientes as c, "
                    + "produtos as p, "
                    + "usuarios as u"
                + " WHERE "
                    + "v.id_cliente = c.id AND "
                    + "v.id_produto = p.id AND "
                    + "v.id_vendedor = u.id ";
            
            PreparedStatement ps;
            
            if (!dataVenda.equals("Escolher data")) {
                ps = conexao.prepareStatement(sql + " AND v.data_venda = ?");
                ps.setString(1, dataVenda);
            } else {
                ps = conexao.prepareStatement(sql);
            }
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Vendas venda = new Vendas();
                
                venda.setDataVenda(rs.getString("v.data_venda"));
                venda.setId(rs.getInt("v.id"));
                venda.setIdCliente(rs.getInt("v.id_cliente"));
                venda.setIdProduto(rs.getInt("v.id_produto"));
                venda.setIdVendedor(rs.getInt("v.id_vendedor"));
                venda.setNomeCliente(rs.getString("c.nome"));
                venda.setNomeProduto(rs.getString("p.nome_produto"));
                venda.setNomeVendedor(rs.getString("u.nome"));
                venda.setQuantidadeVenda(rs.getInt("v.quantidade_venda"));
                venda.setValorVenda(rs.getDouble("v.valor_venda"));
                
                vendas.add(venda);
            }
                    
        } catch (SQLException ex) {
            System.out.println("Erro de SQL: " + ex.getMessage());
        }
        
        return vendas;
    }
    
}
