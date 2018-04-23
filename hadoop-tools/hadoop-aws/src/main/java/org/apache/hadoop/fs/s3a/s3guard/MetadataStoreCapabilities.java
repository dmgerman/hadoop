begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
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
name|s3guard
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
comment|/**  * All the capability constants used for the  * {@link MetadataStore} implementations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetadataStoreCapabilities
specifier|public
specifier|final
class|class
name|MetadataStoreCapabilities
block|{
DECL|method|MetadataStoreCapabilities ()
specifier|private
name|MetadataStoreCapabilities
parameter_list|()
block|{   }
comment|/**    *  This capability tells if the metadata store supports authoritative    *  directories. Used in {@link MetadataStore#getDiagnostics()} as a key    *  for this capability. The value can be boolean true or false.    *  If the Map.get() returns null for this key, that is interpreted as false.    */
DECL|field|PERSISTS_AUTHORITATIVE_BIT
specifier|public
specifier|static
specifier|final
name|String
name|PERSISTS_AUTHORITATIVE_BIT
init|=
literal|"persist.authoritative.bit"
decl_stmt|;
block|}
end_class

end_unit

