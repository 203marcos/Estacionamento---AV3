import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class MatrixFiller extends JFrame {
    //Objeto herdando o JFrame, uma classe responsavel para incializar a interfaceGrafica. Pertence a biblioteca java.swing
    private int numRows; //Numero de linhas da matriz
    private int numCols; //Numero de colunas da matriz
    private JLabel[][] matrixLabels; //Usado para representar a matriz bidimensional na interfaceGrafica
    private JTextField coordInputField; //Usado para declarar o campo a onde o usuario podera escrever as coordenadas
    private JTextField plateInputField; //Usado para declarar o campo a onde o usuario podera escrever as placas
    private JTextField timeInputField; //Usado para declarar o campo a onde o usuario podera escrever o tempo de entrada e saida
    private JLabel statusLabel; //Declara um espaço a onde as mensagem serão exibidas
    private Map <Point, CarInfo> matrixData; //Usado para armazenar o ponto (posição) e o dado do carro (CarInfo),  utiliza a interface Map para especificar o tipo de dados.


    public MatrixFiller(int numRows, int numCols) {
        //incializando o objeto MatrixFiller...
        this.numRows = numRows; //Repassando a linha dada anteriormente
        this.numCols = numCols; //Repassando a coluna dada anteriormente

        setTitle("ESTACIONAMENTO"); //Titulo da interfaceGrafica
        setSize(900, 400); //Dimensões da interface

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Indica que o programa encerra ao quando o usuario clicar no botão de fechar
        setLayout(new BorderLayout()); //Define o gerenciador de layout... 5 areas de componentes (Norte, Sul, Leste, Oeste e centro)

        matrixData = new HashMap<>(); //Iniciação do HashMap

        //Painel da matriz
        JPanel matrixPanel = new JPanel(new GridLayout(numRows, numCols)); //Criação de um painel com layout de grade (grid), ele organiza em linhas e colunas
        matrixLabels = new JLabel[numRows][numCols]; //Apenas criando uma matriz do tipo JLabel

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrixLabels[i][j] = new JLabel("[ ]", SwingConstants.CENTER); //Criando o bjeto dentro da matriz e definindo como '[ ]' e o segundo argumento especifica que deve ser centralizado horizontalmente dentro do rotulo
                matrixLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));//Apenas a borda
                matrixPanel.add(matrixLabels[i][j]); //Aqui você está adicionando o rótulo atual ao painel...Isso coloca o rótulo na posição correspondente na grade.
            }
        }

        add(matrixPanel, BorderLayout.CENTER); //Voce está adicionando o painel (matrixPainel) ao MatrixFiller (JFrame)

        //Painel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        coordInputField = new JTextField(5);
        plateInputField = new JTextField(10);
        timeInputField = new JTextField(15);

        JButton fillButton = new JButton("Preencher");
        JButton removeButton = new JButton("Retirar");

        fillButton.addActionListener(new FillButtonListener());
        removeButton.addActionListener(new RemoveButtonListener());

        statusLabel = new JLabel(" ");

        inputPanel.add(new JLabel("Coordenada (x,y):"));
        inputPanel.add(coordInputField);
        inputPanel.add(new JLabel("Placa do Carro (LLLNoLNoNo):"));
        inputPanel.add(plateInputField);
        inputPanel.add(new JLabel("Horário (HH:MM a HH:MM):"));
        inputPanel.add(timeInputField);
        inputPanel.add(fillButton);
        inputPanel.add(removeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private class FillButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String coordInput = coordInputField.getText();
            String plateInput = plateInputField.getText();
            String timeInput = timeInputField.getText();

            if (!validatePlate(plateInput)) {
                statusLabel.setText("Placa inválida. Use o formato LLLNoLNoNo.");
                return;
            }

            if (!validateTime(timeInput)) {
                statusLabel.setText("Horário inválido. Use o formato HH:MM a HH:MM.");
                return;
            }

            try {
                String[] parts = coordInput.split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());

                if (x >= 0 && x < numRows && y >= 0 && y < numCols) {
                    Point point = new Point(x, y);
                    if (!matrixData.containsKey(point)) {
                        matrixLabels[x][y].setText("[X]");
                        matrixData.put(point, new CarInfo(plateInput, timeInput));
                        statusLabel.setText("Posição preenchida: (" + x + "," + y + ") com placa " + plateInput + " e horário " + timeInput);
                    } else {
                        statusLabel.setText("Posição (" + x + "," + y + ") já está preenchida.");
                    }
                } else {
                    statusLabel.setText("Coordenadas fora dos limites da matriz.");
                }
            } catch (Exception ex) {
                statusLabel.setText("Entrada inválida. Use o formato x,y.");
            }
        }
    }

    private class RemoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String coordInput = coordInputField.getText();
            try {
                String[] parts = coordInput.split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());

                if (x >= 0 && x < numRows && y >= 0 && y < numCols) {
                    Point point = new Point(x, y);
                    if (matrixData.containsKey(point)) {
                        CarInfo carInfo = matrixData.remove(point);
                        matrixLabels[x][y].setText("[ ]");
                        String state = getStateFromPlate(carInfo.getPlate());

                        //Calculando tempo de permanencia
                        long parkingTime = calculateParkingTime(carInfo.getTime());

                        //Calculando valor a ser cobrado
                        double totalCharge = calculateParkingCharge(parkingTime);

                        statusLabel.setText("Posição limpa: (" + x + "," + y + "). Carro de " + state + " com placa " + carInfo.getPlate() + " e horário " + carInfo.getTime() +
                                ". Tempo de permanência: " + parkingTime + " minutos. Valor a ser cobrado: R$" + String.format("%.2f", totalCharge));
                    } else {
                        statusLabel.setText("Posição (" + x + "," + y + ") já está vazia.");
                    }
                } else {
                    statusLabel.setText("Coordenadas fora dos limites da matriz.");
                }
            } catch (Exception ex) {
                statusLabel.setText("Entrada inválida. Use o formato x,y.");
            }
        }
    }

    private boolean validatePlate(String plate) {
        //Representa a validação da placa, {x} representa a quantidade de vezes que aparece em sequencia. Matches usado para essa comparação
        return plate.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
    }

    private boolean validateTime(String time) {
        //Representa a validação do tempo, {x} representa a quantidade de vezes que aparece em sequencia
        return time.matches("[0-9]{2}:[0-9]{2} a [0-9]{2}:[0-9]{2}");
    }

    private String getStateFromPlate(String plate) {
        //Sabe onde a placa foi confecionada, de acordo com as 3 letras iniciais da placa "ABC..."
        String prefix = plate.substring(0, 3);
        if (prefix.compareTo("AAA") >= 0 && prefix.compareTo("BEZ") <= 0) return "Paraná"; //Essa representa a placa do Parana de AAA até BEZ, e assim por diante
        if (prefix.compareTo("BFA") >= 0 && prefix.compareTo("GKI") <= 0) return "São Paulo";
        if (prefix.compareTo("GKJ") >= 0 && prefix.compareTo("HOK") <= 0) return "Minas Gerais";
        return "Estado não presente no banco de dados";
    }

    private long calculateParkingTime(String entryTime) {
        //Podemos utilizar também a função now()
        LocalTime entry = LocalTime.parse(entryTime.split(" a ")[0]); //Esse pega a primeira parte da String, HORA ENTRADA
        LocalTime exit = LocalTime.parse(entryTime.split(" a ")[1]); //Esse pega a segunda parte da String, HORA SAIDA
        return Duration.between(entry, exit).toMinutes(); //Função utilizada para calcular a diferença entre dois objetos LocalTime
    }

    private double calculateParkingCharge(long parkingTime) {
        final double INITIAL_RATE = 3.00; //Valor incial a ser pago por permanencia de 3 horas, 3 minutos
        final double ADDITIONAL_RATE_PER_HOUR = 1.50; // Valor incial a cada hora ou fração de hora, depois das 3 horas

        if (parkingTime <= 15) {
            return 0.0; // Aqui é a tolerancia de 15 minutos
        } else if (parkingTime <= 180) {
            return INITIAL_RATE; //Aqui é permanencia até 3 horas (180 minutos)
        } else {
            long additionalHours = (parkingTime - 180 + 59) / 60; //Arredonda para cima, função teto** (adicionamos 59, justamente para ter a função teto), long pega a parte inteira.
            return INITIAL_RATE + additionalHours * ADDITIONAL_RATE_PER_HOUR; //Retorna o valor a se pagar
        }
    }




}
