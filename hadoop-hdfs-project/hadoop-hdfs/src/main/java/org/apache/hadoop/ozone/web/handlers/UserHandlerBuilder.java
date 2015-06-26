begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|handlers
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
name|ozone
operator|.
name|web
operator|.
name|interfaces
operator|.
name|UserAuth
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
name|ozone
operator|.
name|web
operator|.
name|userauth
operator|.
name|Simple
import|;
end_import

begin_comment
comment|/**  * This class is responsible for providing a  * {@link org.apache.hadoop.ozone.web.interfaces.UserAuth}  * implementation to object store web handlers.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|UserHandlerBuilder
specifier|public
specifier|final
class|class
name|UserHandlerBuilder
block|{
DECL|field|USER_AUTH_THREAD_LOCAL
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|UserAuth
argument_list|>
name|USER_AUTH_THREAD_LOCAL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|UserAuth
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Returns the configured UserAuth from thread-local storage for this    * thread.    *    * @return UserAuth from thread-local storage    */
DECL|method|getAuthHandler ()
specifier|public
specifier|static
name|UserAuth
name|getAuthHandler
parameter_list|()
block|{
name|UserAuth
name|authHandler
init|=
name|USER_AUTH_THREAD_LOCAL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|authHandler
operator|!=
literal|null
condition|)
block|{
return|return
name|authHandler
return|;
block|}
else|else
block|{
comment|// This only happens while using mvn jetty:run for testing.
return|return
operator|new
name|Simple
argument_list|()
return|;
block|}
block|}
comment|/**    * Removes the configured UserAuth from thread-local storage for this    * thread.    */
DECL|method|removeAuthHandler ()
specifier|public
specifier|static
name|void
name|removeAuthHandler
parameter_list|()
block|{
name|USER_AUTH_THREAD_LOCAL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the configured UserAuthHandler in thread-local storage for this    * thread.    *    * @param authHandler authHandler to set in thread-local storage    */
DECL|method|setAuthHandler (UserAuth authHandler)
specifier|public
specifier|static
name|void
name|setAuthHandler
parameter_list|(
name|UserAuth
name|authHandler
parameter_list|)
block|{
name|USER_AUTH_THREAD_LOCAL
operator|.
name|set
argument_list|(
name|authHandler
argument_list|)
expr_stmt|;
block|}
comment|/**    * There is no reason to instantiate this class.    */
DECL|method|UserHandlerBuilder ()
specifier|private
name|UserHandlerBuilder
parameter_list|()
block|{   }
block|}
end_class

end_unit

