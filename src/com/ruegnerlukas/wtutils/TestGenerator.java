package com.ruegnerlukas.wtutils;

import java.util.Random;

import com.ruegnerlukas.wtsights.data.sight.Block;
import com.ruegnerlukas.wtsights.data.sight.BlockElement;
import com.ruegnerlukas.wtsights.data.sight.ParamBool;
import com.ruegnerlukas.wtsights.data.sight.ParamFloat;
import com.ruegnerlukas.wtsights.data.sight.ParamText;
import com.ruegnerlukas.wtsights.data.sight.ParamVec2;

public class TestGenerator {

	public static void main(String[] args) {
		
//		Block block = new Block("root");
//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 2));
//		block.elements.add(new ParamFloat("rangefinderTextScale", 2));
//		block.elements.add(new ParamFloat("rangefinderVerticalOffset", 0.1f));
//		block.elements.add(new ParamFloat("rangefinderHorizontalOffset", 0.1f));
//		randomize(block);

//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 4));
//		block.elements.add(new ParamFloat("lineSizeMult", 4));
//		block.elements.add(new ParamVec2("crosshairHorVertSize", 20, 20));
//		randomize(block);

//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 4));
//		block.elements.add(new ParamFloat("lineSizeMult", 4));
//		block.elements.add(new ParamBool("drawUpward", false));
//		block.elements.add(new ParamVec2("distancePos", 0.5f, 0.5f));
//		block.elements.add(new ParamVec2("crosshairDistHorSizeMain", 0.3f, 0.3f));
//		block.elements.add(new ParamVec2("crosshairDistHorSizeAdditional", 0.05f, 0.05f));
//		block.elements.add(new ParamVec2("textPos", 0.05f, 0.05f));
//		block.elements.add(new ParamFloat("textShift", 0.5f));
//		randomize(block);

//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 4));
//		block.elements.add(new ParamVec2("distanceCorrectionPos", -0.2f, -0.2f));
//		randomize(block);
		
//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 3));
//		block.elements.add(new ParamFloat("lineSizeMult", 3));
//		block.elements.add(new ParamVec2("radialRadius", 70, 1));
//		block.elements.add(new ParamFloat("radialStretch", 5));
//		block.elements.add(new ParamFloat("radialAngle", 200));
//		block.elements.add(new ParamVec2("crosshairDistHorSizeMain", 0.1f, 0.1f));
//		block.elements.add(new ParamVec2("distancePos", 0.2f, 0.2f));
//		block.elements.add(new ParamVec2("textPos", 0.1f, 0.1f));
//		randomize(block);
//		((ParamVec2)block.getElementByName("radialRadius")).value.y = 1;
		
		
//		block.elements.add(new ParamText("thousandth", "real;ussr;nato"));
//		block.elements.add(new ParamFloat("fontSizeMult", 3));
//		block.elements.add(new ParamFloat("lineSizeMult", 3));
//		block.elements.add(new ParamVec2("radialRadius", 70, 1));
//		block.elements.add(new ParamFloat("radialStretch", 5));
//		block.elements.add(new ParamFloat("radialAngle", 200));
//		block.elements.add(new ParamVec2("crosshairDistHorSizeMain", 0.01f, 0.1f));
//		block.elements.add(new ParamVec2("distancePos", 0.2f, 0.2f));
//		block.elements.add(new ParamVec2("textPos", 0.1f, 0.1f));
//		randomize(block);
//		((ParamVec2)block.getElementByName("radialRadius")).value.y = 1;
//		
//		
//		block.resourcePrint(-1);
		
		
		
	}

	
	
	
	public static void randomize(Block block) {
		
		for(BlockElement blockElement : block.elements) {
			
			
			if(blockElement instanceof Block) {
				randomize( ((Block)blockElement) );
				
				
			} else if(blockElement instanceof ParamText) {
				ParamText paramText = (ParamText)blockElement;
				String[] strings = paramText.text.split(";");
				paramText.text = strings[new Random().nextInt(strings.length)];
			
				
			} else if(blockElement instanceof ParamFloat) {
				ParamFloat paramFloat = (ParamFloat)blockElement;
				paramFloat.value = new Random().nextFloat() * paramFloat.value;
				
			} else if(blockElement instanceof ParamVec2) {
				ParamVec2 paramVec2 = (ParamVec2)blockElement;
				paramVec2.value.set(new Random().nextFloat() * paramVec2.value.x, new Random().nextFloat() * paramVec2.value.y);
				
			}
			
		}
		
		
	}
	
	
	
	
	
	

}


















