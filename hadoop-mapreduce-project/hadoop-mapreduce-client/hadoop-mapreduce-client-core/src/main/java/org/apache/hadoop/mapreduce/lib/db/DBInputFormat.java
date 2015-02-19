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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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

begin_comment
comment|/**  * A InputFormat that reads input data from an SQL table.  *<p>  * DBInputFormat emits LongWritables containing the record number as   * key and DBWritables as value.   *   * The SQL query, and input class can be using one of the two   * setInput methods.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|DBInputFormat
specifier|public
class|class
name|DBInputFormat
parameter_list|<
name|T
extends|extends
name|DBWritable
parameter_list|>
extends|extends
name|InputFormat
argument_list|<
name|LongWritable
argument_list|,
name|T
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DBInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dbProductName
specifier|protected
name|String
name|dbProductName
init|=
literal|"DEFAULT"
decl_stmt|;
comment|/**    * A Class that does nothing, implementing DBWritable    */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NullDBWritable
specifier|public
specifier|static
class|class
name|NullDBWritable
implements|implements
name|DBWritable
implements|,
name|Writable
block|{
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|readFields (ResultSet arg0)
specifier|public
name|void
name|readFields
parameter_list|(
name|ResultSet
name|arg0
parameter_list|)
throws|throws
name|SQLException
block|{ }
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|write (PreparedStatement arg0)
specifier|public
name|void
name|write
parameter_list|(
name|PreparedStatement
name|arg0
parameter_list|)
throws|throws
name|SQLException
block|{ }
block|}
comment|/**    * A InputSplit that spans a set of rows    */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DBInputSplit
specifier|public
specifier|static
class|class
name|DBInputSplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
DECL|field|end
specifier|private
name|long
name|end
init|=
literal|0
decl_stmt|;
DECL|field|start
specifier|private
name|long
name|start
init|=
literal|0
decl_stmt|;
comment|/**      * Default Constructor      */
DECL|method|DBInputSplit ()
specifier|public
name|DBInputSplit
parameter_list|()
block|{     }
comment|/**      * Convenience Constructor      * @param start the index of the first row to select      * @param end the index of the last row to select      */
DECL|method|DBInputSplit (long start, long end)
specifier|public
name|DBInputSplit
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO Add a layer to enable SQL "sharding" and support locality
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
comment|/**      * @return The index of the first row to select      */
DECL|method|getStart ()
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/**      * @return The index of the last row to select      */
DECL|method|getEnd ()
specifier|public
name|long
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
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
name|end
operator|-
name|start
return|;
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
name|start
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|end
operator|=
name|input
operator|.
name|readLong
argument_list|()
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
name|output
operator|.
name|writeLong
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|conditions
specifier|protected
name|String
name|conditions
decl_stmt|;
DECL|field|connection
specifier|protected
name|Connection
name|connection
decl_stmt|;
DECL|field|tableName
specifier|protected
name|String
name|tableName
decl_stmt|;
DECL|field|fieldNames
specifier|protected
name|String
index|[]
name|fieldNames
decl_stmt|;
DECL|field|dbConf
specifier|protected
name|DBConfiguration
name|dbConf
decl_stmt|;
comment|/** {@inheritDoc} */
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|dbConf
operator|=
operator|new
name|DBConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|DatabaseMetaData
name|dbMeta
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|this
operator|.
name|dbProductName
operator|=
name|dbMeta
operator|.
name|getDatabaseProductName
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|tableName
operator|=
name|dbConf
operator|.
name|getInputTableName
argument_list|()
expr_stmt|;
name|fieldNames
operator|=
name|dbConf
operator|.
name|getInputFieldNames
argument_list|()
expr_stmt|;
name|conditions
operator|=
name|dbConf
operator|.
name|getInputConditions
argument_list|()
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|dbConf
operator|.
name|getConf
argument_list|()
return|;
block|}
DECL|method|getDBConf ()
specifier|public
name|DBConfiguration
name|getDBConf
parameter_list|()
block|{
return|return
name|dbConf
return|;
block|}
DECL|method|getConnection ()
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
comment|// TODO Remove this code that handles backward compatibility.
if|if
condition|(
name|this
operator|.
name|connection
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|connection
return|;
block|}
DECL|method|createConnection ()
specifier|public
name|Connection
name|createConnection
parameter_list|()
block|{
try|try
block|{
name|Connection
name|newConnection
init|=
name|dbConf
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|newConnection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|newConnection
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_SERIALIZABLE
argument_list|)
expr_stmt|;
return|return
name|newConnection
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getDBProductName ()
specifier|public
name|String
name|getDBProductName
parameter_list|()
block|{
return|return
name|dbProductName
return|;
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
try|try
block|{
comment|// use database product name to determine appropriate record reader.
if|if
condition|(
name|dbProductName
operator|.
name|startsWith
argument_list|(
literal|"ORACLE"
argument_list|)
condition|)
block|{
comment|// use Oracle-specific db reader.
return|return
operator|new
name|OracleDBRecordReader
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
name|getDBConf
argument_list|()
argument_list|,
name|conditions
argument_list|,
name|fieldNames
argument_list|,
name|tableName
argument_list|)
return|;
block|}
elseif|else
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
name|MySQLDBRecordReader
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
name|getDBConf
argument_list|()
argument_list|,
name|conditions
argument_list|,
name|fieldNames
argument_list|,
name|tableName
argument_list|)
return|;
block|}
else|else
block|{
comment|// Generic reader.
return|return
operator|new
name|DBRecordReader
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
name|getDBConf
argument_list|()
argument_list|,
name|conditions
argument_list|,
name|fieldNames
argument_list|,
name|tableName
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
comment|/** {@inheritDoc} */
DECL|method|createRecordReader (InputSplit split, TaskAttemptContext context)
specifier|public
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|T
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|createDBRecordReader
argument_list|(
operator|(
name|DBInputSplit
operator|)
name|split
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
return|;
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
name|getCountQuery
argument_list|()
argument_list|)
expr_stmt|;
name|results
operator|.
name|next
argument_list|()
expr_stmt|;
name|long
name|count
init|=
name|results
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|chunks
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
name|long
name|chunkSize
init|=
operator|(
name|count
operator|/
name|chunks
operator|)
decl_stmt|;
name|results
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
comment|// Split the rows into n-number of chunks and adjust the last chunk
comment|// accordingly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chunks
condition|;
name|i
operator|++
control|)
block|{
name|DBInputSplit
name|split
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|==
name|chunks
condition|)
name|split
operator|=
operator|new
name|DBInputSplit
argument_list|(
name|i
operator|*
name|chunkSize
argument_list|,
name|count
argument_list|)
expr_stmt|;
else|else
name|split
operator|=
operator|new
name|DBInputSplit
argument_list|(
name|i
operator|*
name|chunkSize
argument_list|,
operator|(
name|i
operator|*
name|chunkSize
operator|)
operator|+
name|chunkSize
argument_list|)
expr_stmt|;
name|splits
operator|.
name|add
argument_list|(
name|split
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|splits
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
literal|"Got SQLException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|results
operator|!=
literal|null
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
name|e1
parameter_list|)
block|{}
try|try
block|{
if|if
condition|(
name|statement
operator|!=
literal|null
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
name|e1
parameter_list|)
block|{}
name|closeConnection
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the query for getting the total number of rows,     * subclasses can override this for custom behaviour.*/
DECL|method|getCountQuery ()
specifier|protected
name|String
name|getCountQuery
parameter_list|()
block|{
if|if
condition|(
name|dbConf
operator|.
name|getInputCountQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|dbConf
operator|.
name|getInputCountQuery
argument_list|()
return|;
block|}
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"SELECT COUNT(*) FROM "
operator|+
name|tableName
argument_list|)
expr_stmt|;
if|if
condition|(
name|conditions
operator|!=
literal|null
operator|&&
name|conditions
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|query
operator|.
name|append
argument_list|(
literal|" WHERE "
operator|+
name|conditions
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Initializes the map-part of the job with the appropriate input settings.    *     * @param job The map-reduce job    * @param inputClass the class object implementing DBWritable, which is the     * Java object holding tuple fields.    * @param tableName The table to read data from    * @param conditions The condition which to select data with,     * eg. '(updated> 20070101 AND length> 0)'    * @param orderBy the fieldNames in the orderBy clause.    * @param fieldNames The field names in the table    * @see #setInput(Job, Class, String, String)    */
DECL|method|setInput (Job job, Class<? extends DBWritable> inputClass, String tableName,String conditions, String orderBy, String... fieldNames)
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
name|orderBy
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
block|{
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|DBInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|DBConfiguration
name|dbConf
init|=
operator|new
name|DBConfiguration
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|dbConf
operator|.
name|setInputClass
argument_list|(
name|inputClass
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputTableName
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputFieldNames
argument_list|(
name|fieldNames
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputConditions
argument_list|(
name|conditions
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputOrderBy
argument_list|(
name|orderBy
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the map-part of the job with the appropriate input settings.    *     * @param job The map-reduce job    * @param inputClass the class object implementing DBWritable, which is the     * Java object holding tuple fields.    * @param inputQuery the input query to select fields. Example :     * "SELECT f1, f2, f3 FROM Mytable ORDER BY f1"    * @param inputCountQuery the input query that returns     * the number of records in the table.     * Example : "SELECT COUNT(f1) FROM Mytable"    * @see #setInput(Job, Class, String, String, String, String...)    */
DECL|method|setInput (Job job, Class<? extends DBWritable> inputClass, String inputQuery, String inputCountQuery)
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
name|inputCountQuery
parameter_list|)
block|{
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|DBInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|DBConfiguration
name|dbConf
init|=
operator|new
name|DBConfiguration
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|dbConf
operator|.
name|setInputClass
argument_list|(
name|inputClass
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputQuery
argument_list|(
name|inputQuery
argument_list|)
expr_stmt|;
name|dbConf
operator|.
name|setInputCountQuery
argument_list|(
name|inputCountQuery
argument_list|)
expr_stmt|;
block|}
DECL|method|closeConnection ()
specifier|protected
name|void
name|closeConnection
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|this
operator|.
name|connection
condition|)
block|{
name|this
operator|.
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqlE
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception on close"
argument_list|,
name|sqlE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

