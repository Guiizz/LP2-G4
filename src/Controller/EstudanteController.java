package Controller;

import BLL.EstudanteBLL;
import Model.Estudante;
import Model.Inscricao;
import Model.UnidadeCurricular;

import java.time.LocalDate;
import java.util.ArrayList;

public class EstudanteController {

    private final EstudanteBLL estudanteBLL;
    public EstudanteController(EstudanteBLL estudanteBLL) {
        this.estudanteBLL = estudanteBLL;
    }

    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        return estudanteBLL.registarEstudante(nome, dataNascimento, nif, morada);
    }
    public ArrayList<Estudante> listarEstudante() {
        return estudanteBLL.listarEstudante();
    }

    public void atualizarEstudante(String numMecanografico, String novoNome, String novaMorada) {
        estudanteBLL.atualizarEstudante(numMecanografico, novoNome, novaMorada);
    }

    public void atualizarMoradaPropria(Estudante estudante, String novaMorada) {
        estudanteBLL.atualizarMoradaPropria(estudante, novaMorada);
    }

    public void removerEstudante(String numMecanografico) {
        estudanteBLL.removerEstudante(numMecanografico);
    }

    public Estudante procurarPorNumMecanografico(String num) {
        return estudanteBLL.procurarPorNumMecanografico(num);
    }

    public Estudante procurarPorNif(String nif) {
        return estudanteBLL.procurarPorNif(nif);
    }

    public Estudante autenticarEstudante(String email, String password) {
        return estudanteBLL.autenticarEmail(email, password);
    }

    public void alterarPassword(Estudante estudante, String novaPassword) {
        estudanteBLL.alterarPassword(estudante, novaPassword);
    }

    public void recuperarPassword(String email) {
        estudanteBLL.recuperarPassword(email);
    }
    public void guardarEstadoEstudante(Estudante estudante) {
        estudanteBLL.guardarEstadoEstudante(estudante);
    }
    public Inscricao obterInscricaoAtual(Estudante estudante) {
        return estudanteBLL.obterInscricaoAtual(estudante);
    }

    public void marcarPropinaAtualComoPaga(String numMecanografico) {
        estudanteBLL.marcarPropinaAtualComoPaga(numMecanografico);
    }

    public ArrayList<Estudante> listarComPropinaEmDivida() {
        return estudanteBLL.listarComPropinaEmDivida();
    }
    public void registarNotaNaInscricaoAtual(String numMecanografico, double nota) {
        estudanteBLL.registarNotaNaInscricaoAtual(numMecanografico, nota);
    }

    public void lancarNotaMomento(Estudante estudante, int indiceMomento, double nota) {
        estudanteBLL.lancarNotaMomento(estudante, indiceMomento, nota);
    }
    public void lancarNotaMomento(Estudante estudante, UnidadeCurricular uc, int indiceMomento, double nota) {
        estudanteBLL.lancarNotaMomento(estudante, uc, indiceMomento, nota);
    }
    public void concluirCurso(Estudante estudante) {
        estudanteBLL.concluirCurso(estudante);
    }

    public void pagarPropina(Estudante estudante, double valor) {
        estudanteBLL.pagarPropina(estudante, valor);
    }

    public boolean nifJaExiste(String nif) {
        try {
            return estudanteBLL.procurarPorNif(nif) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}