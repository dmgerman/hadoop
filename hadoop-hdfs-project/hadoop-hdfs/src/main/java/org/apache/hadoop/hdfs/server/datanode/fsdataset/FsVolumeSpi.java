begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
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
name|datanode
operator|.
name|fsdataset
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
comment|/**  * This is an interface for the underlying volume.  */
end_comment

begin_interface
DECL|interface|FsVolumeSpi
specifier|public
interface|interface
name|FsVolumeSpi
block|{
comment|/** @return a list of block pools. */
DECL|method|getBlockPoolList ()
specifier|public
name|String
index|[]
name|getBlockPoolList
parameter_list|()
function_decl|;
comment|/** @return the available storage space in bytes. */
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** @return the base path to the volume */
DECL|method|getBasePath ()
specifier|public
name|String
name|getBasePath
parameter_list|()
function_decl|;
comment|/** @return the path to the volume */
DECL|method|getPath (String bpid)
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @return the directory for the finalized blocks in the block pool. */
DECL|method|getFinalizedDir (String bpid)
specifier|public
name|File
name|getFinalizedDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

