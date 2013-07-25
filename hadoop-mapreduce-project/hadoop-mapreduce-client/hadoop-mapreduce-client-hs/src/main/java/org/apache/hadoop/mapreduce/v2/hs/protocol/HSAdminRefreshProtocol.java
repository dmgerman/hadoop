begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
operator|.
name|Private
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
name|InterfaceStability
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
name|fs
operator|.
name|CommonConfigurationKeys
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
comment|/**  * Protocol use  *   */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|)
annotation|@
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|HSAdminRefreshProtocol
specifier|public
interface|interface
name|HSAdminRefreshProtocol
block|{
comment|/**    * Refresh admin acls.    *     * @throws IOException    */
DECL|method|refreshAdminAcls ()
specifier|public
name|void
name|refreshAdminAcls
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Refresh job retention settings.    *     * @throws IOException    */
DECL|method|refreshJobRetentionSettings ()
specifier|public
name|void
name|refreshJobRetentionSettings
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Refresh log retention settings.    *     * @throws IOException    */
DECL|method|refreshLogRetentionSettings ()
specifier|public
name|void
name|refreshLogRetentionSettings
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

