import java.io.*;
import java.util.Scanner;

public class connectedCC4{
	static int [][]zeroFramedAry;
	static int []EQAry;
	static String [] Property;
	static int[] neighborAry=new int[4];
	static int numRows,	numCols, minVal, maxVal, newMin, newMax=0, newLabel=0, numLabelUsed=0;
	
	public static void main(String[] args){
		connectedCC(args);
		System.out.println("All work done!");
	}
	
	private static void connectedCC(String[] args) {
		initial(args[0]);
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(args[1]));//open output file
			ConnectCC_Pass1();
			outFile.write("The result of Pass1:");
			outFile.newLine();
			PrettyPrint(outFile);
			outFile.write("The EQ Array of Pass1:");
			outFile.newLine();
			printEQAry(outFile);
			outFile.newLine();
			outFile.write("--------------------------------------------------------------------");
			outFile.newLine();
			ConnectCC_Pass2();
			outFile.write("The result of Pass2:");
			outFile.newLine();
			PrettyPrint(outFile);
			outFile.write("The EQ Array of Pass2:");
			outFile.newLine();
			printEQAry(outFile);
			outFile.newLine();
			outFile.write("--------------------------------------------------------------------");
			outFile.newLine();
			manageEQAry();
			outFile.write("The EQ Array of manageEQAry:");
			outFile.newLine();
			printEQAry(outFile);
			outFile.newLine();
			outFile.write("--------------------------------------------------------------------");
			outFile.newLine();
			ConnectCC_Pass3();
			outFile.write("The result of Pass3:");
			outFile.newLine();
			PrettyPrint(outFile);
			outFile.write("The EQ Array of Pass3:");
			outFile.newLine();
			printEQAry(outFile);
			outFile.close();//close file
		}
		catch(Exception e){System.out.println(e);}
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(args[2]));//open output file
			if (numLabelUsed>0) newMin = 1;
			else newMin=0;
			newMax = numLabelUsed;
			outFile.write(numRows+" "+numCols+" "+newMin+" "+newMax);
			outFile.newLine();
			PrettyPrint(outFile);
			outFile.close();//close file
		}
		catch(Exception e){System.out.println(e);}
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(args[3]));//open output file
			computeProperty();
			outFile.write("The connected component property:");
			outFile.newLine();
			for(int i=1; i<=numLabelUsed; i++){ 
				outFile.write(Property[i]);
				outFile.newLine();
			}
			outFile.close();//close file
		}
		catch(Exception e){System.out.println(e);}
	}
	
	private static void initial(String fileName) {
		int row = 1, col = 1;
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File(fileName));//open input file
			String oneLine = inFile.nextLine();//get the header
			String numbers[] = oneLine.split(" ");//get data that split by " "
			numRows=Integer.parseInt(numbers[0]);
			numCols=Integer.parseInt(numbers[1]);
			zeroFramedAry = new int[numRows+2][numCols+2];//dynamically allocate 
			while(inFile.hasNext()){
				zeroFramedAry[row][col++] = inFile.nextInt();
				if(col>numCols) {
					row++;
					col=1;
				}
			}
			EQAry = new int[numRows*numCols/4];
			for(int i=0; i<numRows*numCols/4; i++)
				EQAry[i]=i;
			inFile.close();//close file
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void ConnectCC_Pass1(){
		for(int i=1; i<numRows+1; i++) {
			for(int j=1; j<numCols+1; j++) {
				loadNeighbors(i,j);
				if(zeroFramedAry[i][j]>0)
					if(neighborAry[0]==0 && neighborAry[1]==0)
						zeroFramedAry[i][j]=++newLabel;
					else 
						zeroFramedAry[i][j]=findMinNotZero(i,j,1);
			}
		}
	}
	
	private static void ConnectCC_Pass2(){
		for(int i=numRows; i>0; i--) {
			for(int j=numCols; j>0; j--) {
				loadNeighbors(i,j);
				if(zeroFramedAry[i][j]>0 && (neighborAry[2]!=0 || neighborAry[3]!=0))
					if(zeroFramedAry[i][j]!=neighborAry[2] || zeroFramedAry[i][j]!=neighborAry[3]) {
						int minLabel = findMinNotZero(i,j,2);
						updateEQAry(zeroFramedAry[i][j], minLabel);
						zeroFramedAry[i][j] = minLabel;
					}
			}
		}
	}
	
	private static void ConnectCC_Pass3(){
		for(int i=1; i<numRows+1; i++) 
			for(int j=1; j<numCols+1; j++) 
				if(zeroFramedAry[i][j]>0)
					zeroFramedAry[i][j]=EQAry[zeroFramedAry[i][j]];
	}
	
	private static void manageEQAry(){
		for(int i=1; i<=newLabel; i++)
			if(EQAry[i]==i) 
				EQAry[i]=++numLabelUsed;
			else
				EQAry[i]=EQAry[EQAry[i]];
	}
	
	private static void computeProperty() {
		Property = new String[numLabelUsed+1];
		for(int m=1; m<=numLabelUsed; m++) {
			Property[m]="Label: "+Integer.toString(numLabelUsed)+".    ";
			int sumPixel = 0;
			for (int i = 1; i < numRows+1; i++)
				for (int j = 1; j < numCols+1; j++)
					if (zeroFramedAry[i][j]==m)
						sumPixel++;
			Property[m]+="#pixels: "+Integer.toString(sumPixel)+".    ";
			Property[m]+="The bounding box:";
			int minR=numRows, minC=numCols, maxR=0, maxC=0;
			for (int i = 1; i < numRows+1; i++)
				for (int j = 1; j < numCols+1; j++)
					if (zeroFramedAry[i][j]==m) {
						minR=(i<minR? i:minR);
						minC=(j<minC? j:minC);
						maxR=(i>maxR? i:maxR);
						maxC=(j>maxC? j:maxC);
					}
			Property[m]+="("+Integer.toString(minR)+", "+Integer.toString(minC)+", "+Integer.toString(maxR)+", "+Integer.toString(maxC)+")"+".  ";
		}
	}
	
	public static void PrettyPrint(BufferedWriter outFile) throws IOException{
		for (int i = 1; i < numRows+1; i++) {
			for (int j = 1; j < numCols+1; j++) {
				if (zeroFramedAry[i][j] >0)//pixel_val > 0
					outFile.write(zeroFramedAry[i][j]+" ");//output pixel_val
				else
					outFile.write("  ");//output ' ' // blank
			}
			outFile.newLine();
		}
	}
	
	public static void loadNeighbors(int row, int col){
		neighborAry[0]=zeroFramedAry[row-1][col];
		neighborAry[1]=zeroFramedAry[row][col-1];
		neighborAry[2]=zeroFramedAry[row][col+1];
		neighborAry[3]=zeroFramedAry[row+1][col];
	}
	
	public static void updateEQAry(int index, int val){
		EQAry[index] = val;
	}
	
	public static void printEQAry(BufferedWriter outFile) throws IOException{
		for(int i=1; i<=newLabel; i++)
			outFile.write(i+" ");
		outFile.newLine();
		for(int i=1; i<=newLabel; i++)
			if(i>9 && EQAry[i]<10)
				outFile.write(" "+EQAry[i]+" ");
			else
				outFile.write(EQAry[i]+" ");
		outFile.newLine();
	}
	
	public static int findMinNotZero(int row, int col, int pass){
		if(pass==1){
			if(neighborAry[0]<neighborAry[1])
				return (neighborAry[0]==0? neighborAry[1]:neighborAry[0]);
			else
				return (neighborAry[1]==0? neighborAry[0]:neighborAry[1]);
		}
		if(pass==2){
			if(neighborAry[2]==0){
				if(zeroFramedAry[row][col]<neighborAry[3])
					return zeroFramedAry[row][col];
				else
					return (neighborAry[3]==0? zeroFramedAry[row][col]:neighborAry[3]);
			}
			if(neighborAry[3]==0){
				if(zeroFramedAry[row][col]<neighborAry[2])
					return zeroFramedAry[row][col];
				else
					return (neighborAry[2]==0? zeroFramedAry[row][col]:neighborAry[2]);
			}
			int temp=zeroFramedAry[row][col];
			temp=(neighborAry[2]<temp? neighborAry[2]:temp);
			temp=(neighborAry[3]<temp? neighborAry[3]:temp);
			return temp;
		}
		return 0;
	}
}





