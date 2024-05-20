import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Solicitar as dimensões da matriz
        String sizeStr = JOptionPane.showInputDialog(null, "Digite as dimensões da matriz (MxN):");
        String[] sizeParts = sizeStr.split("x"); // Separar os valores, por meio do split(Devo passar algo similar a MxN)
        int numRows = Integer.parseInt(sizeParts[0].trim()); //N de linhas
        int numCols = Integer.parseInt(sizeParts[1].trim()); //N de colunas

        // Iniciar o aplicativo com as dimensões da matriz fornecidas //Inicializando a interface Runnable
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MatrixFiller(numRows, numCols).setVisible(true);// Nesse objeto temos de passar a linha e coluna, além de habilitar a visibilidade do objeto
            }
        });
    }
}
