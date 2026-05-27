package DAL;

import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Utils.PasswordUtils;
import Utils.Utils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class EstudanteDAL {

    private static final String FICHEIRO_CSV = "csv/estudantes.csv";
    private static final String FICHEIRO_INSCRICOES_CSV = "csv/inscricoes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO_ESTUDANTES = "nome;dataNascimento;nif;morada;numMecanografico;anoAtual;email;password;primeiroLogin;estado";
    private static final String CABECALHO_INSCRICOES = "numMecanografico;anoLetivo;anoDeCurso;nomeCurso;valorPago;notas";

    private ArrayList<Estudante> estudantes;
    private CursoDAL cursoDAL;

    public EstudanteDAL() {
        this.estudantes = new ArrayList<>();
        this.cursoDAL = new CursoDAL();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO_ESTUDANTES);
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_INSCRICOES_CSV, CABECALHO_INSCRICOES);
        carregarDoCSV();
        carregarInscricoesDoCSV();
    }

    public void adicionarEstudante(Estudante estudante) {
        estudantes.add(estudante);
        guardarNoCSV();
    }

    public ArrayList<Estudante> listarEstudantes() {
        return new ArrayList<>(estudantes);
    }

    public boolean atualizarEstudante(Estudante estudanteAtualizado) {
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getNumMecanografico().equals(estudanteAtualizado.getNumMecanografico())) {
                estudantes.set(i, estudanteAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public void removerEstudante(String numMecanografico) {
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getNumMecanografico().equals(numMecanografico)) {
                estudantes.remove(i);
                guardarNoCSV();
                return;
            }
        }
    }

    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        for (Estudante e : estudantes) {
            if (e.getNumMecanografico().equals(numMecanografico)) return e;
        }
        return null;
    }

    public Estudante procurarPorNif(String nif) {
        for (Estudante e : estudantes) {
            if (e.getNif().equals(nif)) return e;
        }
        return null;
    }

    public Estudante procurarPorEmail(String email) {
        for (Estudante e : estudantes) {
            if (e.getEmail().equalsIgnoreCase(email)) return e;
        }
        return null;
    }

    private void carregarDoCSV() {
        estudantes.clear();
        int maiorNumero = Estudante.getContadorSequencial();

        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 9) continue;
            try {
                String nome = campos[0];
                LocalDate dataNascimento = LocalDate.parse(campos[1]);
                String nif = campos[2];
                String morada = campos[3];
                String numMecanografico = campos[4];
                int anoAtual = Integer.parseInt(campos[5]);
                String email = campos[6];
                String password = campos[7];

                if (!PasswordUtils.estaHasheada(password)) {
                    password = PasswordUtils.hashPassword(password);
                }

                boolean primeiroLogin = Boolean.parseBoolean(campos[8]);
                String estado = campos.length >= 10 && !campos[9].isBlank() ? campos[9] : "ATIVO";

                Estudante estudante = new Estudante(nome, dataNascimento, nif, morada, true);
                estudante.setNumMecanografico(numMecanografico);
                estudante.setAnoAtual(anoAtual);
                estudante.setEmail(email);
                estudante.setPassword(password);
                estudante.setPrimeiroLogin(primeiroLogin);
                estudante.setEstado(estado);
                estudantes.add(estudante);

                try {
                    int numero = Integer.parseInt(numMecanografico);
                    if (numero >= maiorNumero) maiorNumero = numero + 1;
                } catch (NumberFormatException ignored) {}

            } catch (Exception e) {
                System.err.println("Erro ao carregar estudantes do CSV: " + e.getMessage());
            }
        }
        Estudante.setContadorSequencial(maiorNumero);
    }

    private void carregarInscricoesDoCSV() {
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_INSCRICOES_CSV, SEPARADOR)) {
            if (campos.length < 5) continue;

            try {
                String numMecanografico = campos[0];
                int anoLetivo = Integer.parseInt(campos[1]);
                int anoDeCurso = Integer.parseInt(campos[2]);
                String nomeCurso = campos[3];
                double valorPago;
                try {
                    valorPago = Double.parseDouble(campos[4]);
                } catch (NumberFormatException e) {
                    valorPago = Boolean.parseBoolean(campos[4]) ? -1 : 0;
                }
                String notas = campos.length >= 6 ? campos[5] : "";

                Estudante estudante = procurarPorNumMecanografico(numMecanografico);
                Curso curso = cursoDAL.procurarPorNome(nomeCurso);

                if (estudante == null || curso == null) continue;

                Inscricao inscricao = new Inscricao(anoLetivo, anoDeCurso, curso);
                if (valorPago == -1) {
                    inscricao.setPropinaPaga(true);
                } else if (valorPago > 0 && inscricao.getPropina() != null) {
                    try {
                        inscricao.getPropina().pagar(
                                Math.min(valorPago, inscricao.getPropina().getSaldoEmDebito())
                        );
                    } catch (IllegalArgumentException ignored) {}
                }
                inscricao.setAvaliacoes(deserializarAvaliacoes(notas));

                estudante.adicionarInscricao(inscricao);

            } catch (NumberFormatException e) {
                System.err.println("Erro ao carregar inscrições do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO_ESTUDANTES)) {
            for (Estudante estudante : estudantes) {
                pw.println(
                        estudante.getNome() + SEPARADOR +
                                estudante.getDataNascimento() + SEPARADOR +
                                estudante.getNif() + SEPARADOR +
                                estudante.getMorada() + SEPARADOR +
                                estudante.getNumMecanografico() + SEPARADOR +
                                estudante.getAnoAtual() + SEPARADOR +
                                estudante.getEmail() + SEPARADOR +
                                estudante.getPassword() + SEPARADOR +
                                estudante.isPrimeiroLogin() + SEPARADOR +
                                estudante.getEstado()
                );
            }
            guardarInscricoesNoCSV();
        } catch (IOException e) {
            System.err.println("Erro ao guardar estudantes no CSV: " + e.getMessage());
        }
    }

    private void guardarInscricoesNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_INSCRICOES_CSV, CABECALHO_INSCRICOES)) {
            for (Estudante estudante : estudantes) {
                if (estudante.getInscricoes() == null) continue;
                for (Inscricao inscricao : estudante.getInscricoes()) {
                    if (inscricao == null || inscricao.getCurso() == null) continue;
                    pw.println(
                            estudante.getNumMecanografico() + SEPARADOR +
                                    inscricao.getAnoLetivo() + SEPARADOR +
                                    inscricao.getAnoDeCurso() + SEPARADOR +
                                    inscricao.getCurso().getNomeCurso() + SEPARADOR +
                                    (inscricao.getPropina() != null ? inscricao.getPropina().getValorPago() : 0.0) + SEPARADOR +
                                    seRealizarAvaliacoes(inscricao.getAvaliacoes())
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar inscrições no CSV: " + e.getMessage());
        }
    }

    private String seRealizarAvaliacoes(ArrayList<Avaliacao> avaliacoes) {
        if (avaliacoes == null || avaliacoes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < avaliacoes.size(); i++) {
            Avaliacao avaliacao = avaliacoes.get(i);
            sb.append(avaliacao == null || !avaliacao.isLancada() ? "P" : avaliacao.getNota());
            if (i < avaliacoes.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private ArrayList<Avaliacao> deserializarAvaliacoes(String notas) {
        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
        if (notas == null || notas.isBlank()) return avaliacoes;
        for (String valor : notas.split(",")) {
            valor = valor.trim();
            if (valor.equalsIgnoreCase("P")) {
                avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new Date()));
            } else {
                try {
                    double nota = Double.parseDouble(valor);
                    avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new Date(), nota, nota >= 10));
                } catch (NumberFormatException ignored) {}
            }
        }
        return avaliacoes;
    }
}