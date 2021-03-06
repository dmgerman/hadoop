begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|LongWritable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Job
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|RecordReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * A InputFormat that reads input data from an SQL table.  * Operates like DBInputFormat, but instead of using LIMIT and OFFSET to demarcate  * splits, it tries to generate WHERE clauses which separate the data into roughly  * equivalent shards.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DataDrivenDBInputFormat
specifier|public
class|class
name|DataDrivenDBInputFormat
parameter_list|<
name|T
extends|extends
name|DBWritable
parameter_list|>
extends|extends
name|DBInputFormat
argument_list|<
name|T
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataDrivenDBInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** If users are providing their own query, the following string is expected to       appear in the WHERE clause, which will be substituted with a pair of conditions       on the input to allow input splits to parallelise the import. */
DECL|field|SUBSTITUTE_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|SUBSTITUTE_TOKEN
init|=
literal|"$CONDITIONS"
decl_stmt|;
comment|/**    * A InputSplit that spans a set of rows    */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DataDrivenDBInputSplit
specifier|public
specifier|static
class|class
name|DataDrivenDBInputSplit
extends|extends
name|DBInputFormat
operator|.
name|DBInputSplit
block|{
DECL|field|lowerBoundClause
specifier|private
name|String
name|lowerBoundClause
decl_stmt|;
DECL|field|upperBoundClause
specifier|private
name|String
name|upperBoundClause
decl_stmt|;
comment|/**      * Default Constructor      */
DECL|method|DataDrivenDBInputSplit ()
specifier|public
name|DataDrivenDBInputSplit
parameter_list|()
block|{     }
comment|/**      * Convenience Constructor      * @param lower the string to be put in the WHERE clause to guard on the 'lower' end      * @param upper the string to be put in the WHERE clause to guard on the 'upper' end      */
DECL|method|DataDrivenDBInputSplit (final String lower, final String upper)
specifier|public
name|DataDrivenDBInputSplit
parameter_list|(
specifier|final
name|String
name|lower
parameter_list|,
specifier|final
name|String
name|upper
parameter_list|)
block|{
name|this
operator|.
name|lowerBoundClause
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|upperBoundClause
operator|=
name|upper
expr_stmt|;
block|}
comment|/**      * @return The total row count in this split      */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
comment|// unfortunately, we don't know this.
block|}
comment|/** {@inheritDoc} */
DECL|method|readFields (DataInput input)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|lowerBoundClause
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|upperBoundClause
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|write (DataOutput output)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|output
argument_list|,
name|this
operator|.
name|lowerBoundClause
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|output
argument_list|,
name|this
operator|.
name|upperBoundClause
argument_list|)
expr_stmt|;
block|}
DECL|method|getLowerClause ()
specifier|public
name|String
name|getLowerClause
parameter_list|()
block|{
return|return
name|lowerBoundClause
return|;
block|}
DECL|method|getUpperClause ()
specifier|public
name|String
name|getUpperClause
parameter_list|()
block|{
return|return
name|upperBoundClause
return|;
block|}
block|}
comment|/**    * @return the DBSplitter implementation to use to divide the table/query into InputSplits.    */
DECL|method|getSplitter (int sqlDataType)
specifier|protected
name|DBSplitter
name|getSplitter
parameter_list|(
name|int
name|sqlDataType
parameter_list|)
block|{
switch|switch
condition|(
name|sqlDataType
condition|)
block|{
case|case
name|Types
operator|.
name|NUMERIC
case|:
case|case
name|Types
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|BigDecimalSplitter
argument_list|()
return|;
case|case
name|Types
operator|.
name|BIT
case|:
case|case
name|Types
operator|.
name|BOOLEAN
case|:
return|return
operator|new
name|BooleanSplitter
argument_list|()
return|;
case|case
name|Types
operator|.
name|INTEGER
case|:
case|case
name|Types
operator|.
name|TINYINT
case|:
case|case
name|Types
operator|.
name|SMALLINT
case|:
case|case
name|Types
operator|.
name|BIGINT
case|:
return|return
operator|new
name|IntegerSplitter
argument_list|()
return|;
case|case
name|Types
operator|.
name|REAL
case|:
case|case
name|Types
operator|.
name|FLOAT
case|:
case|case
name|Types
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|FloatSplitter
argument_list|()
return|;
case|case
name|Types
operator|.
name|CHAR
case|:
case|case
name|Types
operator|.
name|VARCHAR
case|:
case|case
name|Types
operator|.
name|LONGVARCHAR
case|:
return|return
operator|new
name|TextSplitter
argument_list|()
return|;
case|case
name|Types
operator|.
name|DATE
case|:
case|case
name|Types
operator|.
name|TIME
case|:
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
operator|new
name|DateSplitter
argument_list|()
return|;
default|default:
comment|// TODO: Support BINARY, VARBINARY, LONGVARBINARY, DISTINCT, CLOB, BLOB, ARRAY
comment|// STRUCT, REF, DATALINK, and JAVA_OBJECT.
return|return
literal|null
return|;
block|}
block|}
comment|/** {@inheritDoc} */
DECL|method|getSplits (JobContext job)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|targetNumTasks
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|1
operator|==
name|targetNumTasks
condition|)
block|{
comment|// There's no need to run a bounding vals query; just return a split
comment|// that separates nothing. This can be considerably more optimal for a
comment|// large table with no index.
name|List
argument_list|<
name|InputSplit
argument_list|>
name|singletonSplit
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|singletonSplit
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputSplit
argument_list|(
literal|"1=1"
argument_list|,
literal|"1=1"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|singletonSplit
return|;
block|}
name|ResultSet
name|results
init|=
literal|null
decl_stmt|;
name|Statement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|results
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|getBoundingValsQuery
argument_list|()
argument_list|)
expr_stmt|;
name|results
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Based on the type of the results, use a different mechanism
comment|// for interpolating split points (i.e., numeric splits, text splits,
comment|// dates, etc.)
name|int
name|sqlDataType
init|=
name|results
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DBSplitter
name|splitter
init|=
name|getSplitter
argument_list|(
name|sqlDataType
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|splitter
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown SQL data type: "
operator|+
name|sqlDataType
argument_list|)
throw|;
block|}
return|return
name|splitter
operator|.
name|split
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|results
argument_list|,
name|getDBConf
argument_list|()
operator|.
name|getInputOrderBy
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// More-or-less ignore SQL exceptions here, but log in case we need it.
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|results
condition|)
block|{
name|results
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SQLException closing resultset: "
operator|+
name|se
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|statement
condition|)
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SQLException closing statement: "
operator|+
name|se
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|closeConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SQLException committing split transaction: "
operator|+
name|se
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @return a query which returns the minimum and maximum values for    * the order-by column.    *    * The min value should be in the first column, and the    * max value should be in the second column of the results.    */
DECL|method|getBoundingValsQuery ()
specifier|protected
name|String
name|getBoundingValsQuery
parameter_list|()
block|{
comment|// If the user has provided a query, use that instead.
name|String
name|userQuery
init|=
name|getDBConf
argument_list|()
operator|.
name|getInputBoundingQuery
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|userQuery
condition|)
block|{
return|return
name|userQuery
return|;
block|}
comment|// Auto-generate one based on the table name we've been provided with.
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|splitCol
init|=
name|getDBConf
argument_list|()
operator|.
name|getInputOrderBy
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"SELECT MIN("
argument_list|)
operator|.
name|append
argument_list|(
name|splitCol
argument_list|)
operator|.
name|append
argument_list|(
literal|"), "
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"MAX("
argument_list|)
operator|.
name|append
argument_list|(
name|splitCol
argument_list|)
operator|.
name|append
argument_list|(
literal|") FROM "
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
name|getDBConf
argument_list|()
operator|.
name|getInputTableName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|conditions
init|=
name|getDBConf
argument_list|()
operator|.
name|getInputConditions
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|conditions
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|" WHERE ( "
operator|+
name|conditions
operator|+
literal|" )"
argument_list|)
expr_stmt|;
block|}
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Set the user-defined bounding query to use with a user-defined query.       This *must* include the substring "$CONDITIONS"       (DataDrivenDBInputFormat.SUBSTITUTE_TOKEN) inside the WHERE clause,       so that DataDrivenDBInputFormat knows where to insert split clauses.       e.g., "SELECT foo FROM mytable WHERE $CONDITIONS"       This will be expanded to something like:         SELECT foo FROM mytable WHERE (id&gt; 100) AND (id&lt; 250)       inside each split.     */
DECL|method|setBoundingQuery (Configuration conf, String query)
specifier|public
specifier|static
name|void
name|setBoundingQuery
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|query
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|!=
name|query
condition|)
block|{
comment|// If the user's settng a query, warn if they don't allow conditions.
if|if
condition|(
name|query
operator|.
name|indexOf
argument_list|(
name|SUBSTITUTE_TOKEN
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find "
operator|+
name|SUBSTITUTE_TOKEN
operator|+
literal|" token in query: "
operator|+
name|query
operator|+
literal|"; splits may not partition data."
argument_list|)
expr_stmt|;
block|}
block|}
name|conf
operator|.
name|set
argument_list|(
name|DBConfiguration
operator|.
name|INPUT_BOUNDING_QUERY
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|createDBRecordReader (DBInputSplit split, Configuration conf)
specifier|protected
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|T
argument_list|>
name|createDBRecordReader
parameter_list|(
name|DBInputSplit
name|split
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|DBConfiguration
name|dbConf
init|=
name|getDBConf
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|T
argument_list|>
name|inputClass
init|=
call|(
name|Class
argument_list|<
name|T
argument_list|>
call|)
argument_list|(
name|dbConf
operator|.
name|getInputClass
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|dbProductName
init|=
name|getDBProductName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating db record reader for db product: "
operator|+
name|dbProductName
argument_list|)
expr_stmt|;
try|try
block|{
comment|// use database product name to determine appropriate record reader.
if|if
condition|(
name|dbProductName
operator|.
name|startsWith
argument_list|(
literal|"MYSQL"
argument_list|)
condition|)
block|{
comment|// use MySQL-specific db reader.
return|return
operator|new
name|MySQLDataDrivenDBRecordReader
argument_list|<
name|T
argument_list|>
argument_list|(
name|split
argument_list|,
name|inputClass
argument_list|,
name|conf
argument_list|,
name|createConnection
argument_list|()
argument_list|,
name|dbConf
argument_list|,
name|dbConf
operator|.
name|getInputConditions
argument_list|()
argument_list|,
name|dbConf
operator|.
name|getInputFieldNames
argument_list|()
argument_list|,
name|dbConf
operator|.
name|getInputTableName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// Generic reader.
return|return
operator|new
name|DataDrivenDBRecordReader
argument_list|<
name|T
argument_list|>
argument_list|(
name|split
argument_list|,
name|inputClass
argument_list|,
name|conf
argument_list|,
name|createConnection
argument_list|()
argument_list|,
name|dbConf
argument_list|,
name|dbConf
operator|.
name|getInputConditions
argument_list|()
argument_list|,
name|dbConf
operator|.
name|getInputFieldNames
argument_list|()
argument_list|,
name|dbConf
operator|.
name|getInputTableName
argument_list|()
argument_list|,
name|dbProductName
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// Configuration methods override superclass to ensure that the proper
comment|// DataDrivenDBInputFormat gets used.
comment|/** Note that the "orderBy" column is called the "splitBy" in this version.     * We reuse the same field, but it's not strictly ordering it -- just partitioning     * the results.     */
DECL|method|setInput (Job job, Class<? extends DBWritable> inputClass, String tableName,String conditions, String splitBy, String... fieldNames)
specifier|public
specifier|static
name|void
name|setInput
parameter_list|(
name|Job
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBWritable
argument_list|>
name|inputClass
parameter_list|,
name|String
name|tableName
parameter_list|,
name|String
name|conditions
parameter_list|,
name|String
name|splitBy
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
block|{
name|DBInputFormat
operator|.
name|setInput
argument_list|(
name|job
argument_list|,
name|inputClass
argument_list|,
name|tableName
argument_list|,
name|conditions
argument_list|,
name|splitBy
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|DataDrivenDBInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** setInput() takes a custom query and a separate "bounding query" to use       instead of the custom "count query" used by DBInputFormat.     */
DECL|method|setInput (Job job, Class<? extends DBWritable> inputClass, String inputQuery, String inputBoundingQuery)
specifier|public
specifier|static
name|void
name|setInput
parameter_list|(
name|Job
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBWritable
argument_list|>
name|inputClass
parameter_list|,
name|String
name|inputQuery
parameter_list|,
name|String
name|inputBoundingQuery
parameter_list|)
block|{
name|DBInputFormat
operator|.
name|setInput
argument_list|(
name|job
argument_list|,
name|inputClass
argument_list|,
name|inputQuery
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|DBConfiguration
operator|.
name|INPUT_BOUNDING_QUERY
argument_list|,
name|inputBoundingQuery
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|DataDrivenDBInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

