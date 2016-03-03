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
name|conf
operator|.
name|Configuration
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
name|OzoneConfiguration
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
name|StorageHandler
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
name|localstorage
operator|.
name|LocalStorageHandler
import|;
end_import

begin_comment
comment|/**  * This class is responsible for providing a {@link StorageHandler}  * implementation to object store web handlers.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageHandlerBuilder
specifier|public
specifier|final
class|class
name|StorageHandlerBuilder
block|{
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|StorageHandler
argument_list|>
DECL|field|STORAGE_HANDLER_THREAD_LOCAL
name|STORAGE_HANDLER_THREAD_LOCAL
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Returns the configured StorageHandler from thread-local storage for this    * thread.    *    * @return StorageHandler from thread-local storage    */
DECL|method|getStorageHandler ()
specifier|public
specifier|static
name|StorageHandler
name|getStorageHandler
parameter_list|()
block|{
name|StorageHandler
name|storageHandler
init|=
name|STORAGE_HANDLER_THREAD_LOCAL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|storageHandler
operator|!=
literal|null
condition|)
block|{
return|return
name|storageHandler
return|;
block|}
else|else
block|{
comment|// This only happens while using mvn jetty:run for testing.
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
return|return
operator|new
name|LocalStorageHandler
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
comment|/**    * Removes the configured StorageHandler from thread-local storage for this    * thread.    */
DECL|method|removeStorageHandler ()
specifier|public
specifier|static
name|void
name|removeStorageHandler
parameter_list|()
block|{
name|STORAGE_HANDLER_THREAD_LOCAL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the configured StorageHandler in thread-local storage for this thread.    *    * @param storageHandler StorageHandler to set in thread-local storage    */
DECL|method|setStorageHandler (StorageHandler storageHandler)
specifier|public
specifier|static
name|void
name|setStorageHandler
parameter_list|(
name|StorageHandler
name|storageHandler
parameter_list|)
block|{
name|STORAGE_HANDLER_THREAD_LOCAL
operator|.
name|set
argument_list|(
name|storageHandler
argument_list|)
expr_stmt|;
block|}
comment|/**    * There is no reason to instantiate this class.    */
DECL|method|StorageHandlerBuilder ()
specifier|private
name|StorageHandlerBuilder
parameter_list|()
block|{   }
block|}
end_class

end_unit

