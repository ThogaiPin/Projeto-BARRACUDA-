import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// -------------------------------------------------------------
// ENUMS
// -------------------------------------------------------------
enum CategoriaItem {
    MOVEL, ROUPA, ALIMENTO, OUTRO
}

enum StatusDoacao {
    PENDENTE, AGENDADA, EM_TRANSITO, CONCLUIDA, CANCELADA
}

// -------------------------------------------------------------
// HIERARQUIA DE USUÁRIOS
// -------------------------------------------------------------
abstract class Usuario {
    protected String id;
    protected String nome;
    protected String email;
    protected String telefone;
    protected String endereco;

    public Usuario(String id, String nome, String email, String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public void cadastrar() {
        System.out.println("Usuário " + nome + " cadastrado no sistema.");
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
}

class Doador extends Usuario {
    private List<Doacao> historicoDoacoes;

    public Doador(String id, String nome, String email, String telefone, String endereco) {
        super(id, nome, email, telefone, endereco);
        this.historicoDoacoes = new ArrayList<>();
    }

    public Doacao criarDoacao(String idDoacao, String enderecoColeta, List<ItemDoacao> itens) {
        Doacao novaDoacao = new Doacao(idDoacao, this, enderecoColeta);
        for (ItemDoacao item : itens) {
            novaDoacao.adicionarItem(item);
        }
        this.historicoDoacoes.add(novaDoacao);
        return novaDoacao;
    }
}

class Motorista extends Usuario {
    private String cnh;
    private boolean statusDisponibilidade;

    public Motorista(String id, String nome, String email, String telefone, String endereco, String cnh) {
        super(id, nome, email, telefone, endereco);
        this.cnh = cnh;
        this.statusDisponibilidade = true;
    }

    public void aceitarRota(RotaColeta rota) {
        System.out.println("Motorista " + getNome() + " aceitou a rota para a região: " + rota.getRegiao());
        this.statusDisponibilidade = false;
    }

    public void atualizarStatusColeta(Doacao doacao, StatusDoacao status) {
        doacao.setStatus(status);
        System.out.println("Doação " + doacao.getId() + " atualizada para: " + status);
    }

    public boolean isDisponivel() { return statusDisponibilidade; }
    public void setStatusDisponibilidade(boolean disponivel) { this.statusDisponibilidade = disponivel; }
}

// -------------------------------------------------------------
// DOAÇÃO E ITENS
// -------------------------------------------------------------
class ItemDoacao {
    private String id;
    private String descricao;
    private int quantidade;
    private CategoriaItem categoria;
    private String observacao;

    public ItemDoacao(String id, String descricao, int quantidade, CategoriaItem categoria, String observacao) {
        this.id = id;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.categoria = categoria;
        this.observacao = observacao;
    }

    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    public CategoriaItem getCategoria() { return categoria; }
}

class Doacao {
    private String id;
    private Date dataCriacao;
    private StatusDoacao status;
    private String enderecoColeta;
    private Doador doador;
    private List<ItemDoacao> itens;

    public Doacao(String id, Doador doador, String enderecoColeta) {
        this.id = id;
        this.doador = doador;
        this.enderecoColeta = enderecoColeta;
        this.dataCriacao = new Date();
        this.status = StatusDoacao.PENDENTE;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemDoacao item) {
        this.itens.add(item);
    }

    public String getId() { return id; }
    public StatusDoacao getStatus() { return status; }
    public void setStatus(StatusDoacao status) { this.status = status; }
    public List<ItemDoacao> getItens() { return itens; }
}

// -------------------------------------------------------------
// LOGÍSTICA
// -------------------------------------------------------------
class Veiculo {
    private String id;
    private String placa;
    private String modelo;
    private double capacidadeCargaKg;
    private boolean disponivel;

    public Veiculo(String id, String placa, String modelo, double capacidadeCargaKg) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.capacidadeCargaKg = capacidadeCargaKg;
        this.disponivel = true;
    }

    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}

class RotaColeta {
    private String id;
    private String regiao;
    private String status;
    private Motorista motorista;
    private Veiculo veiculo;
    private List<Doacao> doacoes;

    public RotaColeta(String id, String regiao) {
        this.id = id;
        this.regiao = regiao;
        this.status = "CRIADA";
        this.doacoes = new ArrayList<>();
    }

    public void adicionarDoacao(Doacao doacao) {
        this.doacoes.add(doacao);
        doacao.setStatus(StatusDoacao.AGENDADA);
    }

    public void finalizarRota() {
        this.status = "CONCLUIDA";
        if (this.veiculo != null) this.veiculo.setDisponivel(true);
        if (this.motorista != null) this.motorista.setStatusDisponibilidade(true);
    }

