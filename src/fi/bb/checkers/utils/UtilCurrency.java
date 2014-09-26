package fi.bb.checkers.utils;

public class UtilCurrency {

    public UtilCurrency(){
          super();
    }
    
    
    public String parseAmount(String temp){
          String num="";
          int ln = temp.length();
          if(ln == 0){
                num = "0.00";
          }else if(ln == 1){
                num = "0.0" + temp;           
          }else if(ln == 2){
                num = "0." + temp;
          }else if(ln > 2  ){
        	  char test =temp.charAt(ln-3);
        	  if(temp.charAt(ln-3) !='.'){
                num = temp.substring(0, ln-2) + "." + temp.substring(ln-2, ln);
        	  }
        	  else{
        		  num = temp;
        	  }
          }else{
                num = "?";
          }
          return num;
    }
    
    public String parseAmountProvided(String dummy) {
          int f = 0;
          int cnt = 0;
          String rand = "";
          String cent = "";
          String fin = "";
          for (int i = 0; i < dummy.length(); i++) {
                if (f == 0) {
                      if (Character.isDigit(dummy.charAt(i))) {
                            rand = rand + dummy.charAt(i);
                      } else {
                            f = 1;
                      }
                } else if (f == 1 && cnt < 2) {
                      cnt = cnt + 1;
                      cent = cent + dummy.charAt(i);
                } else {
                      f = 4;
                }
          }
          
          if (f != 4) {
                      if(cent.length() == 0){
                            cent = cent + "00";
                      }else if(cent.length() == 1){
                            cent = cent + "0";
                      }
                //fin = rand + cent;
                fin = rand + "." + cent;
                return fin;
          }
          return fin;
    }

}
