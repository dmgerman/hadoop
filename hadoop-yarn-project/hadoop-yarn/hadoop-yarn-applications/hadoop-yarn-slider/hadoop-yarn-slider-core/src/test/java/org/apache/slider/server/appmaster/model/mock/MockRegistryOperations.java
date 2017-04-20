begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.mock
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
name|model
operator|.
name|mock
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
name|fs
operator|.
name|PathNotFoundException
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|RegistryPathStatus
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Simple stub registry for when one is needed for its API, but the operations  * are not actually required.  */
end_comment

begin_class
DECL|class|MockRegistryOperations
class|class
name|MockRegistryOperations
extends|extends
name|AbstractService
implements|implements
name|RegistryOperations
block|{
DECL|method|MockRegistryOperations ()
name|MockRegistryOperations
parameter_list|()
block|{
name|super
argument_list|(
literal|"mock"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mknode (String path, boolean createParents)
specifier|public
name|boolean
name|mknode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|createParents
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|bind (String path, ServiceRecord record, int flags)
specifier|public
name|void
name|bind
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|,
name|int
name|flags
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|resolve (String path)
specifier|public
name|ServiceRecord
name|resolve
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|stat (String path)
specifier|public
name|RegistryPathStatus
name|stat
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|exists (String path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|list (String path)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|delete (String path, boolean recursive)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|addWriteAccessor (String id, String pass)
specifier|public
name|boolean
name|addWriteAccessor
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|pass
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|clearWriteAccessors ()
specifier|public
name|void
name|clearWriteAccessors
parameter_list|()
block|{    }
block|}
end_class

end_unit

