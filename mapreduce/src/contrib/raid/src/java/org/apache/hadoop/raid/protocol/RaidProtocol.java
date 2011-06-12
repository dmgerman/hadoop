begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|VersionedProtocol
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
name|Path
import|;
end_import

begin_comment
comment|/**********************************************************************  * RaidProtocol is used by user code   * {@link org.apache.hadoop.raid.RaidShell} class to communicate   * with the RaidNode.  User code can manipulate the configured policies.  *  **********************************************************************/
end_comment

begin_interface
DECL|interface|RaidProtocol
specifier|public
interface|interface
name|RaidProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * Compared to the previous version the following changes have been introduced:    * Only the latest change is reflected.    * 1: new protocol introduced    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Get a listing of all configured policies    * @throws IOException    * return all categories of configured policies    */
DECL|method|getAllPolicies ()
specifier|public
name|PolicyList
index|[]
name|getAllPolicies
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Unraid the specified input path. This is called when the specified file    * is corrupted. This call will move the specified file to file.old    * and then recover it from the RAID subsystem.    *    * @param inputPath The absolute pathname of the file to be recovered.    * @param corruptOffset The offset that has the corruption    */
DECL|method|recoverFile (String inputPath, long corruptOffset)
specifier|public
name|String
name|recoverFile
parameter_list|(
name|String
name|inputPath
parameter_list|,
name|long
name|corruptOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

