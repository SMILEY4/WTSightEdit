package com.ruegnerlukas.wtutils;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.RRQRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;

public class SightUtils {

	private static final double PI = Math.PI;
	
	public static enum Thousandth {
		USSR("ussr", "ussr (6000)", 6000),
		NATO("nato", "nato (6400)", 6400),
		REAL("real", "real (6283)", 6283);
		
		public final String tag;
		public final String display;
		public int value;

		private Thousandth(String tag, String display, int value) {
			this.tag = tag;
			this.display = display;
			this.value = value;
		}
		
		public static Thousandth get(String str) {
			if(str.equalsIgnoreCase("ussr")) { return USSR; };
			if(str.equalsIgnoreCase("nato")) { return NATO; };
			if(str.equalsIgnoreCase("real")) { return REAL; };
			return USSR;
		}
	}

	
	
	
	
	public static enum TextAlign {
		LEFT(-1),
		CENTER(0),
		RIGHT(1);
		
		public final int id;
		
		private TextAlign(int id) {
			this.id = id;
		}
		
		public static TextAlign get(int id) {
			if(id == -1) { return LEFT; }
			if(id ==  0) { return CENTER; }
			if(id == +1) { return RIGHT; }
			return LEFT;
		}
		
		public static TextAlign get(String str) {
			if(LEFT.toString().equalsIgnoreCase(str)) { return LEFT; 	 }
			if(RIGHT.toString().equalsIgnoreCase(str)) { return RIGHT; 	 }
			if(CENTER.toString().equalsIgnoreCase(str)) { return CENTER; }
			return LEFT;
		}
	}
	
	
	
	
	public static enum ScaleMode {
		VERTICAL,
		RADIAL
	}
	
	
	
	public static enum TriggerGroup {
		PRIMARY("primary"),
		SECONDARY("secondary"),
		COAXIAL("coaxial"),
		MACHINEGUN("machinegun"),
		SPECIAL("special"),
		TORPEDOES("torpedoes"),
		DEPTH_CHARGE("depth_charge"),
		ROCKETS("rockets"),
		MINE("mine"),
		SMOKE("smoke"),
		UNKNOWN("?");
		
		public final String id;
		
		private TriggerGroup(String id) {
			this.id = id;
		}
		
		public static TriggerGroup get(String id) {
			for(TriggerGroup g : TriggerGroup.values()) {
				if(g.id.equalsIgnoreCase(id)) {
					return g;
				}
			}
			return UNKNOWN;
		}
		
