import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*****************************************************************");
        System.out.println("");
        System.out.println("Bienvenido/a al Conversor de Moneda");
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n*****************************************************************");
            System.out.println("");
            System.out.println("Seleccione la opción del menú con el tipo de cambio a realizar:");
            System.out.println("");
            System.out.println("1) De Dólar a Peso argentino");
            System.out.println("2) De Peso argentino a Dólar");
            System.out.println("3) De Dólar a Real brasileño");
            System.out.println("4) De Real brasileño a Dólar");
            System.out.println("5) De Dólar a Peso Colombiano");
            System.out.println("6) De Peso colombiano a Dólar");
            System.out.println("7) Salir");


            System.out.println("************************************************");
            System.out.println("");
            System.out.println("Ingrese una opción: ");
            int opcion = scanner.nextInt();
            double monto;

            switch (opcion) {
                case 1:
                    System.out.println("Ingrese el monto en dólares a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes1 = ConversorMoneda.consultarConversion("USD", "ARS", monto);
                    break;
                case 2:
                    System.out.println("Ingrese el monto en pesos argentinos a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes2 = ConversorMoneda.consultarConversion("ARS", "USD", monto);
                    break;
                case 3:
                    System.out.println("Ingrese el monto en dólares a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes3 = ConversorMoneda.consultarConversion("USD", "BRL", monto);
                    break;
                case 4:
                    System.out.println("Ingrese el monto en reales brasileños a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes4 = ConversorMoneda.consultarConversion("BRL", "USD", monto);
                    break;
                case 5:
                    System.out.println("Ingrese el monto en dólares a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes5 = ConversorMoneda.consultarConversion("USD", "COP", monto);
                    break;
                case 6:
                    System.out.println("Ingrese el monto en pesos colombianos a convertir:");
                    monto = scanner.nextDouble();
                    ComponentesConversion componentes6 = ConversorMoneda.consultarConversion("COP", "USD", monto);
                    break;
                case 7:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, seleccione una opción válida.");
            }
        }
        System.out.println("¡Hasta luego!");
        scanner.close();
    }
}
