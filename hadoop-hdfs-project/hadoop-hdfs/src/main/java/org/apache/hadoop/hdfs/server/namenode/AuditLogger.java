begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_comment
comment|/**  * Interface defining an audit logger.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|AuditLogger
specifier|public
interface|interface
name|AuditLogger
block|{
comment|/**    * Called during initialization of the logger.    *    * @param conf The configuration object.    */
DECL|method|initialize (Configuration conf)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Called to log an audit event.    *<p>    * This method must return as quickly as possible, since it's called    * in a critical section of the NameNode's operation.    *    * @param succeeded Whether authorization succeeded.    * @param userName Name of the user executing the request.    * @param addr Remote address of the request.    * @param cmd The requested command.    * @param src Path of affected source file.    * @param dst Path of affected destination file (if any).    * @param stat File information for operations that change the file's    *             metadata (permissions, owner, times, etc).    */
DECL|method|logAuditEvent (boolean succeeded, String userName, InetAddress addr, String cmd, String src, String dst, FileStatus stat)
name|void
name|logAuditEvent
parameter_list|(
name|boolean
name|succeeded
parameter_list|,
name|String
name|userName
parameter_list|,
name|InetAddress
name|addr
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

