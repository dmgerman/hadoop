begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3native
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
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
name|io
operator|.
name|InputStream
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

begin_comment
comment|/**  *<p>  * An abstraction for a key-based {@link File} store.  *</p>  */
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
DECL|interface|NativeFileSystemStore
interface|interface
name|NativeFileSystemStore
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
DECL|method|storeFile (String key, File file, byte[] md5Hash)
name|void
name|storeFile
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|,
name|byte
index|[]
name|md5Hash
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|storeEmptyFile (String key)
name|void
name|storeEmptyFile
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieveMetadata (String key)
name|FileMetadata
name|retrieveMetadata
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieve (String key)
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieve (String key, long byteRangeStart)
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|byteRangeStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|list (String prefix, int maxListingLength)
name|PartialListing
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|maxListingLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|list (String prefix, int maxListingLength, String priorLastKey, boolean recursive)
name|PartialListing
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|maxListingLength
parameter_list|,
name|String
name|priorLastKey
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|delete (String key)
name|void
name|delete
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|copy (String srcKey, String dstKey)
name|void
name|copy
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete all keys with the given prefix. Used for testing.    * @throws IOException    */
DECL|method|purge (String prefix)
name|void
name|purge
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Diagnostic method to dump state to the console.    * @throws IOException    */
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

