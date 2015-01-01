package neuralnetwork;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *	<p> IOValues contains pair of IOValue. A pair of IOValue is a pair of values in double.</p>
 *  <p> These pairs of values are used in training, validation or testing. </p>
 *	<p> Input values will be matched with the appropriate output values. </p>
 *	<p> As IOPair class takes a generic class type, IOPair is able to receive different types for its input and output type</p>
 *	<p> IOPair is able to receive single units of type, or arraylist of a single type for each input and output </p>
 * 
 * @author Adam Goh
 *
 */

public class IOValues {
	private ArrayList<IOPair> pairs;
	
	
	public IOValues(IOPair<Double,Double> pair){
		this.pairs = new ArrayList<IOPair>();
		pairs.add(pair);
	}
	
	//if input values are a list of them
	public IOValues(ArrayList<Double> inputs, ArrayList<Double> outputs){
		IOPair<ArrayList<Double>, ArrayList<Double>> pair = new IOPair<ArrayList<Double>, ArrayList<Double>>(inputs, outputs);
		this.pairs = new ArrayList<IOPair>();
		pairs.add(pair);
	}
	
	public IOValues(){
		this.pairs = new ArrayList<IOPair>();
	}
	
	/**
	 * Adding a new IOPair of double-typed input and output values
	 * @param x input value of double format
	 * @param y output value of double format
	 * @return adds a pair to the arraylist
	 */
	public boolean add(double x, double y){
		IOPair<Double, Double> pair = new IOPair<Double, Double>(x,y);
		return add(pair);
	}
	
	public boolean add(ArrayList<Double> input, ArrayList<Double> output){
		IOPair<ArrayList<Double>, ArrayList<Double>> pair = new IOPair<ArrayList<Double>, ArrayList<Double>>(input, output);
		return pairs.add(pair);
	}
	
	public boolean add(IOPair<Double,Double> pair){
		return pairs.add(pair);
	}
	
	public IOPair getIOPair(int index){
		return pairs.get(index);
	}
	
	public ArrayList<Double> getAllInputs(){
		Iterator it = pairs.iterator();
		IOPair<Double, Double> pair;
		ArrayList<Double> inputs = new ArrayList<Double>();
		
		while(it.hasNext()){
			pair = (IOPair<Double, Double>) it.next();
			inputs.add( (double) pair.getInput());
		}
		
		return inputs;
	}
	
	//TODO: this has a big trouble in casting. what if IOPair is of Arraylist<Double>?
	@SuppressWarnings("unchecked: typecast from iterator obj to IOPair<Double, Double>")
	public ArrayList<Double> getAllExpectedOutput(){
		Iterator it = pairs.iterator();
		IOPair<Double, Double> pair;
		ArrayList<Double> outputs = new ArrayList<Double>();
		
		while(it.hasNext()){
			pair = (IOPair<Double,Double>) it.next();
			outputs.add( (double) pair.getOutput());
		}
		
		return outputs;
	}
	
	
}

//IOValue class
/**
 * <p> Pair of input and output values. The generic class was decided as it gives the flexibility to how the values may be represented.</p>
 * <p> However, neural network only receives double as the default. May need to be changed for proper generic neural network.</p>
 * <p> As for now, type must be explicitly cast as double</p>
 * @author dolphin
 *
 * @param <In>	Input value
 * @param <Out>	Corresponding Output value
 */
class IOPair<In, Out> {
	 private final In input;
	 private final Out output;
	 
	 public IOPair(In input, Out output){
		 this.input = input;
		 this.output = output;
	 }
	 
	 @Override
	public String toString(){
		 return "Input value is " + input + ", Output value is " + output;
	 }
	 
	 public In getInput(){ return input; } 
	 public Out getOutput(){ return output; }
	 
	 
}

