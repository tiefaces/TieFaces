package com.tiefaces.components.websheet.configuration;

import static com.tiefaces.components.websheet.TieWebSheetConstants.COPY_SHEET_PREFIX;
import static com.tiefaces.components.websheet.TieWebSheetConstants.EXCEL_SHEET_NAME_LIMIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.tiefaces.components.websheet.dataobjects.CachedCells;
import com.tiefaces.components.websheet.dataobjects.FormulaMapping;
import com.tiefaces.components.websheet.service.CellHelper;
import com.tiefaces.components.websheet.service.ShiftFormula;
import com.tiefaces.exception.EvaluationException;

public class ConfigurationHelper {

	private static String expressionNotationBegin = "${";
	private static String expressionNotationEnd = "}";
	private static Pattern expressionNotationPattern = Pattern
			.compile("\\$\\{[^}]*}");

	public static final String USER_FORMULA_PREFIX = "$[";
	public static final String USER_FORMULA_SUFFIX = "]";

	public static final int hiddenFullNameColumn = 255;

	public static final String EACH_COMMAND_FULL_NAME_PREFIX = "E.";
	public static final String FORM_COMMAND_FULL_NAME_PREFIX = "F.";

	public static void evaluate(Map<String, Object> context, Cell cell,
			ExpressionEngine engine, CellHelper cellHelper) {
		int cellType = cell.getCellType();
		Object evaluationResult = null;
		if (cellType == Cell.CELL_TYPE_STRING && cell != null) {
			String strValue = cell.getStringCellValue();
			if (isUserFormula(strValue)) {
				String formulaStr = strValue.substring(2,
						strValue.length() - 1);
				if ((formulaStr != null) && (!formulaStr.isEmpty())) {
					cell.setCellFormula(formulaStr);
				}
			} else {
				evaluationResult = evaluate(strValue, context, engine);
				if (evaluationResult == null) {
					evaluationResult = "";
				}
				cellHelper
						.setCellValue(cell, evaluationResult.toString());
			}
		}
	}

	private static boolean isUserFormula(String str) {
		return str.startsWith(USER_FORMULA_PREFIX)
				&& str.endsWith(USER_FORMULA_SUFFIX);
	}

	public static Object evaluate(String strValue,
			Map<String, Object> context, ExpressionEngine engine) {
		StringBuffer sb = new StringBuffer();
		int beginExpressionLength = expressionNotationBegin.length();
		int endExpressionLength = expressionNotationEnd.length();
		Matcher exprMatcher = expressionNotationPattern.matcher(strValue);
		String matchedString;
		String expression;
		Object lastMatchEvalResult = null;
		int matchCount = 0;
		int endOffset = 0;
		while (exprMatcher.find()) {
			endOffset = exprMatcher.end();
			matchCount++;
			matchedString = exprMatcher.group();
			expression = matchedString.substring(beginExpressionLength,
					matchedString.length() - endExpressionLength);
			lastMatchEvalResult = engine.evaluate(expression, context);
			exprMatcher
					.appendReplacement(
							sb,
							Matcher.quoteReplacement(lastMatchEvalResult != null ? lastMatchEvalResult
									.toString() : ""));
		}
		String lastStringResult = lastMatchEvalResult != null ? lastMatchEvalResult
				.toString() : "";
		boolean isAppendTail = matchCount == 1
				&& endOffset < strValue.length();
		Object evaluationResult = null;
		if (matchCount > 1 || isAppendTail) {
			exprMatcher.appendTail(sb);
			evaluationResult = sb.toString();
		} else if (matchCount == 1) {
			if (sb.length() > lastStringResult.length()) {
				evaluationResult = sb.toString();
			} else {
				evaluationResult = lastMatchEvalResult;
			}
		} else if (matchCount == 0) {
			evaluationResult = strValue;
		}
		return evaluationResult;
	}

	public static int checkCellTypeFromResult(Object result) {
		int type = Cell.CELL_TYPE_STRING;
		if (result instanceof Number) {
			type = Cell.CELL_TYPE_NUMERIC;
		} else if (result instanceof Boolean) {
			type = Cell.CELL_TYPE_BOOLEAN;
		} else if (result instanceof Date) {
			type = Cell.CELL_TYPE_NUMERIC;
		}
		return type;
	}

