import java.util.*;

public class ByteBurgers {
    static final Scanner sc = new Scanner(System.in);
    static Random rand = new Random();
    // La carta de preus (Constants)
    static Map<String, Double> CARTA = new HashMap<>();
    static {
        CARTA.put("Hamburguesa", 8.50);
        CARTA.put("Patates", 3.00);
        CARTA.put("Refresc", 2.50);
        CARTA.put("Aigua", 1.50);
    }


    public static void main(String[] args) {
        Map <String, Integer> comandaActual = new HashMap<>();
        Map <String, Integer> stock;
        Map <String, Integer> vendes = new HashMap<>();

        boolean botigaOberta = true;
        stock = inicialitzaStock();

        System.out.println("=== BENVINGUT A BYTEBURGERS ===");

        while (botigaOberta) {
            mostrarMenuPrincipal();
            int opcio = llegirOpcio(6);

            switch (opcio) {
                case 1:
                    mostrarCarta();
                    esperarEnter();
                    break;
                case 2:
                    afegirProducte(comandaActual, stock);
                    break;
                case 3:
                    mostrarCistella(comandaActual);
                    esperarEnter();
                    break;
                case 4:
                    processarPagament(comandaActual, vendes);
                    break;
                case 5:
                    menuAdministrador(stock,vendes);
                    break;
                case 6:
                    System.out.println("Tancant la caixa... Fins demà!");
                    botigaOberta = false;
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }
        }
    }

    public static void mostrarMenuPrincipal() {
        System.out.println("=== MENU D'OPCIONS ===");
        System.out.println("1. Mostrar Carta");
        System.out.println("2. Afegir Producte a la comanda");
        System.out.println("3. Mostrar Cistella de la comanda");
        System.out.println("4. Processar Pagament");
        System.out.println("5. Menu Administrador (Requereix credencials)");
        System.out.println("6. Sortir");
    }

