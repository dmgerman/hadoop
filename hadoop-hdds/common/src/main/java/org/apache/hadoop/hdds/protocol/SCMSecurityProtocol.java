begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|DatanodeDetailsProto
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|OzoneManagerDetailsProto
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
name|ScmConfigKeys
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
name|security
operator|.
name|KerberosInfo
import|;
end_import

begin_comment
comment|/**  * The protocol used to perform security related operations with SCM.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|ScmConfigKeys
operator|.
name|HDDS_SCM_KERBEROS_PRINCIPAL_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|SCMSecurityProtocol
specifier|public
interface|interface
name|SCMSecurityProtocol
block|{
comment|/**    * Get SCM signed certificate for DataNode.    *    * @param dataNodeDetails - DataNode Details.    * @param certSignReq     - Certificate signing request.    * @return byte[]         - SCM signed certificate.    */
DECL|method|getDataNodeCertificate ( DatanodeDetailsProto dataNodeDetails, String certSignReq)
name|String
name|getDataNodeCertificate
parameter_list|(
name|DatanodeDetailsProto
name|dataNodeDetails
parameter_list|,
name|String
name|certSignReq
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get SCM signed certificate for OM.    *    * @param omDetails - DataNode Details.    * @param certSignReq     - Certificate signing request.    * @return byte[]         - SCM signed certificate.    */
DECL|method|getOMCertificate (OzoneManagerDetailsProto omDetails, String certSignReq)
name|String
name|getOMCertificate
parameter_list|(
name|OzoneManagerDetailsProto
name|omDetails
parameter_list|,
name|String
name|certSignReq
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

