/* Desarrollo III (18 de febrero de 2021) - Tarea: Java Collections Framework 02

Desarrollar una aplicación para evaluar expresiones aritméticas almacenadas en un archivo. 
Cada linea del archivo contiene una expresión aritmética en notación infija que puede incluir
paréntesis. Cada expresión  se debe evaluar en un hilo. Se debe mostrar la expresión y el resultado.

 */
package notacionpostfija;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *
 * @author Erica Guzmán
 */
public class NotacionPostfija_Threads implements Runnable {
    
    String linea;
    public NotacionPostfija_Threads(String linea) {
        this.linea = linea;
    }
    
    @Override
    public void run() {
        String postfija = postfija(linea);
        System.out.println(linea + "\n" + postfija + "\n" + evaluar(postfija) + "\n");
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            abrirArchivo(args[0]);
        } else {
            System.err.println("Faltan argumentos!");
        }
    }
    
    static void abrirArchivo(String nameArchivo) {
        BufferedReader bf = null;
        try {
            File archivo = new File(nameArchivo);
            bf = new BufferedReader(new FileReader(archivo));

            while (true) {
                String linea = bf.readLine();
                if (linea == null) {
                    break;
                } else {
                    Thread hilo = new Thread(new NotacionPostfija_Threads(linea));  // Crear un thread por cada línea
                    hilo.start();
                }
            }

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                bf.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public static String postfija(String infija) {
        StringTokenizer st = new StringTokenizer(infija, "()+-/* ", true);
        
        Stack<String> pilaOperadores = new Stack<String>();
        
        StringBuilder postfija = new StringBuilder();

        String token = null;

        while (st.hasMoreTokens()) {
            String simbolo = st.nextToken().trim();
            
            if (simbolo.isEmpty()) {
                continue;
            }
            
            if (simbolo.equals("(")) {
                pilaOperadores.push(simbolo);
                continue;
            }
            
            if (simbolo.equals(")")) {
                do {
                    token = pilaOperadores.pop();
                    if (!token.equals("(")) {
                        postfija.append(token);
                        postfija.append(" ");
                    }
                } while (!token.equals("("));
                continue;
            }
            
            if (esOperador(simbolo.trim())) {
                while (!pilaOperadores.empty()) {
                    
                    token = pilaOperadores.peek();
                    if (precedencia(token) < precedencia(simbolo)) {
                        postfija.append(pilaOperadores.pop());
                        postfija.append(" ");
                        continue;
                    } else {
                        break;
                    }
                    
                }
                pilaOperadores.push(simbolo);
                continue;
            }
            postfija.append(simbolo);
            postfija.append(" ");
        }

        while (!pilaOperadores.empty()) {
            postfija.append(pilaOperadores.pop());
            postfija.append(" ");
        }

        return postfija.toString();
    }
    
    public static double evaluar(String postfija) {
        double result = 0.0;
        
        Stack<Double> pila = new Stack<Double>();
        
        StringTokenizer st = new StringTokenizer(postfija, "+-/* ", true);

        while (st.hasMoreTokens()) {
            String simbolo = st.nextToken().trim();

            if (simbolo.isEmpty()) {
                continue;
            }
            if (simbolo.equals("*")
                    || simbolo.equals("/")
                    || simbolo.equals("+")
                    || simbolo.equals("-")) {
                double o2 = pila.pop();
                double o1 = pila.pop();
                
                pila.push(calcular(o1, o2, simbolo));
            } else {
                Double value = Double.parseDouble(simbolo);
                pila.push(value);
            }
        }
        if (!pila.empty()) {
            result = pila.pop();
        }
        
        return result;
    }
    
    public static double calcular(double o1, double o2, String oper) {
        double resultado = 0.0;

        switch (oper) {
            case "*":
                resultado = o1 * o2;
                break;
            case "/":
                resultado = o1 / o2;
                break;
            case "+":
                resultado = o1 + o2;
                break;
            case "-":
                resultado = o1 - o2;
                break;
        }

        return resultado;
    }
    
    public static boolean esOperador(String token) {
        return "*/+-".contains(token);
    }
    
    public static int precedencia(String token) {
        int precedencia = 100;

        if (token.equals("+") || token.equals("-")) {
            precedencia = 2;
        }
        if (token.equals("*") || token.equals("/")) {
            precedencia = 1;
        }

        return precedencia;
    }

}