    public static void mostrarCarta() {
        System.out.println("=== CARTA ===");
        for (Map.Entry<String, Double> entry : CARTA.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static String demanarProducte(Map<String, Integer> stock){
        Set<String> productes = stock.keySet();
        System.out.println("Introdueix el nom d'un producte existent: ('carta' per veure la carta) ");
        String producte = sc.nextLine();
        if(producte.equals("carta")){
            mostrarCarta();
            System.out.println("Introdueix el nom d'un producte existent: ");
            producte = sc.nextLine();
        }else if(producte.isEmpty()){
            return "tornar";
        }
        if(productes.contains(producte)){
            return producte;
        }else{
            System.out.println("El producte no existeix.");
            return demanarProducte(stock);
        }
    }

    public static Map<String, Integer> inicialitzaStock() {
        HashMap<String, Integer> stock = new HashMap<>();
        for(String key : CARTA.keySet()) {
            stock.put(key, rand.nextInt(13)+5);
        }
        return stock;
    }

    public static int llegirOpcio(int max) {
        System.out.print("Selecciona una opció (1-"+max+"): ");
        if(sc.hasNextLine()){
            int opcio = 0;
            try{
                opcio = sc.nextInt();
            }catch(InputMismatchException e){
                System.out.println("Has d'introduir un numero valid");
                sc.nextLine();
                return llegirOpcio(max);
            }
            //sc.nextLine();
            if(opcio<1 || opcio>max){
                System.out.println("El numero ha d'estar en el rang de 1 a " + max);
                return llegirOpcio(max);
            }
            sc.nextLine();
            return opcio;
        }else{
            return 0;
        }
    }

    public static int llegirUnitats(){
        System.out.println("Introdueix el numero d'unitats: ");
        if(sc.hasNextLine()){
            int unitats = 0;
            try{
                unitats = sc.nextInt();
            }catch(InputMismatchException e){
                System.out.println("Has d'introduir un numero valid");
                sc.nextLine();
                return llegirUnitats();
            }
            //sc.nextLine();
            if(unitats<1){
                System.out.println("El numero ha de ser superior a 0");
                return 0;
            }
            sc.nextLine();
            return unitats;
        }else{
            return 0;
        }
    }

    public static void mostrarCistella(Map <String, Integer> comandaActual) {
        if(comandaActual.isEmpty()){
            System.out.println("La teva cistella es buida.");
        }else{
            System.out.printf("%-20s %-10s %-15s %-10s", "PRODUCTE", "UDS", "PREU/U", "TOTAL");
            System.out.println();
            System.out.println("-------------------------------------------------------------------------");
            for (Map.Entry<String, Integer> entrada : comandaActual.entrySet()) {
                String producte = entrada.getKey();
                int unitats = entrada.getValue();
                double preuUnitari = CARTA.get(producte);
                double totalProducte = unitats * preuUnitari;
                System.out.printf("%-20s %-10d %-10.2f %-10.2f%n", producte, unitats, preuUnitari, totalProducte);
            }
            System.out.printf("Subtotal sense descompte: %.2f€%n", calcularTotal(comandaActual, true));
            System.out.println();
        }
    }

    public static void afegirProducte(Map <String, Integer> comandaActual, Map <String, Integer> stock) {
        String producte = demanarProducte(stock);
        if(!producte.equals("tornar")) {
            int unitats = llegirUnitats();
            if (unitats != 0) {
                if (stock.get(producte) >= unitats) {
                    stock.put(producte, stock.get(producte) - unitats);
                    if (comandaActual.containsKey(producte)) {
                        comandaActual.put(producte, comandaActual.get(producte) + unitats);
                    } else {
                        comandaActual.put(producte, unitats);
                    }
                    System.out.println("S'ha afegit correctament " + unitats + " unitats de " + producte);
                } else {
                    System.out.println("No hi ha unitats suficients del producte solicitat.");
                    esperarEnter();
                }
            }
        }
    }

    public static void processarPagament(Map <String, Integer> comandaActual, Map <String, Integer> vendes) {
        mostrarCistella(comandaActual);
        double total = calcularTotal(comandaActual, false);
        total = procesarDescompte(total);
        System.out.printf("%s: %.2f€%n", "El total de la compra actual es", total);
        System.out.println();
        actualitzarVendes(comandaActual, vendes);
        comandaActual.clear();
        System.out.println("Comanda procesada correctament.");
        esperarEnter();
    }

    public static void actualitzarVendes(Map<String, Integer> comandaActual, Map<String, Integer> vendes) {
        for (Map.Entry<String, Integer> entry : comandaActual.entrySet()) {
            String producte = entry.getKey();
            int quantitat = entry.getValue();
            if(vendes.containsKey(producte)){
                vendes.put(producte, vendes.get(producte) + quantitat);
            }else{
                vendes.put(producte, quantitat);
            }
        }
    }

    public static void menuAdministrador(Map <String, Integer> stock, Map <String, Integer> vendes) {
        if (autenticarUsuari()) {
            boolean menuObert = true;

            System.out.println("=== BENVINGUT AL MENU ADMINISTRADOR ===");

            while (menuObert) {
                mostrarMenuAdministrador();
                int opcio = llegirOpcio(5);

                switch (opcio) {
                    case 1:
                        afegirStock(stock);
                        esperarEnter();
                        break;
                    case 2:
                        mostrarMap(stock, "stock");
                        esperarEnter();
                        break;
                    case 3:
                        mostrarMap(vendes, "vendes");
                        esperarEnter();
                        break;
                    case 4:
                        afegirProducteNou(stock);
                        esperarEnter();
                        break;
                    case 5:
                        System.out.println("Tornant a la caixa...");
                        menuObert = false;
                        break;
                    default:
                        System.out.println("Opció no vàlida.");
                }
            }
        }
    }

    public static void afegirProducteNou(Map <String, Integer> stock){
        System.out.print("Introdueix el nom del nou producte a introduir: ");
        String producte = sc.nextLine();
        if(producte.isEmpty()){
            System.out.println("Tornant...");
        }else if(CARTA.containsKey(producte)){
            System.out.println("El producte ja existeix, nomes pots afegir stock...");
        }else{
            System.out.print("Introdueix el preu del nou producte: ");
            double preu;
            try{
                preu = sc.nextDouble();
            }catch (InputMismatchException e){
                System.out.println("Has d'introduir un numero decimal valid...");
                afegirProducteNou(stock);
                return;
            }
            sc.nextLine();
            int nouStock;
            System.out.print("Introdueix el stock del nou producte: ");
            try{
                nouStock = sc.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Has d'introduir un numero enter valid...");
                afegirProducteNou(stock);
                return;
            }
            stock.put(producte, nouStock);
            CARTA.put(producte, preu);
            System.out.println("S'ha afegir correctament " + producte + " amb el preu " + preu + " i el stock inicial " + nouStock);
        }
    }

    public static void mostrarMap(Map <String, Integer> stock, String tipusMap) {
        if(stock.isEmpty()){
            System.out.println("Sorry, " + tipusMap + " esta buit.");
        }else{
            System.out.println("=== " + tipusMap.toUpperCase() + " DE PRODUCTES ACTUAL ===");
            for (Map.Entry<String, Integer> entrada : stock.entrySet()) {
                String key = entrada.getKey();
                int value = entrada.getValue();
                System.out.printf("%-20s %-10d", key, value);
                System.out.println();
            }
        }
    }

    public static void afegirStock(Map <String, Integer> stock) {
        String producte = demanarProducte(stock);
        if(!producte.equals("tornar")) {
            int unitats = llegirUnitats();
            stock.put(producte, stock.get(producte) + unitats);
            System.out.println("S'ha afegit correctament " + unitats + " unitats de " + producte + ".");
        }
    }

    public static void mostrarMenuAdministrador(){
        System.out.println("=== MENU D'OPCIONS ===");
        System.out.println("1. Afegir stock d'un producte");
        System.out.println("2. Mostrar stock de productes");
        System.out.println("3. Mostrar vendes");
        System.out.println("4. Afegir un producte nou");
        System.out.println("5. Tornar al menu principal");
    }

    public static boolean autenticarUsuari(){
        System.out.print("Introdueix l'usuari per accedir: ");
        String usuari = sc.nextLine();
        Map<String, String> usuaris = Map.of(
                "oarnab", "super3",
                "admin", "passw0rd"
        );
        if(usuari.isEmpty()){
            System.out.println("Cap usuari introduit, tornant...");
            return false;
        }else{
            System.out.print("Introdueix la contrasenya de l'usuari introduit per accedir: ");
            String contrasenya = sc.nextLine();
            if(contrasenya.isEmpty()){
                System.out.println("Cap contrasenya introduida, tornant...");
                return false;
            }else{
                if(!usuaris.containsKey(usuari)){
                    System.out.println("Incorrecta, tornant...");
                    return false;
                }
                if(usuaris.get(usuari).equals(contrasenya)){
                    System.out.println("Correcta, accdenit...");
                    return true;
                }else{
                    System.out.println("Incorrecta, tornant...");
                    return false;
                }
            }
        }
    }

    public static double calcularTotal(Map <String, Integer> comandaActual, boolean mostrar) {
        double preuBase = 0;
        double iva = 0.21;
        for(Map.Entry<String, Integer> entry : comandaActual.entrySet()) {
            preuBase += (double) entry.getValue() * CARTA.get(entry.getKey());
        }
        double preuIVA = preuBase * (1 + iva);
        if(mostrar){
            System.out.printf("%s: %.2f€%n", "Preu base: ", preuBase);
            System.out.println("IVA aplicat: " + (iva * 100) + "%");
        }
        return preuIVA;
    }

    public static double procesarDescompte(double preu){
        double preuFinal = preu;
        System.out.println("¿Tens algún descompte? (Y o N)");
        char resposta = sc.nextLine().charAt(0);
        if(resposta == 'Y'){
            System.out.println("Introdueix el codi del descompte: ");
            String codi = sc.nextLine();
            if(codi.equals("DAM2025")){
                preuFinal = preuFinal * 0.9;
                System.out.println("Descompte del 10% aplicat correctament.");
                return preuFinal;
            }else{
                System.out.println("El codi introduit no existeix.");
                return preuFinal;
            }
        } else if (resposta == 'N') {
            return preuFinal;
        }else{
            System.out.println("Opció invalida");
            return procesarDescompte(preu);
        }
    }

    public static void esperarEnter(){
        System.out.println("Presiona ENTER per continuar...");
        sc.nextLine();
    }
}