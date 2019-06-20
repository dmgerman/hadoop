begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|impl
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
name|s3a
operator|.
name|Retries
import|;
end_import

begin_comment
comment|/**  * An interface to implement for providing accessors to  * S3AFileSystem-level API calls.  *<p>  * This is used to avoid giving any explicit reference to the owning  * FS in the store context; there are enough calls that using lambda-expressions  * gets over-complex.  *<ol>  *<li>Test suites are free to provide their own implementation, using  *  * the S3AFileSystem methods as the normative reference.</li>  *<li>All implementations<i>MUST</i> translate exceptions.</li>  *</ol>  */
end_comment

begin_interface
DECL|interface|ContextAccessors
specifier|public
interface|interface
name|ContextAccessors
block|{
comment|/**    * Convert a key to a fully qualified path.    * @param key input key    * @return the fully qualified path including URI scheme and bucket name.    */
DECL|method|keyToPath (String key)
name|Path
name|keyToPath
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Turns a path (relative or otherwise) into an S3 key.    *    * @param path input path, may be relative to the working dir    * @return a key excluding the leading "/", or, if it is the root path, ""    */
DECL|method|pathToKey (Path path)
name|String
name|pathToKey
parameter_list|(
name|Path
name|path
parameter_list|)
function_decl|;
comment|/**    * Create a temporary file.    * @param prefix prefix for the temporary file    * @param size the size of the file that is going to be written    * @return a unique temporary file    * @throws IOException IO problems    */
DECL|method|createTempFile (String prefix, long size)
name|File
name|createTempFile
parameter_list|(
name|String
name|prefix
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the region of a bucket. This may be via an S3 API call if not    * already cached.    * @return the region in which a bucket is located    * @throws IOException on any failure.    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|getBucketLocation ()
name|String
name|getBucketLocation
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

