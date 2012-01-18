begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.datatypes
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
operator|.
name|datatypes
package|;
end_package

begin_comment
comment|/**  * Represents a queue name.  */
end_comment

begin_class
DECL|class|QueueName
specifier|public
class|class
name|QueueName
extends|extends
name|DefaultAnonymizableDataType
block|{
DECL|field|queueName
specifier|private
specifier|final
name|String
name|queueName
decl_stmt|;
DECL|method|QueueName (String queueName)
specifier|public
name|QueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueName
operator|=
name|queueName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|queueName
return|;
block|}
annotation|@
name|Override
DECL|method|getPrefix ()
specifier|protected
name|String
name|getPrefix
parameter_list|()
block|{
return|return
literal|"queue"
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

