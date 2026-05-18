package DAL;

import Model.Departamento;
import Utils.Utils;

import java.io.*;
import java.util.ArrayList;

public class DepartamentoDAL {

    private static final String FICHEIRO_CSV = "csv/departamentos.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;sigla";

    private ArrayList<Departamento> listaDepartamentos;

    public DepartamentoDAL() {
        this.listaDepartamentos = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public Departamento adicionarDepartamento(Departamento departamento) {
        listaDepartamentos.add(departamento);
        guardarNoCSV();
        return departamento;
    }

    public boolean atualizarDepartamento(Departamento departamentoAtualizado) {
        for (int i = 0; i < listaDepartamentos.size(); i++) {
            if (listaDepartamentos.get(i).getSigla().equalsIgnoreCase(departamentoAtualizado.getSigla())) {
                listaDepartamentos.set(i, departamentoAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public void removerDepartamento(Departamento departamento) {
        listaDepartamentos.remove(departamento);
        guardarNoCSV();
    }

    public Departamento procurarPorSigla(String sigla) {
        for (Departamento d : listaDepartamentos) {
            if (d.getSigla().equalsIgnoreCase(sigla)) return d;
        }
        return null;
    }

    public ArrayList<Departamento> listarDepartamentos() {
        return new ArrayList<>(listaDepartamentos);
    }

    private void carregarDoCSV() {
        listaDepartamentos.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 2) continue;
            listaDepartamentos.add(new Departamento(campos[0], campos[1]));
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Departamento d : listaDepartamentos) {
                pw.println(d.getNome() + SEPARADOR + d.getSigla());
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar departamentos no CSV: " + e.getMessage());
        }
    }
}