// public class SumDoubleSpace {
// 	public static void main(String[] args){
// 		double sum = 0;
// 		String[] numbers;

// 		for (int i = 0; i < args.length; i++){
// 			numbers = args[i].split("[^\\d-.eE]");
// 			for(String number : numbers){
// 				if(number.length() != 0){
// 					number = number.replace("e", "E");
// 					sum += Double.parseDouble(number);
// 				}
// 			}
// 		}
// 		System.out.println(sum);
// 	}
// }
public class SumDoubleSpace {
	public static void main(String[] args) {
		double sum = 0;
		StringBuilder number = new StringBuilder(""); 
		
		for (String str: args) {
			for(int i = 0; i < str.length(); i++) {
				char simbol = str.charAt(i);
				if (!Character.isSpaceChar(simbol)) {
				    number.append(simbol);
				} else if (number.length() != 0) {
					sum += Double.parseDouble(number.toString());
				    number = new StringBuilder("");
				}
			}
			if (number.length() != 0) {
				sum += Double.parseDouble(number.toString());
				number = new StringBuilder("");
			}
		}

		System.out.println(sum);
	}
}