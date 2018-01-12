begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.executor
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
name|nodemanager
operator|.
name|executor
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
import|;
end_import

begin_comment
comment|/**  * Encapsulate the details needed to reap a container.  */
end_comment

begin_class
DECL|class|ContainerReapContext
specifier|public
specifier|final
class|class
name|ContainerReapContext
block|{
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
comment|/**    * Builder for the ContainerReapContext.    */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|builderContainer
specifier|private
name|Container
name|builderContainer
decl_stmt|;
DECL|field|builderUser
specifier|private
name|String
name|builderUser
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
comment|/**      * Set the container within the context.      *      * @param container the {@link Container}.      * @return the Builder with the container set.      */
DECL|method|setContainer (Container container)
specifier|public
name|Builder
name|setContainer
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|this
operator|.
name|builderContainer
operator|=
name|container
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the set within the context.      *      * @param user the user.      * @return the Builder with the user set.      */
DECL|method|setUser (String user)
specifier|public
name|Builder
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|builderUser
operator|=
name|user
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds the context with the attributes set.      *      * @return the context.      */
DECL|method|build ()
specifier|public
name|ContainerReapContext
name|build
parameter_list|()
block|{
return|return
operator|new
name|ContainerReapContext
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|ContainerReapContext (Builder builder)
specifier|private
name|ContainerReapContext
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|builder
operator|.
name|builderContainer
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|builder
operator|.
name|builderUser
expr_stmt|;
block|}
comment|/**    * Get the container set for the context.    *    * @return the {@link Container} set in the context.    */
DECL|method|getContainer ()
specifier|public
name|Container
name|getContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
comment|/**    * Get the user set for the context.    *    * @return the user set in the context.    */
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
block|}
end_class

end_unit

