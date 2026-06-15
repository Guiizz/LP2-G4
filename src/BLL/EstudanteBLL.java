package BLL;

import DAL.AnoLetivoDAL;
import DAL.IAnoLetivoDAL;
import DAL.DocenteDAL;
import DAL.IDocenteDAL;
import DAL.EstudanteDAL;
import DAL.IEstudanteDAL;
import Model.*;
import Utils.Utils;
import Utils.ServicoEmail;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import Utils.PasswordUtils;

public class EstudanteBLL {
    private IEstudanteDAL estudanteDAL;
    private IDocenteDAL docenteDAL;
    private IAnoLetivoDAL anoLetivoDAL;

    public EstudanteBLL(IEstudanteDAL estudanteDAL, IDocenteDAL docenteDAL) {
        this(estudanteDAL, docenteDAL, new AnoLetivoDAL());
    }

    public EstudanteBLL(IEstudanteDAL estudanteDAL, IDocenteDAL docenteDAL, IAnoLetivoDAL anoLetivoDAL) {
        this.estudanteDAL = estudanteDAL;
        this.docenteDAL = docenteDAL;
        this.anoLetivoDAL = anoLetivoDAL;
    }

    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        Utils.validarNome(nome);
        Utils.validarDataNascimento(dataNascimento);
        Utils.validarNif(nif);
        Utils.validarMorada(morada);

        if (estudanteDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Já existe um estudante com este NIF!");
        }
        if (docenteDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Este NIF já existe no sistema como docente.");
        }

        Estudante novoEstudante = new Estudante(nome, dataNascimento, nif, morada);
        String passwordPlainText = "Issmf" + novoEstudante.getNumMecanografico();
        estudanteDAL.adicionarEstudante(novoEstudante);
        ServicoEmail.enviarCredenciais(
                novoEstudante.getEmail(),   // ← usa getter consistente
                passwordPlainText,
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
        return estudanteDAL.procurarPorNif(nif);
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
            throw new IllegalArgumentException("O estudante está no 3.º ano. Deve concluir o curso, não progredir de ano.");
        }

