package com.tiefaces.components.common.sql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class SQLRunner {
	    private boolean       debug              = false;
	    private boolean       autocommit         = false;
	    private Connection    connection         = null;
	    Pattern               whiteSpacePattern  = Pattern.compile("^\\s*$",                  Pattern.CASE_INSENSITIVE);
	    Pattern               commentPattern     = Pattern.compile("^\\s*--",                 Pattern.CASE_INSENSITIVE);
	    Pattern               selectPattern      = Pattern.compile("^\\s*select\\s",          Pattern.CASE_INSENSITIVE);
	    Pattern               plSQLStartPattern  = Pattern.compile("^\\s*(declare|begin)\\s", Pattern.CASE_INSENSITIVE);
	    Pattern               plSQLEndPattern    = Pattern.compile("^\\s*/\\s*$",               Pattern.CASE_INSENSITIVE);
	    boolean               sqlExceptionThrown = false;


		public SQLRunner(Connection connection, boolean autocommit) {
			super();
			this.connection = connection;
			this.autocommit = autocommit;
		}

		private class Column
	    {
	        private String     name      = null;
	        private int        type      = 0;
	        private int        size      = 0;

	        public Column(String name, int type, int size)
	        {
	            this.name = name;
	            this.type = type;
	            this.size = size;
	        }

	        public String getName()
	        {
	            return name;
	        }

	        public void setName(String name)
	        {
	            this.name = name;
	        }

	        public int getType()
	        {
	            return type;
	        }

	        public void setType(int type)
	        {
	            this.type = type;
	        }

	        public int getSize()
	        {
	            return size;
	        }

	        public void setSize(int size)
	        {
	            this.size = size;
	        }
	        
	        public void fixSize()
	        {
	            if (getName().length() > getSize())
	            {
	                setSize(getName().length());
	            }
	            
	            if (type == java.sql.Types.TIMESTAMP)
	            {
	                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
	                
	                setSize(timeStamp.toString().length());
	            }
	        }
	    }

	    private Connection getConnection()
	    {
	        return connection;
	    }

	    private void setConnection(Connection connection)
	    {
	        this.connection = connection;
	    }

	    public boolean getSqlExceptionThrown()
	    {
	        return sqlExceptionThrown;
	    }

	    public void setSqlExceptionThrown(boolean sqlExceptionThrown)
	    {
	        this.sqlExceptionThrown = sqlExceptionThrown;
	    }

//	    public void openDatabaseConnection(boolean autoCommit) throws NamingException,
//	                                                                  SQLException
//	    {
//	        InitialContext initialContext = new InitialContext();
//	        DataSource     dataSource     = (DataSource) initialContext.lookup(this.dataSourceName);
//	        Connection     connection     = dataSource.getConnection(); 
//	            
//
//	        connection.setAutoCommit(autoCommit);
//	        
//	        setConnection(connection);
//	    }

//	    public void closeDatabaseConnection(boolean rollback) throws SQLException
//	    {
//
//	        if (rollback)
//	        {
//	            getConnection().rollback();
//	        }
//	        
//	        getConnection().close();
//	    }

	    public String runSQLs(String sourceSQL) throws Exception
	    {
	        ArrayList<String> sqlArray       = null;
	        StringBuilder     results        = new StringBuilder();
	        StringBuilder     queries        = new StringBuilder();
	        
	        debug("runSQLs");
	        debug("SQLs = "+sourceSQL);
	        
	        try
	        {
				connection.setAutoCommit(autocommit);
	            sqlArray = generateSQL(sourceSQL);
	            
	            for (String sql : sqlArray)
	            {
	                queries.append(sql + "\n");

	                if (!(isWhiteSpace(sql) || isComment(sql)))
	                {
	                    String result = runSQL(sql);

	                    results.append(result);
	                }
	            }
	        
	            debug(queries.toString());
	        }
	        finally
	        {
	        	if (!autocommit) getConnection().rollback();
	            if (getSqlExceptionThrown())
	            {
	            	System.out.println("Exception throwed: "+results);
	                throw new Exception("SQLReader exceptions");
	            }
	        }

	        return results.toString();
	    }

	    private ArrayList<String> generateSQL(String sqlSet)
	    {
	        ArrayList<String> sqlList           = split(sqlSet, ";|/");
	        ArrayList<String> sqlParts          = new ArrayList<String>();
	        StringBuilder     query             = new StringBuilder();
	        Matcher           plSQLStartMatcher = null;
	        Matcher           plSQLEndMatcher   = null;
	        boolean           plSQLMode         = false;
	        
	        for (String sql : sqlList)
	        {
	            query.append(sql);
	            
	            plSQLStartMatcher = plSQLStartPattern.matcher(sql);
	            
	            plSQLEndMatcher = plSQLEndPattern.matcher(sql);
	            
	            if (plSQLStartMatcher.find())
	            {
	                plSQLMode = true;
	            }
	            else
	            if (plSQLEndMatcher.matches())
	            {
	                plSQLMode = false;
	            }
	            
	            if (!plSQLMode)
	            {
	                sqlParts.add(chop(query.toString()));
	                
	                query = new StringBuilder();
	            }
	        }
	        
	        return sqlParts; 
	    }
	    
	    private String chop(String sql)
	    {
	        String trimmed = sql.trim();

	        return trimmed.substring(0, trimmed.length() - 1);
	    }
	    
	    private ArrayList<String> split(String sql, String splitChars)
	    {
	        ArrayList<String> splitted     = new ArrayList<String>();
	        Pattern           splitPattern = Pattern.compile(splitChars);
	        Matcher           splitMatcher = splitPattern.matcher(sql);
	        int               last_match   = 0;

	        if (sql == null)
	        {
	            sql = "";
	        }
	        else
	        {
	            while (splitMatcher.find())
	            {
	                String matched = sql.substring(last_match, splitMatcher.end());

	                splitted.add(matched);

	                last_match = splitMatcher.end();
	            }
	        }

	        return splitted;
	    }

	    private boolean isWhiteSpace(String sql)
	    {
	        Matcher whiteSpaceMatcher = whiteSpacePattern.matcher(sql);

	        return whiteSpaceMatcher.matches();
	    }

	    private boolean isComment(String sql)
	    {
	        Matcher commentMatcher = commentPattern.matcher(sql);

	        return commentMatcher.find();
	    }
	    
	    private String runSQL(String sql)
	    {
	        StringBuilder resultSet          = new StringBuilder();
	        String        results            = null;
	        
	        debug("runSQL");

	        try
	        {
	            if (isPLSQL(sql))
	            {
	                results = processPLSQL(sql);

	                resultSet.append("SQL:\n" + sql + "\n\n");
	                
	                if (results != null && !results.isEmpty())
	                {
	                    resultSet.append(results).append("\n\n");
	                }
	                else
	                {
	                    resultSet.append("Query returned no results\n\n");
	                }
	            }
	            else
	            {
	                String[] sqlArray = sql.split(";");
	                
	                for (String query : sqlArray)
	                {
	                    if (isSelect(query))
	                    {
	                        results = processSelect(query);

	                        resultSet.append("SQL:\n" + query + "\n\n");
	                        
	                        if (results != null && !results.isEmpty())
	                        {
	                            resultSet.append(results).append("\n\n");
	                        }
	                        else
	                        {
	                            resultSet.append("Query returned no results\n\n");
	                        }
	                    }
	                    else
	                    {
	                        resultSet.append("SQL:\n" + query + "\n\n");
	                        
	                        processUpdate(query);
	                    }
	                }
	            }
	            
	            getConnection().clearWarnings();
	        }
	        catch (SQLException e)
	        {
	            resultSet.append("SQL:\n" + sql + "\n\n" + getSQLExceptions(e) + "\n\n");

	            setSqlExceptionThrown(true);
	        }

	        return resultSet.toString();
	    }

	    private boolean isSelect(String sql)
	    {
	        Matcher selectMatcher = selectPattern.matcher(sql);

	        debug("isSelect");

	        return selectMatcher.find();
	    }

	    private boolean isPLSQL(String sql)
	    {
	        Matcher plSQLStartMatcher = plSQLStartPattern.matcher(sql);

	        debug("isPLSQL");
	        
	        return plSQLStartMatcher.find();
	    }
	    
	    private String processSelect(String sql) throws SQLException
	    {
	        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
	        ResultSet         resultSet         = preparedStatement.executeQuery();
	        String            results           = null;
	        
	        results = getResultSet(resultSet);

	        preparedStatement.close();
	        
	        return results;
	    }

	    private String processPLSQL(String sql) throws SQLException
	    {
	        CallableStatement callableStatement = getConnection().prepareCall(sql);
	        boolean           result            = callableStatement.execute();
	        String            results           = null;
	        
	        if (result)
	        {
	            results = getResultSet(callableStatement.getResultSet());
	        }

	        callableStatement.close();
	        
	        return results;
	    }

	    
	    private String getResultSet(ResultSet resultSet) throws SQLException
	    {
	        ArrayList<ArrayList<Object>> rows    = new ArrayList<ArrayList<Object>>();
	        String                       results = null;
	        ArrayList<Column>            columns = null;
	        
	        columns = getColumnMetaData(resultSet);
	        
	        rows = getRows(resultSet,
	                       columns);
	        
	        results = formatResultSet(rows,
	                                  columns);
	        
	        return results;
	    }

	    private ArrayList<Column> getColumnMetaData(ResultSet resultSet) throws SQLException
	    {
	        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
	        ArrayList<Column> columns           = new ArrayList<Column>();

	        debug("getColumnMetaData");
	        
	        // add 1 to the size to account for result set counting from 1
	        //
	        // this avoids having to do columnHeaders[i - 1]
	        //
	        for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i)
	        {
	            Column column = new Column(resultSetMetaData.getColumnName(i).toLowerCase(),
	                                       resultSetMetaData.getColumnType(i),
	                                       resultSetMetaData.getColumnDisplaySize(i));

	            column.fixSize();
	            
	            columns.add(column);
	        }

	        if (debug)
	        {
	            dumpColumnNames(columns);
	        }
	        
	        return columns;
	    }

	    private ArrayList<ArrayList<Object>> getRows(ResultSet         resultSet,
	                                                 ArrayList<Column> columns) throws SQLException
	    {
	        ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();

	        debug("getRows");
	        
	        while (resultSet.next())
	        {
	            debug("row(" + resultSet.getRow() + ")");
	            
	            ArrayList<Object> row = new ArrayList<Object>();

	            for (Column column : columns)
	            {
	                switch (column.getType())
	                {
	                    case java.sql.Types.BLOB:
	                        //row.add((Object) resultSet.getBytes(column.getName()));
	                        row.add((Object) resultSet.getBlob(column.getName()));
	                    break;

	                    case java.sql.Types.CLOB:
	                        //row.add((Object) resultSet.getBytes(column.getName()));
	                        row.add((Object) resultSet.getClob(column.getName()));
	                    break;

	                    case java.sql.Types.DATE:
	                        row.add((Object) resultSet.getDate(column.getName()));
	                        break;

	                    case java.sql.Types.NUMERIC:
	                        row.add((Object) resultSet.getBigDecimal(column.getName()));
	                        break;

	                    case java.sql.Types.TIME:
	                        row.add((Object) resultSet.getTime(column.getName()));
	                        break;

	                    case java.sql.Types.TIMESTAMP:
	                        row.add((Object) resultSet.getTimestamp(column.getName()));
	                        break;

	                    case java.sql.Types.VARCHAR:
	                        row.add((Object) resultSet.getString(column.getName()));
	                        break;
	                }
	            }

	            rows.add(row);
	        }

	        return rows;
	    }

	    private String formatResultSet(ArrayList<ArrayList<Object>> rows,
	                                   ArrayList<Column>            columns) throws SQLException
	    {
	        StringBuilder results = new StringBuilder();

	        debug("formatResultSet");

	        for (Column column : columns)
	        {
	            results.append(formatColumn(column.getSize(), column.getName()));
	        }

	        results.setCharAt(results.length() - 1, '\n');
	        
	        for (Column column : columns)
	        {
	            results.append(genChars(column.getSize(), '=')).append("\t");
	        }

	        results.setCharAt(results.length() - 1, '\n');
	        
	        for (ArrayList<Object> row : rows)
	        {
	            for (int i = 0; i < row.size(); ++i)
	            {
	                String value       = null;
	                String unPrintable = null;

	                switch (columns.get(i).getType())
	                {
	                    case java.sql.Types.BLOB:
	                        Blob blob = (Blob) row.get(i);
	                        unPrintable = new String(blob.getBytes((long) 1, (int) blob.length()));
	                        value = (row.get(i) == null) ? "" : unPrintable.replaceAll("\\p{Cntrl}", "?");
	                    break;

	                    case java.sql.Types.CLOB:
	                        Clob clob = (Clob) row.get(i);
	                        value = clob.getSubString((long) 1, (int) clob.length()); 
	                    break;

	                    case java.sql.Types.DATE:
	                        value = (row.get(i) == null) ? "" : dateFormat((Date) row.get(i));
	                        break;

	                    case java.sql.Types.NUMERIC:
	                        value = (row.get(i) == null) ? "" : numericFormat((BigDecimal) row.get(i));
	                        break;

	                    case java.sql.Types.TIME:
	                        value = (row.get(i) == null) ? "" : timeFormat((Time) row.get(i));
	                        break;

	                    case java.sql.Types.TIMESTAMP:
	                        value = (row.get(i) == null) ? "" : timeStampFormat((Timestamp) row.get(i));
	                        break;

	                    case java.sql.Types.CHAR:
	                        value = (row.get(i) == null) ? "" : ((String) row.get(i));
	                        break;

	                    case java.sql.Types.VARCHAR:
	                        value = (row.get(i) == null) ? "" : ((String) row.get(i));
	                        break;

	                    default:
	                        value = "Unsupported type";
	                        break;
	                }

	                results.append(formatColumn(columns.get(i).getSize(), value));
	            }

	            results.setCharAt(results.length() - 1, '\n');
	        }
	        
	        return results.toString();
	    }

	    private String dateFormat(Date date)
	    {
	        return date.toString();
	    }

	    private String numericFormat(BigDecimal bigDecimal)
	    {
	        return bigDecimal.toString();
	    }

	    private String timeFormat(Time time)
	    {
	        return time.toString();
	    }

	    private String timeStampFormat(Timestamp timeStamp)
	    {
	        return timeStamp.toString();
	    }
	    
	    private String formatColumn(int    available,
	                                String value)
	    {
	        int    used      = value.length();  
	        int    generate  = 0;
	        String formatted = null;
	        
	        if (used > available)
	        {
	            value = value.substring(0, available - 1);
	        }

	        generate = (available - used) > 0 ? available - used : 0;  

	        if (generate > 0)
	        {
	            formatted = value + genChars(generate, ' ') + "\t";
	        }
	        else
	        {
	            formatted = value + "\t";
	        }

	        return formatted;
	    }

	    private String genChars(int count, char type)
	    {
	        char[] chars = new char[count];

	        Arrays.fill(chars, type);
	        
	        return new String(chars);
	    }
	    
	    private void processUpdate(String sql) throws SQLException
	    {
	        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
	        
	        debug("processUpdate");

	        preparedStatement.executeUpdate();

	        preparedStatement.close();
	    }

	    private String getSQLExceptions(SQLException e)
	    {
	        StringBuilder errorMsg = new StringBuilder();

	        if (e != null)
	        {
	            do
	            {
	                errorMsg.append("OracleError("   + e.getErrorCode()      + ")\n" +
	                                "SQLState("      + e.getSQLState()       + ")\n" +
	                                "SQL Exception(" + e.getMessage().trim() + ")\n");
	            }
	            while ((e = e.getNextException()) != null);
	        }

	        return errorMsg.toString();
	    }

	    private void debug(String msg)
	    {
	        if (debug)
	        {
	            System.out.println("SQLRunner: " + msg);
	        }
	    }

	    public void dumpColumnNames(ArrayList<Column> columns)
	    {
	        debug("number of columns(" + columns.size() + ")");

	        for (Column column : columns)
	        {
	            debug("column(" + column.getName() + ", " + column.getType() + ", " + column.getSize() + ")");
	        }
	    }
	}