		public boolean isOr(TriggerGroup... groups) {
			for(TriggerGroup g : groups) {
				if(this == g) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	
	
	
	
	/*
	 * 
	 * for ballistic range:
	 * 	 => radius * (pi/6000) * (maxRotAngle/21) = stretch
	 * 
	 * for custom elements
	 * 	 => |origin-center| * (pi/6000) * (maxRotAngle/21) = speed
	 *
	 * -> radialMultiplier = stretch or speed
	 * 
	 */

	
	public static double calcRadialMultiplier(double radius, double maxRotationAngle) {
		return radius * ((2.0*PI)/6000) * (maxRotationAngle/21.0);
	}
	
	
	
	
	public static double calcMaxRotationAngle(double radius, double radialMultiplier) {
		return (63000*radialMultiplier) / (PI*radius);
	}
	
	
	
	
	public static double calcRadius(double radialMultiplier, double maxRotationAngle) {
		return (63000*radialMultiplier) / (PI*maxRotationAngle);
	}
	
	
	
	
	/*
	 * (corr / radius) * mul = angle
	 * 
	 * corr 	= dist.correction angle in sov.mils
	 * radius 	= radius of element or |origin-center| for custom elements
	 * mul 		= radialMultiplier (stretch or speed)
	 * angle 	= angle in radians
	 * 
	 */
	
	
	public static double calcAngle_deg(double corrAngleSMil, double radius, double radialMultiplier) {
		return Math.toDegrees(calcAngle_rad(corrAngleSMil, radius, radialMultiplier));
	}
	
	public static double calcAngle_rad(double corrAngleSMil, double radius, double radialMultiplier) {
		return (corrAngleSMil/radius) * radialMultiplier;
	}
	
	
	
	
	public static double calcRangeCorrAngle_deg(double angleDeg, double radius, double radialMultiplier) {
		return calcRangeCorrAngle_rad(Math.toRadians(angleDeg), radius, radialMultiplier);
	}
	
	
	public static double calcRangeCorrAngle_rad(double angleRad, double radius, double radialMultiplier) {
		return (angleRad*radius) / radialMultiplier;
	}
	
	
	
	
	public static double calcRadius_deg(double angleDeg, double corrAngleSMil, double radialMultiplier) {
		return calcRadius_rad(Math.toRadians(angleDeg), corrAngleSMil, radialMultiplier);
	}
	
	
	public static double calcRadius_rad(double angleRad, double corrAngleSMil, double radialMultiplier) {
		return (corrAngleSMil * radialMultiplier) / angleRad;
	}
	
	
	
	
	public static double calcRadialMultiplier_deg(double angleDeg, double corrAngleSMil, double radius) {
		return calcRadialMultiplier_rad(Math.toRadians(angleDeg), corrAngleSMil, radius);
	}
	
	
	public static double calcRadialMultiplier_rad(double angleRad, double corrAngleSMil, double radius) {
		return (angleRad*radius) / corrAngleSMil;
	}
	
	
	
	
	public static double rangeCorrection_meters2sovmil(double meters) {
//		return (meters/10) * 0.0912205;
		return (meters/10) * 0.11;
	}
	
	
	
	
	
	
	
	public static Vector3d fitBallisticFunction(Vector2d p0, Vector2d p1, Vector2d p2, Vector2d p3, double zoomFac) {
		
		final double SINGULARITY_THRESHOLD_RATIO = 1.0e-5;
		
		Vector2d[] points = new Vector2d[] {p0, p1, p2, p3};
		
		double[][] dataA = new double[][] {
				{ 1.0, points[0].x, points[0].x*points[0].x},
				{ 1.0, points[1].x, points[1].x*points[1].x},
				{ 1.0, points[2].x, points[2].x*points[2].x},
				{ 1.0, points[3].x, points[3].x*points[3].x}, };
				
		RealMatrix A = new Array2DRowRealMatrix(dataA, false);
		A.transpose();

		double[] dataB = new double[] { points[0].y/zoomFac, points[1].y/zoomFac, points[2].y/zoomFac, points[3].y/zoomFac };
		RealVector B = new ArrayRealVector(dataB);

		double infNorm = A.getNorm();
		double singularityThreshold = infNorm * SINGULARITY_THRESHOLD_RATIO;

		RRQRDecomposition decomposition = new RRQRDecomposition(A, singularityThreshold);
		DecompositionSolver solver = decomposition.getSolver();
		RealVector v = solver.solve(B);

		double[] result = v.toArray();
		
		
		return new Vector3d(result[0], result[1], result[2]);
	}
	
	
	
	
	public static Vector3d fitBallisticFunction(List<Vector2d> points, double zoomFac) {
		
		final double SINGULARITY_THRESHOLD_RATIO = 1.0e-5;
		
		
		double[][] dataA = new double[points.size()][3];
		for(int i=0; i<points.size(); i++) {
			dataA[i][0] = 1.0;
			dataA[i][1] = points.get(i).x;
			dataA[i][2] = points.get(i).x*points.get(i).x;
		}
		
		RealMatrix A = new Array2DRowRealMatrix(dataA, false);
		A.transpose();

		double[] dataB = new double[points.size()];
		for(int i=0; i<points.size(); i++) {
			dataB[i] = points.get(i).y / zoomFac;
		}
		
		RealVector B = new ArrayRealVector(dataB);

		double infNorm = A.getNorm();
		double singularityThreshold = infNorm * SINGULARITY_THRESHOLD_RATIO;

		RRQRDecomposition decomposition = new RRQRDecomposition(A, singularityThreshold);
		DecompositionSolver solver = decomposition.getSolver();
		RealVector v = null;
		try {
			v = solver.solve(B);
		} catch(SingularMatrixException e) {
//			Logger.get().error(A);
//			Logger.get().error(B);
//			Logger.get().error(e);
			return null;
		}

		double[] result = v.toArray();
		
		return new Vector3d(result[0], result[1], result[2]);
	}
	
	
	
	
	public static double ballisticFunction(double rangeMeters, Vector3d params) {
		final double x = rangeMeters;
		return (float) (params.x + params.y*x + params.z*x*x);
	}
	
	
	
	
	// ONLINE 3d surface fitting tool:
	// http://zunzun.com/
	
	
	
	
	
	
//	public static void main(String[] args) {
//		
//		List<Vector4d> points = new ArrayList<Vector4d>();
//		points.add(new Vector4d(6.8, 740.0,  200.0,  5.0));
//		points.add(new Vector4d(6.8, 740.0, 1000.0, 26.0));
//		points.add(new Vector4d(6.8, 740.0, 2000.0, 58.0));
//		points.add(new Vector4d(5.74, 550.0,  200.0,   9.0));
//		points.add(new Vector4d(5.74, 550.0, 1000.0,  49.0));
//		points.add(new Vector4d(5.74, 550.0, 2000.0, 108.0));
//		points.add(new Vector4d(4.4, 450,  200.0,  14.0));
//		points.add(new Vector4d(4.4, 450, 1000.0,  76.0));
//		points.add(new Vector4d(4.4, 450, 2000.0, 175.0));
//		points.add(new Vector4d(4.15, 919.0,  200.0,  4.0));
//		points.add(new Vector4d(4.15, 919.0, 1000.0, 19.0));
//		points.add(new Vector4d(4.15, 919.0, 2000.0, 43.0));
//		points.add(new Vector4d(6.8, 423,  200.0,   15.0));
//		points.add(new Vector4d(6.8, 423, 1000.0,   81.0));
//		points.add(new Vector4d(6.8, 423, 2000.0,  176.0));
//
//		List<Vector4d> pointsCopy = new ArrayList<Vector4d>();
//		for(Vector4d p : points) {
//			pointsCopy.add(p.copy());
//		}
//		
//		Vector4d[] arr = new Vector4d[points.size()];
//		points.toArray(arr);
//		
//		
//	}

	
	
	
	
}
