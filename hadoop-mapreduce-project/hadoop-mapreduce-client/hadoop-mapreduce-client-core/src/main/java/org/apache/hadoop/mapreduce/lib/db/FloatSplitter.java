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
comment|/**  * Implement DBSplitter over floating-point values.  */
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
DECL|class|FloatSplitter
specifier|public
class|class
name|FloatSplitter
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
name|FloatSplitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MIN_INCREMENT
specifier|private
specifier|static
specifier|final
name|double
name|MIN_INCREMENT
init|=
literal|10000
operator|*
name|Double
operator|.
name|MIN_VALUE
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Generating splits for a floating-point index column. Due to the"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"imprecise representation of floating-point values in Java, this"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"may result in an incomplete import."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"You are strongly encouraged to choose an integral split column."
argument_list|)
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
name|double
name|minVal
init|=
name|results
operator|.
name|getDouble
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|double
name|maxVal
init|=
name|results
operator|.
name|getDouble
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// Use this as a hint. May need an extra task if the size doesn't
comment|// divide cleanly.
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
name|double
name|splitSize
init|=
operator|(
name|maxVal
operator|-
name|minVal
operator|)
operator|/
operator|(
name|double
operator|)
name|numSplits
decl_stmt|;
if|if
condition|(
name|splitSize
operator|<
name|MIN_INCREMENT
condition|)
block|{
name|splitSize
operator|=
name|MIN_INCREMENT
expr_stmt|;
block|}
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
name|double
name|curLower
init|=
name|minVal
decl_stmt|;
name|double
name|curUpper
init|=
name|curLower
operator|+
name|splitSize
decl_stmt|;
while|while
condition|(
name|curUpper
operator|<
name|maxVal
condition|)
block|{
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
name|Double
operator|.
name|toString
argument_list|(
name|curLower
argument_list|)
argument_list|,
name|highClausePrefix
operator|+
name|Double
operator|.
name|toString
argument_list|(
name|curUpper
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|curLower
operator|=
name|curUpper
expr_stmt|;
name|curUpper
operator|+=
name|splitSize
expr_stmt|;
block|}
comment|// Catch any overage and create the closed interval for the last split.
if|if
condition|(
name|curLower
operator|<=
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
name|Double
operator|.
name|toString
argument_list|(
name|curLower
argument_list|)
argument_list|,
name|colName
operator|+
literal|"<= "
operator|+
name|Double
operator|.
name|toString
argument_list|(
name|maxVal
argument_list|)
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

