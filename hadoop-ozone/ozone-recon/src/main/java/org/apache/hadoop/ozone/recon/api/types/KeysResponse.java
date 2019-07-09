begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.api.types
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|api
operator|.
name|types
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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
name|web
operator|.
name|utils
operator|.
name|JsonUtils
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Class that represents the API Response structure of Keys within a container.  */
end_comment

begin_class
DECL|class|KeysResponse
specifier|public
class|class
name|KeysResponse
block|{
comment|/**    * Contains a map with total count of keys inside the given container and a    * list of keys with metadata.    */
annotation|@
name|JsonProperty
argument_list|(
literal|"data"
argument_list|)
DECL|field|keysResponseData
specifier|private
name|KeysResponseData
name|keysResponseData
decl_stmt|;
DECL|method|KeysResponse ()
specifier|public
name|KeysResponse
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|KeysResponse (long totalCount, Collection<KeyMetadata> keys)
specifier|public
name|KeysResponse
parameter_list|(
name|long
name|totalCount
parameter_list|,
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
name|keys
parameter_list|)
block|{
name|this
operator|.
name|keysResponseData
operator|=
operator|new
name|KeysResponseData
argument_list|(
name|totalCount
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
DECL|method|toJsonString ()
specifier|public
name|String
name|toJsonString
parameter_list|()
block|{
try|try
block|{
return|return
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getKeysResponseData ()
specifier|public
name|KeysResponseData
name|getKeysResponseData
parameter_list|()
block|{
return|return
name|keysResponseData
return|;
block|}
DECL|method|setKeysResponseData (KeysResponseData keysResponseData)
specifier|public
name|void
name|setKeysResponseData
parameter_list|(
name|KeysResponseData
name|keysResponseData
parameter_list|)
block|{
name|this
operator|.
name|keysResponseData
operator|=
name|keysResponseData
expr_stmt|;
block|}
comment|/**    * Class that encapsulates the data presented in Keys API Response.    */
DECL|class|KeysResponseData
specifier|public
specifier|static
class|class
name|KeysResponseData
block|{
comment|/**      * Total count of the keys.      */
annotation|@
name|JsonProperty
argument_list|(
literal|"totalCount"
argument_list|)
DECL|field|totalCount
specifier|private
name|long
name|totalCount
decl_stmt|;
comment|/**      * An array of keys.      */
annotation|@
name|JsonProperty
argument_list|(
literal|"keys"
argument_list|)
DECL|field|keys
specifier|private
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
name|keys
decl_stmt|;
DECL|method|KeysResponseData (long totalCount, Collection<KeyMetadata> keys)
name|KeysResponseData
parameter_list|(
name|long
name|totalCount
parameter_list|,
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
name|keys
parameter_list|)
block|{
name|this
operator|.
name|totalCount
operator|=
name|totalCount
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
block|}
DECL|method|getTotalCount ()
specifier|public
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
name|totalCount
return|;
block|}
DECL|method|getKeys ()
specifier|public
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
name|keys
return|;
block|}
block|}
block|}
end_class

end_unit

