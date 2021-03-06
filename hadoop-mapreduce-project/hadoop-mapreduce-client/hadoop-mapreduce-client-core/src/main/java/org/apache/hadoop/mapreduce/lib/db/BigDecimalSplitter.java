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
name|math
operator|.
name|BigDecimal
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
comment|/**  * Implement DBSplitter over BigDecimal values.  */
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
DECL|class|BigDecimalSplitter
specifier|public
class|class
name|BigDecimalSplitter
implements|implements
name|DBSplitter
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
name|BigDecimalSplitter
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
name|BigDecimal
name|minVal
init|=
name|results
operator|.
name|getBigDecimal
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BigDecimal
name|maxVal
init|=
name|results
operator|.
name|getBigDecimal
argument_list|(
literal|2
argument_list|)
decl_stmt|;
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
name|BigDecimal
name|numSplits
init|=
operator|new
name|BigDecimal
argument_list|(
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|minVal
operator|==
literal|null
operator|&&
name|maxVal
operator|==
literal|null
condition|)
block|{
comment|// Range is null to null. Return a null split accordingly.
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
if|if
condition|(
name|minVal
operator|==
literal|null
operator|||
name|maxVal
operator|==
literal|null
condition|)
block|{
comment|// Don't know what is a reasonable min/max value for interpolation. Fail.
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot find a range for NUMERIC or DECIMAL fields with one end NULL."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Get all the split points together.
name|List
argument_list|<
name|BigDecimal
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
name|BigDecimal
name|start
init|=
name|splitPoints
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|BigDecimal
name|end
init|=
name|splitPoints
operator|.
name|get
argument_list|(
name|i
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
name|start
operator|.
name|toString
argument_list|()
argument_list|,
name|colName
operator|+
literal|"<= "
operator|+
name|end
operator|.
name|toString
argument_list|()
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
name|start
operator|.
name|toString
argument_list|()
argument_list|,
name|highClausePrefix
operator|+
name|end
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|end
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
DECL|field|MIN_INCREMENT
specifier|private
specifier|static
specifier|final
name|BigDecimal
name|MIN_INCREMENT
init|=
operator|new
name|BigDecimal
argument_list|(
literal|10000
operator|*
name|Double
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
comment|/**    * Divide numerator by denominator. If impossible in exact mode, use rounding.    */
DECL|method|tryDivide (BigDecimal numerator, BigDecimal denominator)
specifier|protected
name|BigDecimal
name|tryDivide
parameter_list|(
name|BigDecimal
name|numerator
parameter_list|,
name|BigDecimal
name|denominator
parameter_list|)
block|{
try|try
block|{
return|return
name|numerator
operator|.
name|divide
argument_list|(
name|denominator
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ArithmeticException
name|ae
parameter_list|)
block|{
return|return
name|numerator
operator|.
name|divide
argument_list|(
name|denominator
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_UP
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns a list of BigDecimals one element longer than the list of input splits.    * This represents the boundaries between input splits.    * All splits are open on the top end, except the last one.    *    * So the list [0, 5, 8, 12, 18] would represent splits capturing the intervals:    *    * [0, 5)    * [5, 8)    * [8, 12)    * [12, 18] note the closed interval for the last split.    */
DECL|method|split (BigDecimal numSplits, BigDecimal minVal, BigDecimal maxVal)
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|split
parameter_list|(
name|BigDecimal
name|numSplits
parameter_list|,
name|BigDecimal
name|minVal
parameter_list|,
name|BigDecimal
name|maxVal
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|BigDecimal
argument_list|>
argument_list|()
decl_stmt|;
comment|// Use numSplits as a hint. May need an extra task if the size doesn't
comment|// divide cleanly.
name|BigDecimal
name|splitSize
init|=
name|tryDivide
argument_list|(
name|maxVal
operator|.
name|subtract
argument_list|(
name|minVal
argument_list|)
argument_list|,
operator|(
name|numSplits
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitSize
operator|.
name|compareTo
argument_list|(
name|MIN_INCREMENT
argument_list|)
operator|<
literal|0
condition|)
block|{
name|splitSize
operator|=
name|MIN_INCREMENT
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Set BigDecimal splitSize to MIN_INCREMENT"
argument_list|)
expr_stmt|;
block|}
name|BigDecimal
name|curVal
init|=
name|minVal
decl_stmt|;
while|while
condition|(
name|curVal
operator|.
name|compareTo
argument_list|(
name|maxVal
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|splits
operator|.
name|add
argument_list|(
name|curVal
argument_list|)
expr_stmt|;
name|curVal
operator|=
name|curVal
operator|.
name|add
argument_list|(
name|splitSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|splits
operator|.
name|get
argument_list|(
name|splits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|maxVal
argument_list|)
operator|!=
literal|0
operator|||
name|splits
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// We didn't end on the maxVal. Add that to the end of the list.
name|splits
operator|.
name|add
argument_list|(
name|maxVal
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
block|}
end_class

end_unit

