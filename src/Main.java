import Config.ModoPersistencia;
import Utils.Utils;
import View.LoginView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        escolherModoPersistencia();
        new LoginView().iniciar();
    }

    private static void escolherModoPersistencia() {
        Scanner scanner = new Scanner(System.in);
        String[] opcoes = {
                "Ficheiros",
                "Base de Dados (SQL Server)"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ISSMF - MODO DE FUNCIONAMENTO", opcoes, scanner);
        } while (opcao == 0);

        ModoPersistencia.setAtual(opcao == 2 ? ModoPersistencia.BASE_DADOS : ModoPersistencia.FICHEIRO);
    }
}