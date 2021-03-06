begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
import|;
end_import

begin_comment
comment|/**  * Interface to verify delegation tokens passed through WebHDFS.  * Implementations are intercepted by JspHelper that pass delegation token  * for verification.  */
end_comment

begin_interface
DECL|interface|TokenVerifier
specifier|public
interface|interface
name|TokenVerifier
parameter_list|<
name|T
extends|extends
name|AbstractDelegationTokenIdentifier
parameter_list|>
block|{
comment|/* Verify delegation token passed through WebHDFS    * Name node, Router implement this for JspHelper to verify token    */
DECL|method|verifyToken (T t, byte[] password)
name|void
name|verifyToken
parameter_list|(
name|T
name|t
parameter_list|,
name|byte
index|[]
name|password
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

