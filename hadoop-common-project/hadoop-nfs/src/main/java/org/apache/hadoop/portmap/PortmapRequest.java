begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.portmap
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|portmap
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
name|oncrpc
operator|.
name|RpcCall
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
name|oncrpc
operator|.
name|RpcUtil
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
name|oncrpc
operator|.
name|XDR
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
name|oncrpc
operator|.
name|security
operator|.
name|CredentialsNone
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
name|oncrpc
operator|.
name|security
operator|.
name|VerifierNone
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
name|portmap
operator|.
name|PortmapInterface
operator|.
name|Procedure
import|;
end_import

begin_comment
comment|/**  * Helper utility for building portmap request  */
end_comment

begin_class
DECL|class|PortmapRequest
specifier|public
class|class
name|PortmapRequest
block|{
DECL|method|mapping (XDR xdr)
specifier|public
specifier|static
name|PortmapMapping
name|mapping
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
return|return
name|PortmapMapping
operator|.
name|deserialize
argument_list|(
name|xdr
argument_list|)
return|;
block|}
DECL|method|create (PortmapMapping mapping)
specifier|public
specifier|static
name|XDR
name|create
parameter_list|(
name|PortmapMapping
name|mapping
parameter_list|)
block|{
name|XDR
name|request
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|RpcCall
name|call
init|=
name|RpcCall
operator|.
name|getInstance
argument_list|(
name|RpcUtil
operator|.
name|getNewXid
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|RpcProgramPortmap
operator|.
name|PROGRAM
argument_list|)
argument_list|)
argument_list|,
name|RpcProgramPortmap
operator|.
name|PROGRAM
argument_list|,
name|RpcProgramPortmap
operator|.
name|VERSION
argument_list|,
name|Procedure
operator|.
name|PMAPPROC_SET
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|CredentialsNone
argument_list|()
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|write
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|mapping
operator|.
name|serialize
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class

end_unit

