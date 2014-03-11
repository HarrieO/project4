/*
 * class taken from Assignment 3 DataStructuren 2012-2013
 */
public class Division {
    int table_length;
    int initial = 11;
    int multiplier = 43;
    
    public Division(int length) {
        table_length = length;
    }
    public Division(int length, int initial, int multiplier) {
        table_length = length;
        this.initial = initial ;
    	this.multiplier = multiplier ;
    }
    public int calcIndex(Object Key){
    	if( Key.getClass().equals(java.lang.String.class) ){
    		return calcStringIndex((String) Key);
    	} else {
    		throw( new java.security.InvalidParameterException(
    				"No HashingMethod for an object of class " + Key.getClass().toString() + ".") );
    	}
    }
    public int calcStringIndex(String key) {
        int index;

        index = Math.abs(hashCode(key)) % table_length;
        return index;
    }

    private int hashCode(String key) {
        char[] val = key.toCharArray();
        int h = initial * (val[0] +1);
        int len = key.length();
        for (int i = 1; i < len; i++) {
            h = multiplier * h + val[i]*val[i-1] ;
        }
       
        
        return h;
    }

}
