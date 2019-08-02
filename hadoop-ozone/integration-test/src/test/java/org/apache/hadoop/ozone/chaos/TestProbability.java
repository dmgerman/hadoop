begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.chaos
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|chaos
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|lang3
operator|.
name|RandomUtils
import|;
end_import

begin_comment
comment|/**  * This class is used to find out if a certain event is true.  * Every event is assigned a propbability and the isTrue function returns true  * when the probability has been met.  */
end_comment

begin_class
DECL|class|TestProbability
specifier|final
specifier|public
class|class
name|TestProbability
block|{
DECL|field|pct
specifier|private
name|int
name|pct
decl_stmt|;
DECL|method|TestProbability (int pct)
specifier|private
name|TestProbability
parameter_list|(
name|int
name|pct
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pct
operator|<=
literal|100
operator|&&
name|pct
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|pct
operator|=
name|pct
expr_stmt|;
block|}
DECL|method|isTrue ()
specifier|public
name|boolean
name|isTrue
parameter_list|()
block|{
return|return
operator|(
name|RandomUtils
operator|.
name|nextInt
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
operator|<=
name|pct
operator|)
return|;
block|}
DECL|method|valueOf (int pct)
specifier|public
specifier|static
name|TestProbability
name|valueOf
parameter_list|(
name|int
name|pct
parameter_list|)
block|{
return|return
operator|new
name|TestProbability
argument_list|(
name|pct
argument_list|)
return|;
block|}
block|}
end_class

end_unit

