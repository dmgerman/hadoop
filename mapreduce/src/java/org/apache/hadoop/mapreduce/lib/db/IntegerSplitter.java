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
comment|/**  * Implement DBSplitter over integer values.  */
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
DECL|class|IntegerSplitter
specifier|public
class|class
name|IntegerSplitter
implements|implements
name|DBSplitter
block|{
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
init|=
name|results
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|maxVal
init|=
name|results
operator|.
name|getLong
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
name|results
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
operator|==
literal|null
operator|&&
name|results
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
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
comment|// Get all the split points together.
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
name|Long
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|,
name|colName
operator|+
literal|"<= "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|end
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
name|Long
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|,
name|highClausePrefix
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|end
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|end
expr_stmt|;
block|}
if|if
condition|(
name|results
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
operator|==
literal|null
operator|||
name|results
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// At least one extrema is null; add a null split.
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
comment|/**    * Returns a list of longs one element longer than the list of input splits.    * This represents the boundaries between input splits.    * All splits are open on the top end, except the last one.    *    * So the list [0, 5, 8, 12, 18] would represent splits capturing the intervals:    *    * [0, 5)    * [5, 8)    * [8, 12)    * [12, 18] note the closed interval for the last split.    */
DECL|method|split (long numSplits, long minVal, long maxVal)
name|List
argument_list|<
name|Long
argument_list|>
name|split
parameter_list|(
name|long
name|numSplits
parameter_list|,
name|long
name|minVal
parameter_list|,
name|long
name|maxVal
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|// Use numSplits as a hint. May need an extra task if the size doesn't
comment|// divide cleanly.
name|long
name|splitSize
init|=
operator|(
name|maxVal
operator|-
name|minVal
operator|)
operator|/
name|numSplits
decl_stmt|;
if|if
condition|(
name|splitSize
operator|<
literal|1
condition|)
block|{
name|splitSize
operator|=
literal|1
expr_stmt|;
block|}
name|long
name|curVal
init|=
name|minVal
decl_stmt|;
while|while
condition|(
name|curVal
operator|<=
name|maxVal
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
operator|+=
name|splitSize
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
operator|!=
name|maxVal
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

