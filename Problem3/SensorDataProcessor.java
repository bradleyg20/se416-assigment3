import java.io.BufferedWriter;
import java.io.FileWriter;

public class SensorDataProcessor{

    // Senson data and limits.
    public double[][][] data;
    public double[][] limit;

    // constructor
    public DataProcessor(double[][][] data, double[][] limit) {
        this.data = data;
        this.limit = limit;
    }

    // calculates average of sensor data
    private double average(double[] array) {
        int i = 0;
        double val = 0;
        for (i = 0; i < array.length; i++) {
            val += array[i];
        }

        return val / array.length;
    }

    // calculate data
    public void calculate(double d) {

        int i, j, k = 0;
        double[][][] data2 = new double[data.length][data[0].length][data[0][0].length];

        BufferedWriter out;

        // Write racing stats data into a file
        try {
            out = new BufferedWriter(new FileWriter("RacingStatsData.txt"));

            for (i = 0; i < data.length; i++) {
                for (j = 0; j < data[0].length; j++) {
                    double limitSquared = Math.pow(limit[i][j], 2.0);
            
                    for (k = 0; k < data[0][0].length; k++) {
                        double val = data[i][j][k];
                        data2[i][j][k] = val / d - limitSquared;
            
                        double avgData2 = average(data2[i][j]);
                        double avgData = average(data[i][j]);
            
                        if (avgData2 > 10 && avgData2 < 50) break;
            
                        else if (Math.max(val, data2[i][j][k]) > val) break;
            
                        else {
                            double cubeVal = val * val * val;
                            double cubeData2 = data2[i][j][k] * data2[i][j][k] * data2[i][j][k];
            
                            if (cubeVal < cubeData2 && avgData < data2[i][j][k] && (i + 1) * (j + 1) > 0)
                                data2[i][j][k] *= 2;
                        }
                    }
                }
            }
            out.close();
        }  
        catch (Exception e) {
            System.out.println("Error= " + e);
        }
    }
}