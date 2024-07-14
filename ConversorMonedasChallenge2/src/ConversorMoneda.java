import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ConversorMoneda {

    public static ComponentesConversion consultarConversion(String monedaOrigen, String monedaDestino, double monto) {
        String direccion = "https://v6.exchangerate-api.com/v6/1a37262885b92427b0dad50b/latest/" + monedaOrigen;

        try {
            URL apiUrl = new URL(direccion);
            HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();

                String response = responseBuilder.toString();
                if (!response.isEmpty()) {
                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                    JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

                    double tasaCambio = conversionRates.get(monedaDestino).getAsDouble();
                    double montoConvertido = monto * tasaCambio;

                    System.out.println(" => El valor de " + monto + " [" + monedaOrigen.toUpperCase() + "]" + " corresponde al valor final de: " + montoConvertido + " [" + monedaDestino.toUpperCase() + "]");


                    ComponentesConversion consulta = new ComponentesConversion(monedaOrigen, monedaDestino, montoConvertido);

                    GeneradorDeArchivo generador = new GeneradorDeArchivo();
                    generador.guardarJson(consulta);

                    return new ComponentesConversion(monedaOrigen, monedaDestino, montoConvertido);

                } else {
                    System.out.println("La respuesta de la API está vacía.");
                }
            } else {
                System.out.println("Error al conectar a la API. Código de estado: " + status);
            }
            con.disconnect();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

}