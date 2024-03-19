public class Sum{
	public static void main(String[] args){
		int sum = 0;
		int iPow = 0;
		int curNumber = 0;
		final char ZERO = '0';
		final char NINE = '9';
		final char MINUS = '-';
		
		for(String str: args){
			iPow = 0;
			curNumber = 0;
			for(int i = str.length() - 1; i >= 0; i--){
				char simbol = str.toCharArray()[i];
				if((simbol >= ZERO) & (simbol <= NINE)) {
					curNumber += Character.getNumericValue(simbol) * Math.pow(10, iPow);
					iPow += 1;
				}
				else if (simbol == MINUS){
					curNumber *= -1;
				}
				else{
					iPow = 0;
					sum += curNumber;
					curNumber = 0;
				}
			}
			sum += curNumber;
		}

		System.out.println(sum);
	}
}