begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resource
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|resource
package|;
end_package

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
operator|.
name|Private
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
operator|.
name|Evolving
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|ResourceWeights
specifier|public
class|class
name|ResourceWeights
block|{
DECL|field|NEUTRAL
specifier|public
specifier|static
specifier|final
name|ResourceWeights
name|NEUTRAL
init|=
operator|new
name|ResourceWeights
argument_list|(
literal|1.0f
argument_list|)
decl_stmt|;
DECL|field|weights
specifier|private
name|float
index|[]
name|weights
init|=
operator|new
name|float
index|[
name|ResourceType
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
DECL|method|ResourceWeights (float memoryWeight, float cpuWeight)
specifier|public
name|ResourceWeights
parameter_list|(
name|float
name|memoryWeight
parameter_list|,
name|float
name|cpuWeight
parameter_list|)
block|{
name|weights
index|[
name|ResourceType
operator|.
name|MEMORY
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|memoryWeight
expr_stmt|;
name|weights
index|[
name|ResourceType
operator|.
name|CPU
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|cpuWeight
expr_stmt|;
block|}
DECL|method|ResourceWeights (float weight)
specifier|public
name|ResourceWeights
parameter_list|(
name|float
name|weight
parameter_list|)
block|{
name|setWeight
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceWeights ()
specifier|public
name|ResourceWeights
parameter_list|()
block|{ }
DECL|method|setWeight (float weight)
specifier|public
name|void
name|setWeight
parameter_list|(
name|float
name|weight
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|weights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|weights
index|[
name|i
index|]
operator|=
name|weight
expr_stmt|;
block|}
block|}
DECL|method|setWeight (ResourceType resourceType, float weight)
specifier|public
name|void
name|setWeight
parameter_list|(
name|ResourceType
name|resourceType
parameter_list|,
name|float
name|weight
parameter_list|)
block|{
name|weights
index|[
name|resourceType
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|weight
expr_stmt|;
block|}
DECL|method|getWeight (ResourceType resourceType)
specifier|public
name|float
name|getWeight
parameter_list|(
name|ResourceType
name|resourceType
parameter_list|)
block|{
return|return
name|weights
index|[
name|resourceType
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<"
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
name|ResourceType
operator|.
name|values
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|ResourceType
name|resourceType
init|=
name|ResourceType
operator|.
name|values
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|resourceType
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|" weight=%.1f"
argument_list|,
name|getWeight
argument_list|(
name|resourceType
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

