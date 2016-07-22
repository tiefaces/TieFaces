package com.tiefaces.components.websheet.service;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;

public class ShiftFormula {
	// only support xlsx
	private static int _rowWrappingMask = SpreadsheetVersion.EXCEL2007
			.getLastRowIndex();

	public static Ptg[] convertSharedFormulas(Ptg[] ptgs, int shiftRow,
			int shiftStart, int shiftEnd) {
		Ptg[] newPtgStack = new Ptg[ptgs.length];

		RefPtgBase areaNPtg = null;
		AreaPtgBase var9 = null;
		Object ptg = null;
		byte originalOperandClass = 0;
		for (int k = 0; k < ptgs.length; ++k) {
			ptg = ptgs[k];
			originalOperandClass = -1;
			if (!((Ptg) ptg).isBaseToken()) {
				originalOperandClass = ((Ptg) ptg).getPtgClass();
			}

			if (ptg instanceof RefPtgBase) {
				if (ptg instanceof Ref3DPxg) {
					areaNPtg = (Ref3DPxg) ptg;
					fixupRefRelativeRow(areaNPtg, shiftRow, shiftStart,
							shiftEnd);
					ptg = areaNPtg;
				} else if (ptg instanceof Ref3DPtg) {
					areaNPtg = (Ref3DPtg) ptg;
					fixupRefRelativeRow(areaNPtg, shiftRow, shiftStart,
							shiftEnd);
					ptg = areaNPtg;
				} else {
					areaNPtg = (RefPtgBase) ptg;
					ptg = new RefPtg(fixupRelativeRow(shiftRow,
							areaNPtg.getRow(), areaNPtg.isRowRelative(),
							shiftStart, shiftEnd), areaNPtg.getColumn(),
							areaNPtg.isRowRelative(),
							areaNPtg.isColRelative());
				}
				((Ptg) ptg).setClass(originalOperandClass);
			} else if (ptg instanceof AreaPtgBase) {
				if (ptg instanceof Area3DPxg) {
					var9 = (Area3DPxg) ptg;
					fixupAreaRelativeRow(var9, shiftRow, shiftStart,
							shiftEnd);
					ptg = var9;
				} else if (ptg instanceof Area3DPxg) {
					var9 = (Area3DPtg) ptg;
					fixupAreaRelativeRow(var9, shiftRow, shiftStart,
							shiftEnd);
					ptg = var9;
				} else {
					var9 = (AreaPtgBase) ptg;
					ptg = new AreaPtg(fixupRelativeRow(shiftRow,
							var9.getFirstRow(),
							var9.isFirstRowRelative(), shiftStart,
							shiftEnd), fixupRelativeRow(shiftRow,
							var9.getLastRow(), var9.isLastRowRelative(),
							shiftStart, shiftEnd), var9.getFirstColumn(),
							var9.getLastColumn(),
							var9.isFirstRowRelative(),
							var9.isLastRowRelative(),
							var9.isFirstColRelative(),
							var9.isLastColRelative());
				}
				((Ptg) ptg).setClass(originalOperandClass);
			} else if (ptg instanceof OperandPtg) {
				ptg = ((OperandPtg) ptg).copy();
			}

			newPtgStack[k] = (Ptg) ptg;
		}

		return newPtgStack;
	}

	public static int[] getRowNumFromPtg(Object ptg) {

		RefPtgBase areaNPtg = null;
		AreaPtgBase var9 = null;

		int[] areaInt = { -1, -1 };

		if (ptg instanceof RefPtgBase) {
			if (ptg instanceof Ref3DPxg) {
				areaNPtg = (Ref3DPxg) ptg;
				areaInt[0] = areaNPtg.getRow();
			} else if (ptg instanceof Ref3DPtg) {
				areaNPtg = (Ref3DPtg) ptg;
				areaInt[0] = areaNPtg.getRow();
			} else {
				areaNPtg = (RefPtgBase) ptg;
				areaInt[0] = areaNPtg.getRow();
			}
		} else if (ptg instanceof AreaPtgBase) {
			if (ptg instanceof Area3DPxg) {
				var9 = (Area3DPxg) ptg;
				areaInt[0] = var9.getFirstRow();
				areaInt[1] = var9.getLastRow();
			} else if (ptg instanceof Area3DPxg) {
				var9 = (Area3DPtg) ptg;
				areaInt[0] = var9.getFirstRow();
				areaInt[1] = var9.getLastRow();
			} else {
				var9 = (AreaPtgBase) ptg;
				areaInt[0] = var9.getFirstRow();
				areaInt[1] = var9.getLastRow();
			}
		} else if (ptg instanceof OperandPtg) {
			ptg = ((OperandPtg) ptg).copy();
		}

		return areaInt;
	}

	protected static void fixupRefRelativeRow(RefPtgBase areaNPtg,
			int shiftRow, int shiftStart, int shiftEnd) {
		areaNPtg.setRow(fixupRelativeRow(shiftRow, areaNPtg.getRow(),
				areaNPtg.isRowRelative(), shiftStart, shiftEnd));
	}

	protected static void fixupAreaRelativeRow(AreaPtgBase var9,
			int shiftRow, int shiftStart, int shiftEnd) {
		var9.setFirstRow(fixupRelativeRow(shiftRow, var9.getFirstRow(),
				var9.isFirstRowRelative(), shiftStart, shiftEnd));
		var9.setLastRow(fixupRelativeRow(shiftRow, var9.getLastRow(),
				var9.isLastRowRelative(), shiftStart, shiftEnd));
	}

	protected static int fixupRelativeRow(int shift, int row,
			boolean relative, int shiftStart, int shiftEnd) {
		if ((relative) && (row >= shiftStart) && (row <= shiftEnd))
			return row + shift & _rowWrappingMask;
		else
			return row;

	}

}
