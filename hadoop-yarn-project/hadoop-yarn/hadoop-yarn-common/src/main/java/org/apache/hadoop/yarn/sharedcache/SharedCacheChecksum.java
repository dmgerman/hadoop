begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sharedcache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sharedcache
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Evolving
import|;
end_import

begin_interface
annotation|@
name|Public
annotation|@
name|Evolving
comment|/**  * An interface to calculate a checksum for a resource in the shared cache. The  * checksum implementation should be thread safe.  */
DECL|interface|SharedCacheChecksum
specifier|public
interface|interface
name|SharedCacheChecksum
block|{
comment|/**    * Calculate the checksum of the passed input stream.    *    * @param in<code>InputStream</code> to be checksumed    * @return the message digest of the input stream    * @throws IOException    */
DECL|method|computeChecksum (InputStream in)
specifier|public
name|String
name|computeChecksum
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

