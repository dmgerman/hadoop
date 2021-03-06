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
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|Date
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
name|MRJobConfig
import|;
end_import

begin_comment
comment|/**  * Implement DBSplitter over date/time values.  * Make use of logic from IntegerSplitter, since date/time are just longs  * in Java.  */
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
DECL|class|DateSplitter
specifier|public
class|class
name|DateSplitter
extends|extends
name|IntegerSplitter
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
name|DateSplitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|split (Configuration conf, ResultSet results, String colName)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|split
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResultSet
name|results
parameter_list|,
name|String
name|colName
parameter_list|)
throws|throws
name|SQLException
block|{
name|long
name|minVal
decl_stmt|;
name|long
name|maxVal
decl_stmt|;
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
name|minVal
operator|=
name|resultSetColToLong
argument_list|(
name|results
argument_list|,
literal|1
argument_list|,
name|sqlDataType
argument_list|)
expr_stmt|;
name|maxVal
operator|=
name|resultSetColToLong
argument_list|(
name|results
argument_list|,
literal|2
argument_list|,
name|sqlDataType
argument_list|)
expr_stmt|;
name|String
name|lowClausePrefix
init|=
name|colName
operator|+
literal|">= "
decl_stmt|;
name|String
name|highClausePrefix
init|=
name|colName
operator|+
literal|"< "
decl_stmt|;
name|int
name|numSplits
init|=
name|conf
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
name|numSplits
operator|<
literal|1
condition|)
block|{
name|numSplits
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|minVal
operator|==
name|Long
operator|.
name|MIN_VALUE
operator|&&
name|maxVal
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
comment|// The range of acceptable dates is NULL to NULL. Just create a single split.
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
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|colName
operator|+
literal|" IS NULL"
argument_list|,
name|colName
operator|+
literal|" IS NULL"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|splits
return|;
block|}
comment|// Gather the split point integers
name|List
argument_list|<
name|Long
argument_list|>
name|splitPoints
init|=
name|split
argument_list|(
name|numSplits
argument_list|,
name|minVal
argument_list|,
name|maxVal
argument_list|)
decl_stmt|;
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
comment|// Turn the split points into a set of intervals.
name|long
name|start
init|=
name|splitPoints
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Date
name|startDate
init|=
name|longToDate
argument_list|(
name|start
argument_list|,
name|sqlDataType
argument_list|)
decl_stmt|;
if|if
condition|(
name|sqlDataType
operator|==
name|Types
operator|.
name|TIMESTAMP
condition|)
block|{
comment|// The lower bound's nanos value needs to match the actual lower-bound nanos.
try|try
block|{
operator|(
operator|(
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|)
name|startDate
operator|)
operator|.
name|setNanos
argument_list|(
name|results
operator|.
name|getTimestamp
argument_list|(
literal|1
argument_list|)
operator|.
name|getNanos
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// If the lower bound was NULL, we'll get an NPE; just ignore it and don't set nanos.
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|splitPoints
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|long
name|end
init|=
name|splitPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Date
name|endDate
init|=
name|longToDate
argument_list|(
name|end
argument_list|,
name|sqlDataType
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|splitPoints
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|sqlDataType
operator|==
name|Types
operator|.
name|TIMESTAMP
condition|)
block|{
comment|// The upper bound's nanos value needs to match the actual upper-bound nanos.
try|try
block|{
operator|(
operator|(
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|)
name|endDate
operator|)
operator|.
name|setNanos
argument_list|(
name|results
operator|.
name|getTimestamp
argument_list|(
literal|2
argument_list|)
operator|.
name|getNanos
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// If the upper bound was NULL, we'll get an NPE; just ignore it and don't set nanos.
block|}
block|}
comment|// This is the last one; use a closed interval.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|lowClausePrefix
operator|+
name|dateToString
argument_list|(
name|startDate
argument_list|)
argument_list|,
name|colName
operator|+
literal|"<= "
operator|+
name|dateToString
argument_list|(
name|endDate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Normal open-interval case.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|lowClausePrefix
operator|+
name|dateToString
argument_list|(
name|startDate
argument_list|)
argument_list|,
name|highClausePrefix
operator|+
name|dateToString
argument_list|(
name|endDate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|end
expr_stmt|;
name|startDate
operator|=
name|endDate
expr_stmt|;
block|}
if|if
condition|(
name|minVal
operator|==
name|Long
operator|.
name|MIN_VALUE
operator|||
name|maxVal
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
comment|// Add an extra split to handle the null case that we saw.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|colName
operator|+
literal|" IS NULL"
argument_list|,
name|colName
operator|+
literal|" IS NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
comment|/** Retrieve the value from the column in a type-appropriate manner and return       its timestamp since the epoch. If the column is null, then return Long.MIN_VALUE.       This will cause a special split to be generated for the NULL case, but may also       cause poorly-balanced splits if most of the actual dates are positive time       since the epoch, etc.     */
DECL|method|resultSetColToLong (ResultSet rs, int colNum, int sqlDataType)
specifier|private
name|long
name|resultSetColToLong
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|colNum
parameter_list|,
name|int
name|sqlDataType
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
block|{
switch|switch
condition|(
name|sqlDataType
condition|)
block|{
case|case
name|Types
operator|.
name|DATE
case|:
return|return
name|rs
operator|.
name|getDate
argument_list|(
name|colNum
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
case|case
name|Types
operator|.
name|TIME
case|:
return|return
name|rs
operator|.
name|getTime
argument_list|(
name|colNum
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
name|rs
operator|.
name|getTimestamp
argument_list|(
name|colNum
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Not a date-type field"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// null column. return minimum long value.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered a NULL date in the split column. Splits may be poorly balanced."
argument_list|)
expr_stmt|;
return|return
name|Long
operator|.
name|MIN_VALUE
return|;
block|}
block|}
comment|/**  Parse the long-valued timestamp into the appropriate SQL date type. */
DECL|method|longToDate (long val, int sqlDataType)
specifier|private
name|Date
name|longToDate
parameter_list|(
name|long
name|val
parameter_list|,
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
name|DATE
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
name|val
argument_list|)
return|;
case|case
name|Types
operator|.
name|TIME
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Time
argument_list|(
name|val
argument_list|)
return|;
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Timestamp
argument_list|(
name|val
argument_list|)
return|;
default|default:
comment|// Shouldn't ever hit this case.
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Given a Date 'd', format it as a string for use in a SQL date    * comparison operation.    * @param d the date to format.    * @return the string representing this date in SQL with any appropriate    * quotation characters, etc.    */
DECL|method|dateToString (Date d)
specifier|protected
name|String
name|dateToString
parameter_list|(
name|Date
name|d
parameter_list|)
block|{
return|return
literal|"'"
operator|+
name|d
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
return|;
block|}
block|}
end_class

end_unit

