begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|speculate
package|;
end_package

begin_class
DECL|class|DataStatistics
specifier|public
class|class
name|DataStatistics
block|{
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|sum
specifier|private
name|double
name|sum
init|=
literal|0
decl_stmt|;
DECL|field|sumSquares
specifier|private
name|double
name|sumSquares
init|=
literal|0
decl_stmt|;
DECL|method|DataStatistics ()
specifier|public
name|DataStatistics
parameter_list|()
block|{   }
DECL|method|DataStatistics (double initNum)
specifier|public
name|DataStatistics
parameter_list|(
name|double
name|initNum
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|sum
operator|=
name|initNum
expr_stmt|;
name|this
operator|.
name|sumSquares
operator|=
name|initNum
operator|*
name|initNum
expr_stmt|;
block|}
DECL|method|add (double newNum)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|double
name|newNum
parameter_list|)
block|{
name|this
operator|.
name|count
operator|++
expr_stmt|;
name|this
operator|.
name|sum
operator|+=
name|newNum
expr_stmt|;
name|this
operator|.
name|sumSquares
operator|+=
name|newNum
operator|*
name|newNum
expr_stmt|;
block|}
DECL|method|updateStatistics (double old, double update)
specifier|public
specifier|synchronized
name|void
name|updateStatistics
parameter_list|(
name|double
name|old
parameter_list|,
name|double
name|update
parameter_list|)
block|{
name|this
operator|.
name|sum
operator|+=
name|update
operator|-
name|old
expr_stmt|;
name|this
operator|.
name|sumSquares
operator|+=
operator|(
name|update
operator|*
name|update
operator|)
operator|-
operator|(
name|old
operator|*
name|old
operator|)
expr_stmt|;
block|}
DECL|method|mean ()
specifier|public
specifier|synchronized
name|double
name|mean
parameter_list|()
block|{
return|return
name|count
operator|==
literal|0
condition|?
literal|0.0
else|:
name|sum
operator|/
name|count
return|;
block|}
DECL|method|var ()
specifier|public
specifier|synchronized
name|double
name|var
parameter_list|()
block|{
comment|// E(X^2) - E(X)^2
if|if
condition|(
name|count
operator|<=
literal|1
condition|)
block|{
return|return
literal|0.0
return|;
block|}
name|double
name|mean
init|=
name|mean
argument_list|()
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
operator|(
name|sumSquares
operator|/
name|count
operator|)
operator|-
name|mean
operator|*
name|mean
argument_list|,
literal|0.0d
argument_list|)
return|;
block|}
DECL|method|std ()
specifier|public
specifier|synchronized
name|double
name|std
parameter_list|()
block|{
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|this
operator|.
name|var
argument_list|()
argument_list|)
return|;
block|}
DECL|method|outlier (float sigma)
specifier|public
specifier|synchronized
name|double
name|outlier
parameter_list|(
name|float
name|sigma
parameter_list|)
block|{
if|if
condition|(
name|count
operator|!=
literal|0.0
condition|)
block|{
return|return
name|mean
argument_list|()
operator|+
name|std
argument_list|()
operator|*
name|sigma
return|;
block|}
return|return
literal|0.0
return|;
block|}
DECL|method|count ()
specifier|public
specifier|synchronized
name|double
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**    * calculates the mean value within 95% ConfidenceInterval.    * 1.96 is standard for 95 %    *    * @return the mean value adding 95% confidence interval    */
DECL|method|meanCI ()
specifier|public
specifier|synchronized
name|double
name|meanCI
parameter_list|()
block|{
if|if
condition|(
name|count
operator|<=
literal|1
condition|)
return|return
literal|0.0
return|;
name|double
name|currMean
init|=
name|mean
argument_list|()
decl_stmt|;
name|double
name|currStd
init|=
name|std
argument_list|()
decl_stmt|;
return|return
name|currMean
operator|+
operator|(
literal|1.96
operator|*
name|currStd
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|count
argument_list|)
operator|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DataStatistics: count is "
operator|+
name|count
operator|+
literal|", sum is "
operator|+
name|sum
operator|+
literal|", sumSquares is "
operator|+
name|sumSquares
operator|+
literal|" mean is "
operator|+
name|mean
argument_list|()
operator|+
literal|" std() is "
operator|+
name|std
argument_list|()
operator|+
literal|", meanCI() is "
operator|+
name|meanCI
argument_list|()
return|;
block|}
block|}
end_class

end_unit

