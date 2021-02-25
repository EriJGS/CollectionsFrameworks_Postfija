/* Desarrollo III (18 de febrero de 2021) - Ejemplo Java Collections Framework 02

Notación infija -> notación postfija

 */
package notacionpostfija;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 *
 * @author Erica Guzmán
 */
public class NotacionPostfija {

    public static void main(String[] args) {
        
        String infija = "2 * (3 + 4) + 6";   // 2 3 4 + * 6 +   ->> 20.0
//        String infija = "1 - (6 / 2) - 1";   // 1 6 2 / 1 - -   ->> -1.0
//        String infija = "1 * (2 + 2) + 1";   // 1 2 2 + * 1 +   ->> 5.0
//        String infija = "2 / (2 - 1) * 2";   // 2 2 1 - 2 * /   ->> 1.0
//        String infija = "1 + (1 * 5) / 2";   // 1 1 5 * 2 / +   ->> 3.5
        
        String postfija = postfija(infija);    // Convertir a postfija
        
        System.out.println(postfija);
        
        System.out.println(evaluar(postfija)); // Resolver expresión aritmética
    }
    
    // Método que pasa una expresión infija a postfija
    public static String postfija(String infija) {
        StringTokenizer st = new StringTokenizer(infija, "()+-/* ", true);   // Separar en tokens(símbolos) una cadena, indicando los caracteres a utilizar para separar. El "true" indica que me regrese los separadores como si fueran tokens también
        
        Stack<String> pilaOperadores = new Stack<String>();   // Pila para almacenar los operadores de la expresión
        
        StringBuilder postfija = new StringBuilder();         // Cadena que será la expresión ya en notación postfija. Como la iremos actualizando, es StringBuilder

        String token = null;

        while (st.hasMoreTokens()) {                          // Verificar si hay más tokens en la cadena (boolean)
            String simbolo = st.nextToken().trim();           // Extraer el símbolo que sigue, trim() quita espacios en blanco al inicio o final (porque dividimos por espacios también, y a veces el token será sólo un espacio " ")
            
            if (simbolo.isEmpty()) {                          // Si el token está limpio " " nos salimos y volvemos a entrar al while
                continue;
            }
            
            if (simbolo.equals("(")) {                        // Si el símbolo es (
                pilaOperadores.push(simbolo);                 // Lo metemos a la pila
                continue;                                     // Pasamos a leer el sig. token
            }
            
            if (simbolo.equals(")")) {                        // Si el símbolo es )
                do {
                    token = pilaOperadores.pop();             // De la pila sacamos cada operador
                    if (!token.equals("(")) {                 // Verificamos si es (
                        postfija.append(token);               // Si no es, lo pasamos a la cadena postfija. Los símbolos ( ) no irán en la postfija por eso los evitamos
                        postfija.append(" ");                 // Y damos un espacio
                    }
                } while (!token.equals("("));                 // Si encontramos el )
                continue;                                     // Salimos y volvemos al while principal
            }
            
            if (esOperador(simbolo.trim())) {                 // Si el simbolo es un operador. Le quitamos los espacios en blanco
                while (!pilaOperadores.empty()) {             // Si sí es, revisaremos la pila. Mientras no esté vacía seguiremos leyendo
                    
                    token = pilaOperadores.peek();                    // Revisamos cuál es el elemento en el tope de la pila
                    if (precedencia(token) < precedencia(simbolo)) {  // Si el operador que está en el tope de la pila tiene mayor prioridad (1) ante el operador nuevo
                        postfija.append(pilaOperadores.pop());        // Extraer el operador de la pila y meterlo a la cadena postfija (porque tiene mayor precedencia)
                        postfija.append(" ");                         // Damos un espacio en blanco
                        continue;
                    } else {                                          // Si no tiene mayor precedencia lo que está en la pila o es la misma
                        break;                                        // Salir del while
                    }
                    
                }
                pilaOperadores.push(simbolo);                         // Y meter directamente el símbolo a la pila
                continue;
            }
            
            postfija.append(simbolo);                                 // Si el símbolo no fue ninguno de los anteriores, será un operando, So, lo metemos a la cadena postfija directamente
            postfija.append(" ");
        }

        while (!pilaOperadores.empty()) {                             // Cuando ya no haya más tokens por leer, revisamos la pila
            postfija.append(pilaOperadores.pop());                    // Extraemos cada elemento de la pila (quedarán ahí los tokens de precedencia 2) y los agregamos a la expresión postfija
            postfija.append(" ");                                     // Separando por un espacio
        }

        return postfija.toString();
    }
    
    // Método que regresa el valor de la expresión postfija
    public static double evaluar(String postfija) {
        double result = 0.0;
        
        Stack<Double> pila = new Stack<Double>();   // Aquí estarán los resultados parciales de la evaluación de la expresión (sólo estarán los operando)
        
        StringTokenizer st = new StringTokenizer(postfija, "+-/* ", true);  // Separamos elementos

        while (st.hasMoreTokens()) {                   // Recorre expresión de derecha a izquierda
            String simbolo = st.nextToken().trim();    // Sacando cada uno de los símbolos

            if (simbolo.isEmpty()) {                   
                continue;
            }
            
            // Si es un operador
            if (simbolo.equals("*")
                    || simbolo.equals("/")
                    || simbolo.equals("+")
                    || simbolo.equals("-")) {
                double o2 = pila.pop();                      // Sacamos dos valores de la pila
                double o1 = pila.pop();
                
                pila.push(calcular(o1, o2, simbolo));        // Calculamos el resultado de esos dos valores correspondiente al operador que está a la derecha de esos dos números. El resultado lo metemos a la nueva pila
            
            // Si es un operando
            } else {
                Double value = Double.parseDouble(simbolo);  // Convierte el valor en doble (de ese tipo es la pila)
                pila.push(value);                            // Lo meto a la pila
            } 
        } // Se terminaron de leer todos los elementos de la expresión postfija
        
        if (!pila.empty()) {   
            result = pila.pop();  // Saco el único valor de la pila que es el resultado final
        }
        
        return result;
    }
    
    // Método para realizar la operación correspondiente con los valores correspondientes
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
    
    // Método para verificar si el token es igual a algún operador
    public static boolean esOperador(String token) {
        return "*/+-".contains(token);
    }
    
    // Método para verificar la precedencia de los operadores
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