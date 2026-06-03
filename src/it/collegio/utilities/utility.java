package it.collegio.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



 public final class utility {
    
    // per recuperare l'ultimo numero
    public static int convInt(String num){
        int ris;
        ris = Integer.parseInt(num.substring(1));  
        return ris;   
    }
    
    //metodo per visualizzare il la l'id della camera in modo alphanumerico
    public static String convAlfaR(int num){
        String ris;
        ris = "R0" + String.format("%03d",num);
        return ris;   
    }

    //metodo per visualizzare il la l'id della camera in modo alphanumerico
    public static String convAlfaP(int num){
        String ris;
        ris = "P0" + String.format("%03d",num);
        return ris;   
    }
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }
    
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("\\d{10}"); // Regex precompilata
    public static boolean validaNumeroTelefono(String numero) {
        Matcher matcher = PATTERN_TELEFONO.matcher(numero);
        return matcher.matches();
    }
    
     public static int calculDate(String f1, String f2) {   
        int days = 0;
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd"); 
        
        try{ 
            
            Date d1=simple.parse(f1); // meno recente
            Date d2=simple.parse(f2); // piu  recente
                
            long diff = d2.getTime() - d1.getTime();
            days=(int)(diff/(1000*24*60*60));
            
            if(days == 0)
                days++;
            
           
        }catch(Exception ex){
           ex.getStackTrace();
        }
        return days; 
    }
}
