 class CarInfo {
    private final String plate;//Placa do carro
    private final String time;//O tempo do carro em questão

    public CarInfo(String plate, String time) {
        this.plate = plate;
        this.time = time;
    }

    public String getPlate() {
        return plate;
    }

    public String getTime() {
        return time;
    }
}