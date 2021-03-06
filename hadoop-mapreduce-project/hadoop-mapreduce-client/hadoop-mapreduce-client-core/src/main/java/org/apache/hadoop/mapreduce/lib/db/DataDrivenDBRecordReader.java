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

begin_comment
comment|/**  * A RecordReader that reads records from a SQL table,  * using data-driven WHERE clause splits.  * Emits LongWritables containing the record number as  * key and DBWritables as value.  */
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
DECL|class|DataDrivenDBRecordReader
specifier|public
class|class
name|DataDrivenDBRecordReader
parameter_list|<
name|T
extends|extends
name|DBWritable
parameter_list|>
extends|extends
name|DBRecordReader
argument_list|<
name|T
argument_list|>
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
name|DataDrivenDBRecordReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dbProductName
specifier|private
name|String
name|dbProductName
decl_stmt|;
comment|// database manufacturer string.
comment|/**    * @param split The InputSplit to read data for    * @throws SQLException     */
DECL|method|DataDrivenDBRecordReader (DBInputFormat.DBInputSplit split, Class<T> inputClass, Configuration conf, Connection conn, DBConfiguration dbConfig, String cond, String [] fields, String table, String dbProduct)
specifier|public
name|DataDrivenDBRecordReader
parameter_list|(
name|DBInputFormat
operator|.
name|DBInputSplit
name|split
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|inputClass
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|DBConfiguration
name|dbConfig
parameter_list|,
name|String
name|cond
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|String
name|table
parameter_list|,
name|String
name|dbProduct
parameter_list|)
throws|throws
name|SQLException
block|{
name|super
argument_list|(
name|split
argument_list|,
name|inputClass
argument_list|,
name|conf
argument_list|,
name|conn
argument_list|,
name|dbConfig
argument_list|,
name|cond
argument_list|,
name|fields
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|dbProductName
operator|=
name|dbProduct
expr_stmt|;
block|}
comment|/** Returns the query for selecting the records,    * subclasses can override this for custom behaviour.*/
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getSelectQuery ()
specifier|protected
name|String
name|getSelectQuery
parameter_list|()
block|{
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
name|dataSplit
init|=
operator|(
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
operator|)
name|getSplit
argument_list|()
decl_stmt|;
name|DBConfiguration
name|dbConf
init|=
name|getDBConf
argument_list|()
decl_stmt|;
name|String
index|[]
name|fieldNames
init|=
name|getFieldNames
argument_list|()
decl_stmt|;
name|String
name|tableName
init|=
name|getTableName
argument_list|()
decl_stmt|;
name|String
name|conditions
init|=
name|getConditions
argument_list|()
decl_stmt|;
comment|// Build the WHERE clauses associated with the data split first.
comment|// We need them in both branches of this function.
name|StringBuilder
name|conditionClauses
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|conditionClauses
operator|.
name|append
argument_list|(
literal|"( "
argument_list|)
operator|.
name|append
argument_list|(
name|dataSplit
operator|.
name|getLowerClause
argument_list|()
argument_list|)
expr_stmt|;
name|conditionClauses
operator|.
name|append
argument_list|(
literal|" ) AND ( "
argument_list|)
operator|.
name|append
argument_list|(
name|dataSplit
operator|.
name|getUpperClause
argument_list|()
argument_list|)
expr_stmt|;
name|conditionClauses
operator|.
name|append
argument_list|(
literal|" )"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbConf
operator|.
name|getInputQuery
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// We need to generate the entire query.
name|query
operator|.
name|append
argument_list|(
literal|"SELECT "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|append
argument_list|(
name|fieldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|fieldNames
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|query
operator|.
name|append
argument_list|(
literal|" FROM "
argument_list|)
operator|.
name|append
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dbProductName
operator|.
name|startsWith
argument_list|(
literal|"ORACLE"
argument_list|)
condition|)
block|{
comment|// Seems to be necessary for hsqldb? Oracle explicitly does *not*
comment|// use this clause.
name|query
operator|.
name|append
argument_list|(
literal|" AS "
argument_list|)
operator|.
name|append
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|append
argument_list|(
literal|" WHERE "
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
block|{
comment|// Put the user's conditions first.
name|query
operator|.
name|append
argument_list|(
literal|"( "
argument_list|)
operator|.
name|append
argument_list|(
name|conditions
argument_list|)
operator|.
name|append
argument_list|(
literal|" ) AND "
argument_list|)
expr_stmt|;
block|}
comment|// Now append the conditions associated with our split.
name|query
operator|.
name|append
argument_list|(
name|conditionClauses
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// User provided the query. We replace the special token with our WHERE clause.
name|String
name|inputQuery
init|=
name|dbConf
operator|.
name|getInputQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputQuery
operator|.
name|indexOf
argument_list|(
name|DataDrivenDBInputFormat
operator|.
name|SUBSTITUTE_TOKEN
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not find the clause substitution token "
operator|+
name|DataDrivenDBInputFormat
operator|.
name|SUBSTITUTE_TOKEN
operator|+
literal|" in the query: ["
operator|+
name|inputQuery
operator|+
literal|"]. Parallel splits may not work correctly."
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|append
argument_list|(
name|inputQuery
operator|.
name|replace
argument_list|(
name|DataDrivenDBInputFormat
operator|.
name|SUBSTITUTE_TOKEN
argument_list|,
name|conditionClauses
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using query: "
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

