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
comment|/**  * Interface to query streams for supported capabilities.  *  * Capability strings must be in lower case.  *  * Constant strings are chosen over enums in order to allow other file systems  * to define their own capabilities.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|StreamCapabilities
specifier|public
interface|interface
name|StreamCapabilities
block|{
comment|/**    * Stream hflush capability implemented by {@link Syncable#hflush()}.    */
DECL|field|HFLUSH
name|String
name|HFLUSH
init|=
literal|"hflush"
decl_stmt|;
comment|/**    * Stream hsync capability implemented by {@link Syncable#hsync()}.    */
DECL|field|HSYNC
name|String
name|HSYNC
init|=
literal|"hsync"
decl_stmt|;
comment|/**    * Stream setReadahead capability implemented by    * {@link CanSetReadahead#setReadahead(Long)}.    */
DECL|field|READAHEAD
name|String
name|READAHEAD
init|=
literal|"in:readahead"
decl_stmt|;
comment|/**    * Stream setDropBehind capability implemented by    * {@link CanSetDropBehind#setDropBehind(Boolean)}.    */
DECL|field|DROPBEHIND
name|String
name|DROPBEHIND
init|=
literal|"dropbehind"
decl_stmt|;
comment|/**    * Stream unbuffer capability implemented by {@link CanUnbuffer#unbuffer()}.    */
DECL|field|UNBUFFER
name|String
name|UNBUFFER
init|=
literal|"in:unbuffer"
decl_stmt|;
comment|/**    * Stream read(ByteBuffer) capability implemented by    * {@link ByteBufferReadable#read(java.nio.ByteBuffer)}.    */
DECL|field|READBYTEBUFFER
name|String
name|READBYTEBUFFER
init|=
literal|"in:readbytebuffer"
decl_stmt|;
comment|/**    * Stream read(long, ByteBuffer) capability implemented by    * {@link ByteBufferPositionedReadable#read(long, java.nio.ByteBuffer)}.    */
DECL|field|PREADBYTEBUFFER
name|String
name|PREADBYTEBUFFER
init|=
literal|"in:preadbytebuffer"
decl_stmt|;
comment|/**    * Capabilities that a stream can support and be queried for.    */
annotation|@
name|Deprecated
DECL|enum|StreamCapability
enum|enum
name|StreamCapability
block|{
DECL|enumConstant|HFLUSH
name|HFLUSH
parameter_list|(
name|StreamCapabilities
operator|.
name|HFLUSH
parameter_list|)
operator|,
DECL|enumConstant|HSYNC
constructor|HSYNC(StreamCapabilities.HSYNC
block|)
enum|;
DECL|field|capability
specifier|private
specifier|final
name|String
name|capability
decl_stmt|;
DECL|method|StreamCapability (String value)
name|StreamCapability
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|capability
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
specifier|final
name|String
name|getValue
parameter_list|()
block|{
return|return
name|capability
return|;
block|}
block|}
end_interface

begin_comment
comment|/**    * Query the stream for a specific capability.    *    * @param capability string to query the stream support for.    * @return True if the stream supports capability.    */
end_comment

begin_function_decl
DECL|method|hasCapability (String capability)
name|boolean
name|hasCapability
parameter_list|(
name|String
name|capability
parameter_list|)
function_decl|;
end_function_decl

unit|}
end_unit

