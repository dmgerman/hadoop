begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskType
import|;
end_import

begin_comment
comment|// This class exists to hold a bunch of static utils.  It's never instantiated.
end_comment

begin_class
DECL|class|Version20LogInterfaceUtils
specifier|abstract
class|class
name|Version20LogInterfaceUtils
block|{
DECL|method|get20TaskType (String taskType)
specifier|static
name|TaskType
name|get20TaskType
parameter_list|(
name|String
name|taskType
parameter_list|)
block|{
try|try
block|{
return|return
name|TaskType
operator|.
name|valueOf
argument_list|(
name|taskType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
literal|"CLEANUP"
operator|.
name|equals
argument_list|(
name|taskType
argument_list|)
condition|)
block|{
return|return
name|TaskType
operator|.
name|JOB_CLEANUP
return|;
block|}
if|if
condition|(
literal|"SETUP"
operator|.
name|equals
argument_list|(
name|taskType
argument_list|)
condition|)
block|{
return|return
name|TaskType
operator|.
name|JOB_SETUP
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

