package au.edu.uni.sydney.comp5216.comp5216w05;

public class TemperatureData {
    private double temperature;
    private String createdAt;

    public TemperatureData (){

    }

    public TemperatureData (double _temperature, String _createdAt) {
        this.createdAt = _createdAt;
        this.temperature = _temperature;
    }

    public double getTemperature(){
        return this.temperature;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

}