        validarRegrasAcademicas(estudante, obterInscricaoAtual(estudante), "progredir");
    }

    public void podeConcluirCurso(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        if (estudante.isConcluido()) {
            throw new IllegalArgumentException("O estudante já concluiu o curso.");
        }

        if (estudante.getAnoAtual() != 3) {
            throw new IllegalArgumentException("Só estudantes do 3.º ano podem concluir o curso.");
        }

        validarRegrasAcademicas(estudante, obterInscricaoAtual(estudante), "concluir o curso");
    }

    public void concluirCurso(Estudante estudante) {
        podeConcluirCurso(estudante);
        estudante.setEstado("CONCLUIDO");
        estudanteDAL.atualizarEstudante(estudante);
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

    public void desinscreverEstudante(Estudante estudante) {
        if (estudante == null)
            throw new IllegalArgumentException("O estudante não pode ser nulo.");

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);
        if (inscricaoAtual == null)
            throw new IllegalArgumentException("O estudante '" + estudante.getNome() + "' não tem inscrição ativa.");

        if (inscricaoAtual.getAvaliacoes() != null) {
            for (Avaliacao av : inscricaoAtual.getAvaliacoes()) {
                if (av.isLancada())
                    throw new IllegalArgumentException(
                            "Não é possível desinscrever: o estudante já tem notas lançadas.");
            }
        }

        estudante.getInscricoes().remove(inscricaoAtual);
        estudante.setAnoAtual(1);
        estudante.setEstado("ATIVO");
        estudanteDAL.atualizarEstudante(estudante);
    }

    public void marcarPropinaAtualComoPaga(String numMecanografico) {
        Estudante estudante = procurarPorNumMecanografico(numMecanografico);
        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        Utils.validarInscricaoComAnoLetivoECursoAtivos(
                inscricaoAtual,
                obterAnoLetivoDaInscricao(inscricaoAtual),
                "marcar a propina como paga"
        );

        inscricaoAtual.setPropinaPaga(true);
        estudanteDAL.atualizarEstudante(estudante);
    }

    public void pagarPropina(Estudante estudante, double valor) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante não tem inscrição ativa.");
        }

        Utils.validarCursoAtivo(inscricaoAtual.getCurso(), "pagar propinas");

        AnoLetivo anoLetivo = obterAnoLetivoDaInscricao(inscricaoAtual);
        if (anoLetivo == null || !anoLetivo.isAberto()) {
            throw new IllegalArgumentException(
                    "Não é possível pagar propinas: o ano letivo " +
                            inscricaoAtual.getAnoLetivo() + "/" + (inscricaoAtual.getAnoLetivo() + 1) +
                            " não está aberto.");
        }

        Propina propina = inscricaoAtual.getPropina();
        if (propina == null) {
            throw new IllegalArgumentException("A inscrição não tem propina associada.");
        }
        if (propina.isTotalmentePaga()) {
            throw new IllegalArgumentException("A propina deste ano já se encontra totalmente paga.");
        }

        propina.pagar(valor);
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
        lancarNotaMomento(estudante, null, indiceMomento, nota);
    }

    public void lancarNotaMomento(Estudante estudante, UnidadeCurricular uc, int indiceMomento, double nota) {
        Utils.validarNota(nota);

        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }

        Utils.validarIndiceMomento(indiceMomento, uc != null ? uc.getNome() : "desconhecida");

        if (uc != null) {
            if (!uc.isAtiva()) {
                throw new IllegalArgumentException("A UC '" + uc.getNome() + "' ainda não está ativa.");
            }

            if (uc.getMomentosAvaliacao() == null || uc.getMomentosAvaliacao().isEmpty()) {
                throw new IllegalArgumentException("A UC '" + uc.getNome() + "' não tem momentos de avaliação definidos.");
            }

            if (indiceMomento >= uc.getMomentosAvaliacao().size()) {
                throw new IllegalArgumentException(
                        "Momento inválido. A UC '" + uc.getNome() + "' só tem "
                                + uc.getMomentosAvaliacao().size() + " momento(s) de avaliação."
                );
            }
        }

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);
        if (inscricaoAtual == null) {
            throw new IllegalArgumentException("O estudante '" + estudante.getNome() + "' não tem inscrição ativa.");
        }

        ArrayList<Avaliacao> avaliacoes = inscricaoAtual.getAvaliacoes();
        if (avaliacoes == null) {
            avaliacoes = new ArrayList<>();
            inscricaoAtual.setAvaliacoes(avaliacoes);
        }

        Avaliacao avaliacao;
        if (uc != null) {
            // As notas são por UC: o índice do momento conta apenas dentro da própria UC,
            // para não colidir com momentos de outras UCs na mesma inscrição.
            ArrayList<Avaliacao> daUC = new ArrayList<>();
            for (Avaliacao a : avaliacoes) {
                if (a != null && a.getUc() != null && a.getUc().contains(uc)) daUC.add(a);
            }
            while (daUC.size() <= indiceMomento) {
                Avaliacao nova = criarAvaliacaoPendenteParaMomento(uc, daUC.size());
                avaliacoes.add(nova);
                daUC.add(nova);
            }
            avaliacao = daUC.get(indiceMomento);
        } else {
            // Compatibilidade: sem UC, usa posição global (comportamento antigo)
            while (avaliacoes.size() <= indiceMomento) {
                avaliacoes.add(criarAvaliacaoPendenteParaMomento(null, avaliacoes.size()));
            }
            avaliacao = avaliacoes.get(indiceMomento);
        }

        avaliacao.lancarNota(nota, nota >= 10);
        estudanteDAL.atualizarEstudante(estudante);
    }

    private AnoLetivo obterAnoLetivoDaInscricao(Inscricao inscricao) {
        if (inscricao == null) return null;
        return anoLetivoDAL.procurarPorAno(inscricao.getAnoLetivo());
    }

    private void validarRegrasAcademicas(Estudante estudante, Inscricao inscricaoAtual, String acao) {
        AnoLetivo anoLetivo = obterAnoLetivoDaInscricao(inscricaoAtual);

        Utils.validarInscricaoComAnoLetivoECursoAtivos(inscricaoAtual, anoLetivo, acao);
        Utils.validarPropinaPaga(inscricaoAtual, acao);
        Utils.validarNotasTodasLancadas(inscricaoAtual, acao);
        Utils.validarAproveitamentoMinimo(estudante.calcularAproveitamentoGlobal(), 0.60, acao);
    }

    private Avaliacao criarAvaliacaoPendenteParaMomento(UnidadeCurricular uc, int indiceMomento) {
        ArrayList<UnidadeCurricular> ucs = new ArrayList<>();
        double peso = 100.0;

        if (uc != null) {
            ucs.add(uc);
            if (uc.getMomentosAvaliacao() != null && indiceMomento < uc.getMomentosAvaliacao().size()) {
                peso = uc.getMomentosAvaliacao().get(indiceMomento).getPeso();
            }
        }

        return new Avaliacao(ucs, peso, new Date());
    }
}