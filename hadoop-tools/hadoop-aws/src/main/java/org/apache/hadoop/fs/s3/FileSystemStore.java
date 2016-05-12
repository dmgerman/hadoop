begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Path
import|;
end_import

begin_comment
comment|/**  * A facility for storing and retrieving {@link INode}s and {@link Block}s.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|FileSystemStore
specifier|public
interface|interface
name|FileSystemStore
block|{
DECL|method|initialize (URI uri, Configuration conf)
name|void
name|initialize
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getVersion ()
name|String
name|getVersion
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|storeINode (Path path, INode inode)
name|void
name|storeINode
parameter_list|(
name|Path
name|path
parameter_list|,
name|INode
name|inode
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|storeBlock (Block block, File file)
name|void
name|storeBlock
parameter_list|(
name|Block
name|block
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|inodeExists (Path path)
name|boolean
name|inodeExists
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|blockExists (long blockId)
name|boolean
name|blockExists
parameter_list|(
name|long
name|blockId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieveINode (Path path)
name|INode
name|retrieveINode
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieveBlock (Block block, long byteRangeStart)
name|File
name|retrieveBlock
parameter_list|(
name|Block
name|block
parameter_list|,
name|long
name|byteRangeStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteINode (Path path)
name|void
name|deleteINode
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteBlock (Block block)
name|void
name|deleteBlock
parameter_list|(
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|listSubPaths (Path path)
name|Set
argument_list|<
name|Path
argument_list|>
name|listSubPaths
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|listDeepSubPaths (Path path)
name|Set
argument_list|<
name|Path
argument_list|>
name|listDeepSubPaths
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete everything. Used for testing.    * @throws IOException on any problem    */
DECL|method|purge ()
name|void
name|purge
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Diagnostic method to dump all INodes to the console.    * @throws IOException on any problem    */
DECL|method|dump ()
name|void
name|dump
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

