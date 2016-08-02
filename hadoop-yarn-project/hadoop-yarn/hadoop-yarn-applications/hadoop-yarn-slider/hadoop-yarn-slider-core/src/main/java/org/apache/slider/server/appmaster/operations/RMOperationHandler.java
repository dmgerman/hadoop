begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.operations
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|RMOperationHandler
specifier|public
specifier|abstract
class|class
name|RMOperationHandler
implements|implements
name|RMOperationHandlerActions
block|{
annotation|@
name|Override
DECL|method|execute (List<AbstractRMOperation> operations)
specifier|public
name|void
name|execute
parameter_list|(
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|operations
parameter_list|)
block|{
for|for
control|(
name|AbstractRMOperation
name|operation
range|:
name|operations
control|)
block|{
name|operation
operator|.
name|execute
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

