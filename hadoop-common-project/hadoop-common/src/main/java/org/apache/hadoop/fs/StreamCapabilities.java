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
comment|/**  * Interface to query streams for supported capabilities.  */
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
comment|/**    * Capabilities that a stream can support and be queried for.    */
DECL|enum|StreamCapability
enum|enum
name|StreamCapability
block|{
comment|/**      * Stream hflush capability to flush out the data in client's buffer.      * Streams with this capability implement {@link Syncable} and support      * {@link Syncable#hflush()}.      */
DECL|enumConstant|HFLUSH
name|HFLUSH
argument_list|(
literal|"hflush"
argument_list|)
block|,
comment|/**      * Stream hsync capability to flush out the data in client's buffer and      * the disk device. Streams with this capability implement {@link Syncable}      * and support {@link Syncable#hsync()}.      */
DECL|enumConstant|HSYNC
name|HSYNC
argument_list|(
literal|"hsync"
argument_list|)
block|;
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
comment|/**    * Query the stream for a specific capability.    *    * @param capability string to query the stream support for.    * @return True if the stream supports capability.    */
DECL|method|hasCapability (String capability)
name|boolean
name|hasCapability
parameter_list|(
name|String
name|capability
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

