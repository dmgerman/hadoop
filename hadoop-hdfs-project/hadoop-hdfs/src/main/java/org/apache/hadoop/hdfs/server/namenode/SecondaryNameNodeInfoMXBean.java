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

begin_comment
comment|/**  * JMX information of the secondary NameNode  */
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
DECL|interface|SecondaryNameNodeInfoMXBean
specifier|public
interface|interface
name|SecondaryNameNodeInfoMXBean
extends|extends
name|VersionInfoMXBean
block|{
comment|/**    * Gets the host and port colon separated.    */
DECL|method|getHostAndPort ()
specifier|public
name|String
name|getHostAndPort
parameter_list|()
function_decl|;
comment|/**    * Gets if security is enabled.    *    * @return true, if security is enabled.    */
DECL|method|isSecurityEnabled ()
name|boolean
name|isSecurityEnabled
parameter_list|()
function_decl|;
comment|/**    * @return the timestamp of when the SNN starts    */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * @return the timestamp of the last checkpoint    */
DECL|method|getLastCheckpointTime ()
specifier|public
name|long
name|getLastCheckpointTime
parameter_list|()
function_decl|;
comment|/**    * @return the number of msec since the last checkpoint, or -1 if no    * checkpoint has been done yet.    */
DECL|method|getLastCheckpointDeltaMs ()
specifier|public
name|long
name|getLastCheckpointDeltaMs
parameter_list|()
function_decl|;
comment|/**    * @return the directories that store the checkpoint images    */
DECL|method|getCheckpointDirectories ()
specifier|public
name|String
index|[]
name|getCheckpointDirectories
parameter_list|()
function_decl|;
comment|/**    * @return the directories that store the edit logs    */
DECL|method|getCheckpointEditlogDirectories ()
specifier|public
name|String
index|[]
name|getCheckpointEditlogDirectories
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