	@SuppressWarnings("rawtypes")
	public static Collection transformToCollectionObject(
			ExpressionEngine engine, String collectionName,
			Map<String, Object> context) {
		Object collectionObject = engine
				.evaluate(collectionName, context);
		if (!(collectionObject instanceof Collection)) {
			throw new EvaluationException(collectionName
					+ " expression is not a collection");
		}
		return (Collection) collectionObject;
	}

	public static Boolean isConditionTrue(ExpressionEngine engine,
			Map<String, Object> context) {
		Object conditionResult = engine.evaluate(context);
		if (!(conditionResult instanceof Boolean)) {
			throw new EvaluationException(
					"Condition result is not a boolean value - "
							+ engine.getJexlExpression().getExpression());
		}
		return (Boolean) conditionResult;
	}

	public static boolean isRowAllowAdd(Row row,
			SheetConfiguration sheetConfig) {
		String fullName = getFullNameFromRow(row);
		if (fullName != null) {
			int index = fullName.indexOf(":");
			if (index > 0) {
				fullName = fullName.substring(index + 1);
				ConfigRangeAttrs attrs = sheetConfig.getShiftMap().get(
						fullName);
				if ((attrs != null)
						&& (attrs.allowAdd)
						&& (row.getRowNum() == attrs.firstRowRef
								.getRowIndex())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getFullNameFromRow(Row row) {
		if (row != null) {
			Cell cell = row.getCell(hiddenFullNameColumn);
			if (cell != null) {
				return cell.getStringCellValue();
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addRow(ConfigBuildRef configBuildRef, int rowIndex,
			SheetConfiguration sheetConfig,
			Map<String, Object> dataContext) {
		String fullName = getFullNameFromRow(configBuildRef.getSheet()
				.getRow(rowIndex));
		if (fullName == null) {
			return -1;
		}
		String[] parts = fullName.split(":");
		if (parts == null) {
			return -1;
		}

		int originRowIndex = Integer.parseInt(parts[0]);

		fullName = fullName.substring(fullName.indexOf(":") + 1);

		Collection lastCollection = null;
		int lastCollectionIndex = -1;
		EachCommand eachCommand = null;
		// replace the lastCollection.
		// since here's add one row.
		// Then we should insert one empty object in the list.
		// The collection must be a list to support add/delete function.
		// and the object must support empty constructor.
		try {
			// prepare collection data in context.
			// must loop through the full name which may have multiple
			// layer.
			// i.e. E.department.1:E.employee.0
			// need prepare department.1 and employee.0
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if (part.startsWith(EACH_COMMAND_FULL_NAME_PREFIX)) {
					String[] varparts = part.split("\\.");
					eachCommand = getEachCommandFromPartsName(configBuildRef,varparts); 
					lastCollection = transformToCollectionObject(
							configBuildRef.getEngine(), eachCommand.getItems(),
							dataContext);
					lastCollectionIndex = prepareCollectionDataInContext(
							varparts, configBuildRef, eachCommand,
							lastCollection, dataContext);
				}
			}
			if (lastCollectionIndex < 0) {
				// no each command in the loop.
				return 0;
			}

			String unitFullName = insertEmptyObjectInContext(fullName,
					lastCollection, eachCommand, lastCollectionIndex,
					dataContext);
			RowsMapping unitRowsMapping = new RowsMapping();
			ConfigRangeAttrs savedRangeAttrs = configBuildRef
					.getShiftMap().get(fullName);
			int insertPosition = savedRangeAttrs.firstRowRef.getRowIndex() + savedRangeAttrs.finalLengh;
			insertEachTemplate(eachCommand.getConfigRange(),
					configBuildRef, lastCollectionIndex + 1,
					insertPosition, unitRowsMapping);
			ConfigRange currentRange = buildCurrentRange(
					eachCommand.getConfigRange(),
					configBuildRef.getSheet(), insertPosition);
			List<RowsMapping> currentRowsMappingList = findUpperRowsMappingFromShiftMap(
					parts, configBuildRef.getShiftMap());
			currentRowsMappingList.add(unitRowsMapping);
			currentRange.getAttrs().allowAdd = true;
			configBuildRef.setBodyAllowAdd(true);
			// reverse order of changeMap.
			Map<String, String> changeMap = new TreeMap<String, String>(
					Collections.reverseOrder());
			increaseIndexNumberInHiddenColumn(configBuildRef,
					currentRange.getAttrs().lastRowPlusRef.getRowIndex(),
					fullName, changeMap);
			increaseIndexNumberInShiftMap(configBuildRef.getShiftMap(),
					changeMap);
			configBuildRef.putShiftAttrs(unitFullName,
					currentRange.getAttrs(), unitRowsMapping);
			int length = currentRange.buildAt(unitFullName,
					configBuildRef, insertPosition, dataContext,
					currentRowsMappingList);
			currentRange.getAttrs().finalLengh = length;
			
			reBuildUpperLevelFormula(configBuildRef, fullName);
			increaseUpperLevelFinalLength(
					configBuildRef.getShiftMap(),
					fullName, 
					length);			
			insertPosition += length;
			currentRowsMappingList.remove(unitRowsMapping);
			dataContext.remove(eachCommand.getVar());

			return 1;

		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String insertEmptyObjectInContext(String fullName,
			Collection lastCollection, EachCommand eachCommand,
			int lastCollectionIndex, Map<String, Object> dataContext)
			throws Exception {
		if (!(lastCollection instanceof List)) {
			throw new EvaluationException(
					eachCommand.getVar()
							+ " is not a list collection, cannot support add/delete function.");
		}
		List collectionList = (List) lastCollection;
		// the object must support empty constructor.
		Object currentObj = collectionList.get(lastCollectionIndex);
		Object insertObj = currentObj.getClass().newInstance();
		collectionList.add(lastCollectionIndex + 1, insertObj);
		dataContext.put(eachCommand.getVar(), insertObj);
		return 	fullName.substring(0, fullName.lastIndexOf(".") + 1) + (lastCollectionIndex + 1);

	}

	private static EachCommand getEachCommandFromPartsName(
			ConfigBuildRef configBuildRef, String[] varparts) {
		if (varparts.length == 3) {
			return (EachCommand) configBuildRef.getCommandIndexMap().get(
					EACH_COMMAND_FULL_NAME_PREFIX + varparts[1]);
		}
		return null;

	}

	private static int prepareCollectionDataInContext(String[] varparts,
			ConfigBuildRef configBuildRef, EachCommand eachCommand,
			Collection collection, Map<String, Object> dataContext) {
		if (varparts.length == 3) {
			int collectionIndex = Integer.parseInt(varparts[2]);
			Object obj = findItemInCollection(collection, collectionIndex);
			if (obj != null) {
				dataContext.put(varparts[1], obj);
				return collectionIndex;
			}
		}
		return -1;
	}

	public static void reBuildUpperLevelFormula(
			ConfigBuildRef configBuildRef, String addFullName) {
		Map<Cell, String> cachedMap = configBuildRef
				.getCachedCells();
		Map<String, List<RowsMapping>> rowsMap = new HashMap<String, List<RowsMapping>>();
		for (Map.Entry<Cell, String> entry : cachedMap.entrySet()) {
			Cell cell = entry.getKey();
			String originFormula = entry.getValue();
			if (originFormula != null) {
				String fullName = getFullNameFromRow(cell.getRow());
				fullName = fullName.substring(fullName.indexOf(":") + 1);
				// it's upper level
				if (addFullName.startsWith(fullName +":"))
				 {
					List<RowsMapping> currentRowsMappingList = rowsMap
							.get(fullName);
					if (currentRowsMappingList == null) {
						currentRowsMappingList = gatherRowsMappingByFullName(
								configBuildRef, fullName);
						rowsMap.put(fullName, currentRowsMappingList);
					}
					ShiftFormulaRef shiftFormulaRef = new ShiftFormulaRef(
							configBuildRef.getWatchList(),
							currentRowsMappingList);
					shiftFormulaRef.setFormulaChanged(0);
					buildCellFormulaForShiftedRows(configBuildRef.getSheet(),
							configBuildRef.getWbWrapper(), shiftFormulaRef,
							cell, originFormula);
					if (shiftFormulaRef.getFormulaChanged() > 0) {
						configBuildRef.getCachedCells().put(cell,
								originFormula);
					}
	
				}
			}	
		}

	}

	public static void buildCellFormulaForShiftedRows(final Sheet sheet,
			final XSSFEvaluationWorkbook wbWrapper,
			final ShiftFormulaRef shiftFormulaRef, Cell cell, 
			final String originFormula) {
		// only shift when there's watchlist exist.
		if ((shiftFormulaRef.getWatchList() != null)
				&& (shiftFormulaRef.getWatchList().size() > 0)) {
			Ptg[] ptgs = FormulaParser.parse(originFormula,
					wbWrapper, FormulaType.CELL, sheet.getWorkbook()
							.getSheetIndex(sheet));
			Ptg[] convertedFormulaPtg = ShiftFormula
					.convertSharedFormulas(ptgs, shiftFormulaRef);
			if (shiftFormulaRef.getFormulaChanged() > 0) {
				// only change formula when indicator is true
				cell.setCellFormula(FormulaRenderer.toFormulaString(
						wbWrapper, convertedFormulaPtg));

			}
		}
	}

	public static List<RowsMapping> gatherRowsMappingByFullName(
			ConfigBuildRef configBuildRef, String fullName) {
		List<RowsMapping> list = new ArrayList<RowsMapping>();
		Map<String, ConfigRangeAttrs> shiftMap = configBuildRef
				.getShiftMap();
		for (Map.Entry<String, ConfigRangeAttrs> entry : shiftMap
				.entrySet()) {
			String fname = entry.getKey();
			if (fname.startsWith(fullName+":")||fname.equals(fullName)) {
				ConfigRangeAttrs attrs = entry.getValue();
				list.add(attrs.unitRowsMapping);
			}
		}
		return list;
	}

	public static void increaseIndexNumberInShiftMap(
			Map<String, ConfigRangeAttrs> shiftMap,
			Map<String, String> changeMap) {
		for (Map.Entry<String, String> entry : changeMap.entrySet()) {
			String key = entry.getKey();
			String newKey = entry.getValue();
			ConfigRangeAttrs attrs = shiftMap.get(key);
			if (attrs != null) {
				shiftMap.remove(key);
				shiftMap.put(newKey, attrs);
			}
		}
	}

	public static void increaseUpperLevelFinalLength(
			Map<String, ConfigRangeAttrs> shiftMap,
			String addedFullName, 
			int increasedLength) {
		String[] parts = addedFullName.split(":");
		String fname = null;
		for (int i=0; i< (parts.length - 1); i++) {
			if (i==0) {
				fname = parts[i];
			} else {
				fname = fname +":" + parts[i];
			}
			shiftMap.get(fname).finalLengh += increasedLength;
		}
	}

	public static void increaseIndexNumberInHiddenColumn(
			ConfigBuildRef configBuildRef, int startRowIndex,
			String fullName, Map<String, String> changeMap) {
		String searchName = fullName.substring(0,
				fullName.lastIndexOf(".") + 1);
		Sheet sheet = configBuildRef.getSheet();
		for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String fname = getFullNameFromRow(row);
			int sindex = fname.indexOf(searchName);
			// no search found, then no need to change.
			if (sindex < 0) {
				break;
			}
			String snum = fname.substring(sindex + searchName.length());
			int sufindex = snum.indexOf(":");
			String suffix = "";
			if (sufindex > 0) {
				snum = snum.substring(0, sufindex - 1);
				suffix = ":";
			}
			int increaseNum = Integer.parseInt(snum) + 1;
			String realFullName = fname.substring(sindex);
			String changeName = fname.replace((searchName + snum + suffix),
					(searchName + increaseNum + suffix));
			if (changeMap.get(realFullName) == null) {
				changeMap.put(realFullName, changeName.substring(sindex));
			}
			setFullNameInHiddenColumn(row, changeName);
		}
	}

	public static void setFullNameInHiddenColumn(Row row, String fullName) {
		Cell cell = row.getCell(hiddenFullNameColumn,
				Row.CREATE_NULL_AS_BLANK);
		String rowNum = cell.getStringCellValue();
		System.out.println("set fullname hidden rownum = " + rowNum
				+ " fullName = " + fullName);
		cell.setCellValue(rowNum + fullName);
	}

	public static List<RowsMapping> findUpperRowsMappingFromShiftMap(
			String[] parts, Map<String, ConfigRangeAttrs> shiftMap) {

		String fullName = null;
		List<RowsMapping> rowsMappingList = new ArrayList<RowsMapping>();
		for (int i=1; i< parts.length -1; i++) {
			String part =  parts[i];
			if (fullName == null) {
				fullName = part;
			} else {
				fullName = fullName + ":" + part;
			}
			if (fullName != null) {
				ConfigRangeAttrs rangeAttrs = shiftMap.get(fullName);
				rowsMappingList.add(rangeAttrs.unitRowsMapping);
			}
		}
		return rowsMappingList;
	}

	@SuppressWarnings("rawtypes")
	public static Object findItemInCollection(Collection collection,
			int index) {
		if (index >= 0) {
			if (collection instanceof List) {
				List list = (List) collection;
				return list.get(index);
			} else {
				int i = 0;
				for (Object object : collection) {
					if (i == index) {
						return object;
					}
					i++;
				}
			}
		}
		return null;
	}

	public static void insertEachTemplate(ConfigRange sourceConfigRange,
			ConfigBuildRef configBuildRef, int index, int insertPosition,
			RowsMapping unitRowsMapping) {
		// TODO Auto-generated method stub
		int srcStartRow = sourceConfigRange.getFirstRowAddr().getRow();
		int srcEndRow = sourceConfigRange.getLastRowPlusAddr().getRow() - 1;

		Sheet sheet = configBuildRef.getSheet();
		CellHelper cellHelper = configBuildRef.getCellHelper();

		Workbook wb = sheet.getWorkbook();
		// excel sheet name has limit 31 chars
		String copyName = (COPY_SHEET_PREFIX + sheet.getSheetName());
		if (copyName.length() > EXCEL_SHEET_NAME_LIMIT) {
			copyName = copyName.substring(0, EXCEL_SHEET_NAME_LIMIT);
		}
		Sheet srcSheet = wb.getSheet(copyName);
		if (index > 0) {
			cellHelper.copyRows(sheet.getWorkbook(),
					configBuildRef.getWbWrapper(), srcSheet, sheet,
					srcStartRow, srcEndRow, insertPosition, false, true);
		}

		for (int rowIndex = srcStartRow; rowIndex <= srcEndRow; rowIndex++) {
			if (configBuildRef.getWatchList().contains(rowIndex)
					&& (isStaticRow(sourceConfigRange, rowIndex))) {
				unitRowsMapping.addRow(
						rowIndex,
						sheet.getRow(insertPosition + rowIndex
								- srcStartRow));
			}
		}
	}

	public static ConfigRange buildCurrentRange(
			ConfigRange sourceConfigRange, Sheet sheet, int insertPosition) {
		ConfigRange current = new ConfigRange(sourceConfigRange);
		int shiftNum = insertPosition
				- sourceConfigRange.getFirstRowAddr().getRow();
		current.shiftRowRef(sheet, shiftNum);
		return current;
	}

	/**
	 * Whether the row is static. This only check rowIndex against original
	 * template.
	 * 
	 * @param row
	 *            the row for check.
	 * @return true is static false is not.
	 */
	public static boolean isStaticRow(ConfigRange sourceConfigRange,
			int rowIndex) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList().size(); i++) {
				Command command = sourceConfigRange.getCommandList().get(
						i);
				if ((rowIndex >= command.getConfigRange()
						.getFirstRowAddr().getRow())
						&& (rowIndex < (command.getConfigRange()
								.getLastRowPlusAddr().getRow()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Whether the row is static.This check row after shifted.
	 * 
	 * @param row
	 *            the row for check.
	 * @return true is static false is not.
	 */
	public static boolean isStaticRowRef(ConfigRange sourceConfigRange,
			Row row) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList().size(); i++) {
				Command command = sourceConfigRange.getCommandList().get(
						i);
				int rowIndex = row.getRowNum();
				if ((rowIndex >= command.getTopRow())
						&& (rowIndex < (command.getTopRow() + command
								.getFinalLength()))) {
					return false;
				}
			}
		}
		return true;
	}

	public static void indexCommandRange(ConfigRange sourceConfigRange,
			Map<String, Command> indexMap) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList().size(); i++) {
				// cellRange.resetChangeMatrix();
				Command command = sourceConfigRange.getCommandList().get(
						i);
				indexMap.put(command.getCommandName(), command);
				command.getConfigRange().indexCommandRange(indexMap);
			}
		}

	}
}
