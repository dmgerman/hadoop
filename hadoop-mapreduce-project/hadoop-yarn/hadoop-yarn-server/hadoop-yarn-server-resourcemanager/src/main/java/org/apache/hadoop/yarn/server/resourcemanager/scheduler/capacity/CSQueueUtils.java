begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|scheduler
operator|.
name|capacity
package|;
end_package

begin_class
DECL|class|CSQueueUtils
class|class
name|CSQueueUtils
block|{
DECL|method|checkMaxCapacity (String queueName, float capacity, float maximumCapacity)
specifier|public
specifier|static
name|void
name|checkMaxCapacity
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|)
block|{
if|if
condition|(
name|Math
operator|.
name|round
argument_list|(
literal|100
operator|*
name|maximumCapacity
argument_list|)
operator|!=
name|CapacitySchedulerConfiguration
operator|.
name|UNDEFINED
operator|&&
name|maximumCapacity
operator|<
name|capacity
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal call to setMaxCapacity. "
operator|+
literal|"Queue '"
operator|+
name|queueName
operator|+
literal|"' has "
operator|+
literal|"capacity ("
operator|+
name|capacity
operator|+
literal|") greater than "
operator|+
literal|"maximumCapacity ("
operator|+
name|maximumCapacity
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

