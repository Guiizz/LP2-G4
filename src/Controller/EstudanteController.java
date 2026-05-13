package Controller;

import BLL.EstudanteBLL;
import DAL.EstudanteDAL;
import Model.Estudante;
import Model.Inscricao;

import java.time.LocalDate;
import java.util.ArrayList;

public class EstudanteController {

    private final EstudanteBLL estudanteBLL;

    public EstudanteController() {
        this.estudanteBLL = new EstudanteBLL(new EstudanteDAL());
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

    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        return estudanteBLL.procurarPorNumMecanografico(numMecanografico);
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

    public void verificarProgressaoAno(Estudante estudante) {
        estudanteBLL.podeProgredirAno(estudante);
    }

    public void passarAno(Estudante estudante, Inscricao novaInscricao) {
        estudanteBLL.passarDeAno(estudante, novaInscricao);
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
}

