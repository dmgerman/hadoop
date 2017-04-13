begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
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
name|cblock
operator|.
name|proto
operator|.
name|MountVolumeResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class is the handler of CBlockManager used by target server  * to communicate with CBlockManager.  *  * More specifically, this class will expose local methods to target  * server, and make RPC calls to CBlockManager accordingly  */
end_comment

begin_class
DECL|class|CBlockManagerHandler
specifier|public
class|class
name|CBlockManagerHandler
block|{
DECL|field|handler
specifier|private
specifier|final
name|CBlockClientProtocolClientSideTranslatorPB
name|handler
decl_stmt|;
DECL|method|CBlockManagerHandler ( CBlockClientProtocolClientSideTranslatorPB handler)
specifier|public
name|CBlockManagerHandler
parameter_list|(
name|CBlockClientProtocolClientSideTranslatorPB
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
DECL|method|mountVolume ( String userName, String volumeName)
specifier|public
name|MountVolumeResponse
name|mountVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|handler
operator|.
name|mountVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

