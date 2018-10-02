begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|java
operator|.
name|util
operator|.
name|List
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
name|conf
operator|.
name|ReconfigurationTaskStatus
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
name|io
operator|.
name|retry
operator|.
name|Idempotent
import|;
end_import

begin_comment
comment|/**********************************************************************  * ReconfigurationProtocol is used by HDFS admin to reload configuration  * for NN/DN without restarting them.  **********************************************************************/
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ReconfigurationProtocol
specifier|public
interface|interface
name|ReconfigurationProtocol
block|{
DECL|field|VERSIONID
name|long
name|VERSIONID
init|=
literal|1L
decl_stmt|;
comment|/**    * Asynchronously reload configuration on disk and apply changes.    */
annotation|@
name|Idempotent
DECL|method|startReconfiguration ()
name|void
name|startReconfiguration
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the status of the previously issued reconfig task.    * @see org.apache.hadoop.conf.ReconfigurationTaskStatus    */
annotation|@
name|Idempotent
DECL|method|getReconfigurationStatus ()
name|ReconfigurationTaskStatus
name|getReconfigurationStatus
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of allowed properties for reconfiguration.    */
annotation|@
name|Idempotent
DECL|method|listReconfigurableProperties ()
name|List
argument_list|<
name|String
argument_list|>
name|listReconfigurableProperties
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