    public String getId() { return id; }
    public String getRegiao() { return regiao; }
    public void setMotorista(Motorista motorista) { this.motorista = motorista; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
}

class Instituicao {
    private String id;
    private String nome;
    private String cnpj;
    private String endereco;
    private List<Veiculo> veiculos;
    private List<RotaColeta> rotas;

    public Instituicao(String id, String nome, String cnpj, String endereco) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.veiculos = new ArrayList<>();
        this.rotas = new ArrayList<>();
    }

    public RotaColeta criarRota(String idRota, String regiao) {
        RotaColeta novaRota = new RotaColeta(idRota, regiao);
        this.rotas.add(novaRota);
        return novaRota;
    }

    public void alocarVeiculo(RotaColeta rota, Veiculo veiculo) {
        if (veiculo.isDisponivel()) {
            rota.setVeiculo(veiculo);
            veiculo.setDisponivel(false);
            System.out.println("Veículo " + veiculo.getPlaca() + " alocado para a rota " + rota.getId());
        } else {
            System.out.println("Veículo indisponível no momento.");
        }
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        this.veiculos.add(veiculo);
    }
}

// -------------------------------------------------------------
// CLASSE PRINCIPAL (EXECUTÁVEL)
// -------------------------------------------------------------
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 1. INICIALIZANDO O SISTEMA DA INSTITUIÇÃO ===");
        Instituicao instituicao = new Instituicao(
            "INST-01", 
            "ONG Mãos Amigas", 
            "12.345.678/0001-90", 
            "Rua Central, 100 - Centro"
        );

        Veiculo furgao = new Veiculo("VEIC-01", "ABC-1234", "Master Furgão", 1200.0);
        instituicao.adicionarVeiculo(furgao);
        System.out.println("Veículo cadastrado: " + furgao.getModelo() + " (" + furgao.getPlaca() + ")\n");

        System.out.println("=== 2. CADASTRO DE ATORES ===");
        Doador doador = new Doador("DOA-01", "Carlos Silva", "carlos@email.com", "(11) 98765-4321", "Rua das Flores, 45 - ZN");
        Motorista motorista = new Motorista("MOT-01", "João Souza", "joao@email.com", "(11) 91234-5678", "Av. Brasil, 500", "12345678900");
        
        doador.cadastrar();
        motorista.cadastrar();
        System.out.println();

        System.out.println("=== 3. CRIANDO UMA DOAÇÃO (COMPOSIÇÃO) ===");
        List<ItemDoacao> itensDoacao = new ArrayList<>();
        itensDoacao.add(new ItemDoacao("ITM-01", "Sofá 3 Lugares", 1, CategoriaItem.MOVEL, "Usado, mas em bom estado"));
        itensDoacao.add(new ItemDoacao("ITM-02", "Casacos de Inverno", 5, CategoriaItem.ROUPA, "Lavados e higienizados"));
        itensDoacao.add(new ItemDoacao("ITM-03", "Cestas Básicas", 2, CategoriaItem.ALIMENTO, "Dentro da validade"));

        Doacao doacaoCarlos = doador.criarDoacao("DOC-101", doador.getEndereco(), itensDoacao);
        
        System.out.println("Doação " + doacaoCarlos.getId() + " gerada por " + doador.getNome());
        System.out.println("Status inicial: " + doacaoCarlos.getStatus());
        System.out.println("Itens inclusos:");
        for (ItemDoacao item : doacaoCarlos.getItens()) {
            System.out.println(" - " + item.getQuantidade() + "x " + item.getDescricao() + " (" + item.getCategoria() + ")");
        }
        System.out.println();

        System.out.println("=== 4. LOGÍSTICA DE COLETA (AGREGAÇÃO) ===");
        RotaColeta rotaZonaNorte = instituicao.criarRota("ROTA-ZN-01", "Zona Norte");
        
        rotaZonaNorte.adicionarDoacao(doacaoCarlos);
        instituicao.alocarVeiculo(rotaZonaNorte, furgao);
        rotaZonaNorte.setMotorista(motorista);
        
        motorista.aceitarRota(rotaZonaNorte);
        System.out.println("Status da doação após agendamento: " + doacaoCarlos.getStatus() + "\n");

        System.out.println("=== 5. EXECUÇÃO E FINALIZAÇÃO ===");
        motorista.atualizarStatusColeta(doacaoCarlos, StatusDoacao.EM_TRANSITO);
        motorista.atualizarStatusColeta(doacaoCarlos, StatusDoacao.CONCLUIDA);

        rotaZonaNorte.finalizarRota();

        System.out.println("\n=== STATUS FINAL DA OPERAÇÃO ===");
        System.out.println("Doação " + doacaoCarlos.getId() + ": " + doacaoCarlos.getStatus());
        System.out.println("Veículo disponível novamente: " + furgao.isDisponivel());
        System.out.println("Motorista disponível novamente: " + motorista.isDisponivel());
    }
}