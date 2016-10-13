begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ChunkInfo
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
comment|/**  * Chunk Manager allows read, write, delete and listing of chunks in  * a container.  */
end_comment

begin_interface
DECL|interface|ChunkManager
specifier|public
interface|interface
name|ChunkManager
block|{
comment|/**    * writes a given chunk.    * @param pipeline - Name and the set of machines that make this container.    * @param keyName - Name of the Key.    * @param info - ChunkInfo.    * @throws IOException    */
DECL|method|writeChunk (Pipeline pipeline, String keyName, ChunkInfo info, byte[] data)
name|void
name|writeChunk
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|keyName
parameter_list|,
name|ChunkInfo
name|info
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * reads the data defined by a chunk.    * @param pipeline - container pipeline.    * @param keyName - Name of the Key    * @param info - ChunkInfo.    * @return  byte array    * @throws IOException    *    * TODO: Right now we do not support partial reads and writes of chunks.    * TODO: Explore if we need to do that for ozone.    */
DECL|method|readChunk (Pipeline pipeline, String keyName, ChunkInfo info)
name|byte
index|[]
name|readChunk
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|keyName
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a given chunk.    * @param pipeline  - Pipeline.    * @param keyName   - Key Name    * @param info  - Chunk Info    * @throws IOException    */
DECL|method|deleteChunk (Pipeline pipeline, String keyName, ChunkInfo info)
name|void
name|deleteChunk
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|keyName
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// TODO : Support list operations.
comment|/**    * Shutdown the chunkManager.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

