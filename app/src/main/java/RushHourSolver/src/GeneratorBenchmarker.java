
public class GeneratorBenchmarker {
	public static double benchmark( BoardGenerator gen){
		
		long totalTime = 0;
		int totalLength = 0 ;
		double averageLength = 0;
		int[] frequency = new int[60];
		for( int i = 0; i < 10000; i++){
			long startTime = System.nanoTime();
			Board board = gen.generate(6, 6);
			totalTime += (System.nanoTime() - startTime)/100000000 ;
			int solution = PuzzleSolver.solve(board).size();
			for( int j = Math.min(solution,59) ; j >= 0 ; j--){
				frequency[j]++;
			}
			totalLength += solution ;
			averageLength = totalLength/(i+1.0);
			System.out.println();
			System.out.println(i + " Avg: " + (averageLength/totalTime) + " averageLenght "+ averageLength + " averageTime: " + ((double)totalTime/(i+1)) + " time: " + totalTime );
			if( i %50 == 0 ){
				for(int k = 0 ; k < 60 && frequency[k] > 0 ; k++){
					System.out.println(k + " per " +(totalTime/frequency[k]));
				}
			}
		}
		System.out.println("In " + totalTime + " an average of " + averageLength + " where made:" + (averageLength/totalTime) );
		System.out.println("Average time taken to create puzzle of lenght:");
		for(int i = 0 ; i < 60 && frequency[i] > 0 ; i++){
			System.out.println(i + " per " +(totalTime/frequency[i]));
		}
		return averageLength/totalTime ;
	}
}
