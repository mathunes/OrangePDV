package aplicacao;

public class Categorias {

    private int id;
    private String nomeCategoria;

    public Categorias(int id, String nomeCategoria) {
        this.id = id;
        this.nomeCategoria = nomeCategoria;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }
    
}