begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
comment|/**  * Supports zero-copy reads.  */
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
DECL|interface|SupportsZeroCopy
specifier|public
interface|interface
name|SupportsZeroCopy
block|{
comment|/**    * Get a zero-copy cursor to use for zero-copy reads.    *    * @throws IOException    *     If there was an error creating the ZeroCopyCursor    * @throws UnsupportedOperationException    *     If this stream does not support zero-copy reads.    *     This is used, for example, when one stream wraps another    *     which may or may not support ZCR.    */
DECL|method|createZeroCopyCursor ()
specifier|public
name|ZeroCopyCursor
name|createZeroCopyCursor
parameter_list|()
throws|throws
name|IOException
throws|,
name|ZeroCopyUnavailableException
function_decl|;
block|}
end_interface

end_unit

