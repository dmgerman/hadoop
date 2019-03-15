begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|hdds
operator|.
name|scm
operator|.
name|protocol
operator|.
name|ScmBlockLocationProtocol
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
name|hdds
operator|.
name|scm
operator|.
name|protocol
operator|.
name|StorageContainerLocationProtocol
import|;
end_import

begin_comment
comment|/**  * Wrapper class for Scm protocol clients.  */
end_comment

begin_class
DECL|class|ScmClient
specifier|public
class|class
name|ScmClient
block|{
DECL|field|blockClient
specifier|private
name|ScmBlockLocationProtocol
name|blockClient
decl_stmt|;
DECL|field|containerClient
specifier|private
name|StorageContainerLocationProtocol
name|containerClient
decl_stmt|;
DECL|method|ScmClient (ScmBlockLocationProtocol blockClient, StorageContainerLocationProtocol containerClient)
name|ScmClient
parameter_list|(
name|ScmBlockLocationProtocol
name|blockClient
parameter_list|,
name|StorageContainerLocationProtocol
name|containerClient
parameter_list|)
block|{
name|this
operator|.
name|containerClient
operator|=
name|containerClient
expr_stmt|;
name|this
operator|.
name|blockClient
operator|=
name|blockClient
expr_stmt|;
block|}
DECL|method|getBlockClient ()
name|ScmBlockLocationProtocol
name|getBlockClient
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockClient
return|;
block|}
DECL|method|getContainerClient ()
name|StorageContainerLocationProtocol
name|getContainerClient
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerClient
return|;
block|}
block|}
end_class

end_unit

