package BLL;

import DAL.EstudanteDAL;
import Model.Avaliacao;
import Model.Estudante;
import Model.Inscricao;
import Model.Propina;
import Utils.Utils;
import Utils.ServicoEmail;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import Utils.PasswordUtils;

public class EstudanteBLL {
    private EstudanteDAL estudanteDAL;

    public EstudanteBLL(EstudanteDAL estudanteDAL) {
        this.estudanteDAL = estudanteDAL;
    }

    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        Utils.validarNome(nome);
        Utils.validarDataNascimento(dataNascimento);
        Utils.validarNif(nif);
        Utils.validarMorada(morada);

        if (estudanteDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Já existe um estudante com este NIF!");
        }

        Estudante novoEstudante = new Estudante(nome, dataNascimento, nif, morada);
        estudanteDAL.adicionarEstudante(novoEstudante);

        ServicoEmail.enviarCredenciais(
                novoEstudante.getNumMecanografico() + "@issmf.pt",
                "Issmf" + novoEstudante.getNumMecanografico(),
                "Estudante"
        );

        return novoEstudante;
    }

    public ArrayList<Estudante> listarEstudante() {
        return estudanteDAL.listarEstudantes();
    }

    public void atualizarEstudante(String numMecanografico, String novoNome, String novaMorada) {
        if (numMecanografico == null || numMecanografico.trim().isEmpty()) {
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);

        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }

        if (!estudante.getInscricoes().isEmpty()) {
            throw new IllegalArgumentException("Não é possível alterar os dados de um estudante com inscrições ativas.");
        }

        Utils.validarNome(novoNome);
        Utils.validarMorada(novaMorada);

        estudante.setNome(novoNome);
        estudante.setMorada(novaMorada);

        estudanteDAL.atualizarEstudante(estudante);
    }

    public void atualizarMoradaPropria(Estudante estudante, String novaMorada) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        Utils.validarMorada(novaMorada);

        estudante.setMorada(novaMorada);
        estudanteDAL.atualizarEstudante(estudante);
    }

    public void removerEstudante(String numMecanografico) {
        if (numMecanografico == null || numMecanografico.trim().isEmpty()) {
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);

        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }

        if (!estudante.getInscricoes().isEmpty()) {
            throw new IllegalArgumentException("Não é possível remover um estudante com inscrições ativas.");
        }

        estudanteDAL.removerEstudante(numMecanografico);
    }

    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        if (numMecanografico == null || numMecanografico.trim().isEmpty()) {
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);

        if (estudante == null) {
            throw new IllegalArgumentException("Estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }

        return estudante;
    }

    public Estudante procurarPorNif(String nif) {
        Utils.validarNif(nif);

        Estudante estudante = estudanteDAL.procurarPorNif(nif);

        if (estudante == null) {
            throw new IllegalArgumentException("Estudante não encontrado com o nif: " + nif);
        }

        return estudante;
    }

    public Estudante autenticarEmail(String email, String password) {
        Utils.validarEmail(email);
        Utils.validarPassword(password);

        ArrayList<Estudante> estudantes = estudanteDAL.listarEstudantes();

        for (Estudante e : estudantes) {
            if (e.getEmail().equalsIgnoreCase(email) && PasswordUtils.verificarPassword(password, e.getPassword())) {
                return e;
            }
        }

        throw new IllegalArgumentException("E-mail ou Palavra-passe inválido!");
    }

    public void alterarPassword(Estudante estudante, String novaPassword) {
        Utils.validarPassword(novaPassword);

        estudante.setPassword(PasswordUtils.hashPassword(novaPassword));
        estudante.setPrimeiroLogin(false);

        estudanteDAL.atualizarEstudante(estudante);
    }

    public void podeProgredirAno(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        if (estudante.isConcluido()) {
            throw new IllegalArgumentException("O estudante já concluiu o curso.");
        }

        if (estudante.getAnoAtual() >= 3) {
            throw new IllegalArgumentException("O estudante já se encontra no último ano do curso.");
        }

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante não tem inscrição ativa.");
        }

        if (!inscricaoAtual.isPropinaPaga()) {
            throw new IllegalArgumentException("O estudante não pode progredir porque tem propina em dívida.");
        }

        if (inscricaoAtual.temNotasPorLancar()) {
            throw new IllegalArgumentException("O estudante não pode progredir porque existem notas por lançar.");
        }

        double percentagemAprovacao = estudante.calcularAproveitamentoGlobal();

        if (percentagemAprovacao < 0.60) {
            throw new IllegalArgumentException(
                    "O estudante não pode progredir. Aprovação global (incluindo UCs em atraso): " +
                            String.format("%.1f", percentagemAprovacao * 100) +
                            "%. Mínimo necessário: 60%."
            );
        }
    }

    public void passarDeAno(Estudante estudante, Inscricao novaInscricao) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        if (novaInscricao == null) {
            throw new IllegalArgumentException("A nova inscrição não pode ser nula.");
        }

        podeProgredirAno(estudante);

        if (novaInscricao.getAnoDeCurso() != estudante.getAnoAtual() + 1) {
            throw new IllegalArgumentException("A nova inscrição tem de corresponder ao ano seguinte.");
        }

        estudante.setAnoAtual(estudante.getAnoAtual() + 1);
        estudante.adicionarInscricao(novaInscricao);

        estudanteDAL.atualizarEstudante(estudante);
    }

    public void recuperarPassword (String email) {
        Estudante estudante = null;
        for (Estudante e : estudanteDAL.listarEstudantes()){
            if (e.getEmail().equalsIgnoreCase(email)){
                estudante = e;
                break;
            }
        }
        if (estudante == null) {
            throw new IllegalArgumentException(("Não existe nenhum estudante com esse e-mail."));
        }
        String passwordTemporaria = "Issmf" + estudante.getNumMecanografico() + "Tmp";
        estudante.setPassword(PasswordUtils.hashPassword(passwordTemporaria));
        estudante.setPrimeiroLogin(true);
        estudanteDAL.atualizarEstudante(estudante);
        ServicoEmail.enviarPasswordTemporaria(email, passwordTemporaria, "Estudante");
    }

    public void guardarEstadoEstudante(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        estudanteDAL.atualizarEstudante(estudante);
    }

    public Inscricao obterInscricaoAtual(Estudante estudante) {
        if (estudante == null || estudante.getInscricoes() == null || estudante.getInscricoes().isEmpty()) {
            return null;
        }

        return estudante.getInscricoes().get(estudante.getInscricoes().size() - 1);
    }

    public void marcarPropinaAtualComoPaga(String numMecanografico) {
        Estudante estudante = procurarPorNumMecanografico(numMecanografico);
        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante não tem inscrição ativa.");
        }

        inscricaoAtual.setPropinaPaga(true);
        estudanteDAL.atualizarEstudante(estudante);
    }

    public ArrayList<Estudante> listarComPropinaEmDivida() {
        ArrayList<Estudante> resultado = new ArrayList<>();

        for (Estudante estudante : estudanteDAL.listarEstudantes()) {
            Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

            if (inscricaoAtual != null && !inscricaoAtual.isPropinaPaga()) {
                resultado.add(estudante);
            }
        }

        return resultado;
    }

    public void registarNotaNaInscricaoAtual(String numMecanografico, double nota) {
        Utils.validarNota(nota);

        Estudante estudante = procurarPorNumMecanografico(numMecanografico);
        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante não tem inscrição ativa.");
        }

        Avaliacao avaliacao = new Avaliacao(new ArrayList<>(), 100, new Date(), nota, nota >= 10);

        inscricaoAtual.adicionarAvaliacao(avaliacao);
        estudanteDAL.atualizarEstudante(estudante);
    }

    public void lancarNotaMomento(Estudante estudante, int indiceMomento, double nota) {
        Utils.validarNota(nota);

        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);
        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante '" + estudante.getNome() + "' não tem inscrição ativa.");
        }

        ArrayList<Avaliacao> avaliacoes = inscricaoAtual.getAvaliacoes();
        if (avaliacoes == null || indiceMomento < 0 || indiceMomento >= avaliacoes.size()) {
            throw new IllegalArgumentException("Momento de avaliação inválido para '" + estudante.getNome() + "'.");
        }

        avaliacoes.get(indiceMomento).lancarNota(nota, nota >= 10);
        estudanteDAL.atualizarEstudante(estudante);
    }

    public void pagarPropina(Estudante estudante, double valor) {
        if (estudante == null)
            throw new IllegalArgumentException("O estudante não pode ser nulo.");

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);
        if (inscricaoAtual == null)
            throw new IllegalArgumentException("O estudante não tem inscrição ativa.");

        Propina propina = inscricaoAtual.getPropina();
        if (propina == null)
            throw new IllegalArgumentException("A inscrição não tem propina associada.");
        if (propina.isTotalmentePaga())
            throw new IllegalArgumentException("A propina deste ano já se encontra totalmente paga.");

        propina.pagar(valor);
        estudanteDAL.atualizarEstudante(estudante);
    }
}