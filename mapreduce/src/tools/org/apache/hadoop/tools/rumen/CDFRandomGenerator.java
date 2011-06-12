begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *   */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_comment
comment|/**  * An instance of this class generates random values that confirm to the  * embedded {@link LoggedDiscreteCDF} . The discrete CDF is a pointwise  * approximation of the "real" CDF. We therefore have a choice of interpolation  * rules.  *   * A concrete subclass of this abstract class will implement valueAt(double)  * using a class-dependent interpolation rule.  *   */
end_comment

begin_class
DECL|class|CDFRandomGenerator
specifier|public
specifier|abstract
class|class
name|CDFRandomGenerator
block|{
DECL|field|rankings
specifier|final
name|double
index|[]
name|rankings
decl_stmt|;
DECL|field|values
specifier|final
name|long
index|[]
name|values
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CDFRandomGenerator (LoggedDiscreteCDF cdf)
name|CDFRandomGenerator
parameter_list|(
name|LoggedDiscreteCDF
name|cdf
parameter_list|)
block|{
name|this
argument_list|(
name|cdf
argument_list|,
operator|new
name|Random
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CDFRandomGenerator (LoggedDiscreteCDF cdf, long seed)
name|CDFRandomGenerator
parameter_list|(
name|LoggedDiscreteCDF
name|cdf
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|this
argument_list|(
name|cdf
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|CDFRandomGenerator (LoggedDiscreteCDF cdf, Random random)
specifier|private
name|CDFRandomGenerator
parameter_list|(
name|LoggedDiscreteCDF
name|cdf
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|rankings
operator|=
operator|new
name|double
index|[
name|cdf
operator|.
name|getRankings
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|2
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
name|cdf
operator|.
name|getRankings
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|2
index|]
expr_stmt|;
name|initializeTables
argument_list|(
name|cdf
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeTables (LoggedDiscreteCDF cdf)
specifier|protected
specifier|final
name|void
name|initializeTables
parameter_list|(
name|LoggedDiscreteCDF
name|cdf
parameter_list|)
block|{
name|rankings
index|[
literal|0
index|]
operator|=
literal|0.0
expr_stmt|;
name|values
index|[
literal|0
index|]
operator|=
name|cdf
operator|.
name|getMinimum
argument_list|()
expr_stmt|;
name|rankings
index|[
name|rankings
operator|.
name|length
operator|-
literal|1
index|]
operator|=
literal|1.0
expr_stmt|;
name|values
index|[
name|rankings
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|cdf
operator|.
name|getMaximum
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|LoggedSingleRelativeRanking
argument_list|>
name|subjects
init|=
name|cdf
operator|.
name|getRankings
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subjects
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|rankings
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|subjects
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getRelativeRanking
argument_list|()
expr_stmt|;
name|values
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|subjects
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatum
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|floorIndex (double probe)
specifier|protected
name|int
name|floorIndex
parameter_list|(
name|double
name|probe
parameter_list|)
block|{
name|int
name|result
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|rankings
argument_list|,
name|probe
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|result
operator|+
literal|1
argument_list|)
operator|-
literal|1
return|;
block|}
DECL|method|getRankingAt (int index)
specifier|protected
name|double
name|getRankingAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|rankings
index|[
name|index
index|]
return|;
block|}
DECL|method|getDatumAt (int index)
specifier|protected
name|long
name|getDatumAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|index
index|]
return|;
block|}
DECL|method|randomValue ()
specifier|public
name|long
name|randomValue
parameter_list|()
block|{
return|return
name|valueAt
argument_list|(
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
return|;
block|}
DECL|method|valueAt (double probability)
specifier|public
specifier|abstract
name|long
name|valueAt
parameter_list|(
name|double
name|probability
parameter_list|)
function_decl|;
block|}
end_class

end_unit

