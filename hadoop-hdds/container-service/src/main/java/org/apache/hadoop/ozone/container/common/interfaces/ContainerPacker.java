begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
import|;
end_import

begin_comment
comment|/**  * Service to pack/unpack ContainerData container data to/from a single byte  * stream.  */
end_comment

begin_interface
DECL|interface|ContainerPacker
specifier|public
interface|interface
name|ContainerPacker
parameter_list|<
name|CONTAINERDATA
extends|extends
name|ContainerData
parameter_list|>
block|{
comment|/**    * Extract the container data to the path defined by the container.    *<p>    * This doesn't contain the extraction of the container descriptor file.    *    * @return the byte content of the descriptor (which won't be written to a    * file but returned).    */
DECL|method|unpackContainerData (Container<CONTAINERDATA> container, InputStream inputStream)
name|byte
index|[]
name|unpackContainerData
parameter_list|(
name|Container
argument_list|<
name|CONTAINERDATA
argument_list|>
name|container
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Compress all the container data (chunk data, metadata db AND container    * descriptor) to one single archive.    */
DECL|method|pack (Container<CONTAINERDATA> container, OutputStream destination)
name|void
name|pack
parameter_list|(
name|Container
argument_list|<
name|CONTAINERDATA
argument_list|>
name|container
parameter_list|,
name|OutputStream
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read the descriptor from the finished archive to get the data before    * importing the container.    */
DECL|method|unpackContainerDescriptor (InputStream inputStream)
name|byte
index|[]
name|unpackContainerDescriptor
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

