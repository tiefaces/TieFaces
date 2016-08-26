package com.tiefaces.components.websheet.configuration;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import com.tiefaces.components.websheet.service.CellHelper;
import com.tiefaces.exception.EvaluationException;


public class ExpressionHelper {
	
	
    private static String expressionNotationBegin ="${";
    private static String expressionNotationEnd = "}";
    private static Pattern expressionNotationPattern = Pattern.compile("\\$\\{[^}]*}");
    
    public static final String USER_FORMULA_PREFIX = "$[";
    public static final String USER_FORMULA_SUFFIX = "]";
    
	
	
    public static void evaluate(Map<String, Object> context, Cell cell,  ExpressionEngine engine, CellHelper cellHelper){
    	int cellType = cell.getCellType();
        Object evaluationResult = null;
        if( cellType == Cell.CELL_TYPE_STRING && cell != null){
            String strValue = cell.getStringCellValue();
            if( isUserFormula(strValue) ){
                String formulaStr = strValue.substring(2, strValue.length()-1);
                if ((formulaStr!=null)&&(!formulaStr.isEmpty())) {
                	cell.setCellFormula(formulaStr);
                }
            }else{
            	evaluationResult = evaluate(strValue, context, engine);
            	if (evaluationResult == null) {
            		evaluationResult = "";
            	} 
               	cellHelper.setCellValue(cell, evaluationResult.toString());
            }
        }
    }
    
    
    private static boolean isUserFormula(String str) {
        return str.startsWith(USER_FORMULA_PREFIX) && str.endsWith(USER_FORMULA_SUFFIX);
    }
    
	
	   public static Object evaluate(String strValue, Map<String, Object> context, ExpressionEngine engine) {
	        StringBuffer sb = new StringBuffer();
	        int beginExpressionLength = expressionNotationBegin.length();
	        int endExpressionLength = expressionNotationEnd.length();
	        Matcher exprMatcher = expressionNotationPattern.matcher(strValue);
	        String matchedString;
	        String expression;
	        Object lastMatchEvalResult = null;
	        int matchCount = 0;
	        int endOffset = 0;
	        while(exprMatcher.find()){
	            endOffset = exprMatcher.end();
	            matchCount++;
	            matchedString = exprMatcher.group();
	            expression = matchedString.substring(beginExpressionLength, matchedString.length() - endExpressionLength);
	            lastMatchEvalResult = engine.evaluate(expression, context);
	            exprMatcher.appendReplacement(sb, Matcher.quoteReplacement( lastMatchEvalResult != null ? lastMatchEvalResult.toString() : "" ));
	        }
	        String lastStringResult = lastMatchEvalResult != null ? lastMatchEvalResult.toString() : "";
	        boolean isAppendTail = matchCount == 1 && endOffset < strValue.length();
	        Object evaluationResult = null;
			if( matchCount > 1 || isAppendTail){
	            exprMatcher.appendTail(sb);
	            evaluationResult  = sb.toString();
	        }else if(matchCount == 1){
	            if(sb.length() > lastStringResult.length()){
	                evaluationResult = sb.toString();
	            }else {
	                evaluationResult = lastMatchEvalResult;
	            }
	        }else if(matchCount == 0){
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
		public static Collection transformToCollectionObject(ExpressionEngine engine, String collectionName,  Map<String, Object> context){
	        Object collectionObject = engine.evaluate(collectionName, context);
	        if( !(collectionObject instanceof Collection) ){
	            throw new EvaluationException(collectionName + " expression is not a collection");
	        }
	        return (Collection) collectionObject;
	    }
	    
	    public static Boolean isConditionTrue(ExpressionEngine engine,  Map<String, Object> context){
	        Object conditionResult = engine.evaluate(context);
	        if( !(conditionResult instanceof Boolean) ){
	            throw new EvaluationException("Condition result is not a boolean value - " + engine.getJexlExpression().getExpression());
	        }
	        return (Boolean)conditionResult;
	    }	    
	    
}
