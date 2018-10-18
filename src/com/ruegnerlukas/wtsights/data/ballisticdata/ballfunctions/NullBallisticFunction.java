package com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions;

public class NullBallisticFunction implements IBallisticFunction {

	@Override
	public double eval(double x) {
		return 0;
	}

}
