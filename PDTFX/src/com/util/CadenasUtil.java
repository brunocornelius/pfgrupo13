package com.util;

public class CadenasUtil {

	public CadenasUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static int getNumeros(String cadena) {
		char[] array_cadena = cadena.toCharArray();
		String resultado = "";
		for (int i = 0; i < array_cadena.length; i++) {
			if (Character.isDigit(array_cadena[i])) {
				//Si es un digito
				resultado = resultado + array_cadena[i];
			}
		}
		if (resultado.equals("")) {
			//Si no hay digitos
			return 0;
		}
		return Integer.parseInt(resultado);
	}

}
