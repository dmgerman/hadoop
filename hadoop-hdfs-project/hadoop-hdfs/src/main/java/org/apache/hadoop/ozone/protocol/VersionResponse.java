begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|KeyValue
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Version response class.  */
end_comment

begin_class
DECL|class|VersionResponse
specifier|public
class|class
name|VersionResponse
block|{
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|values
decl_stmt|;
comment|/**    * Creates a version response class.    * @param version    * @param values    */
DECL|method|VersionResponse (int version, Map<String, String> values)
specifier|public
name|VersionResponse
parameter_list|(
name|int
name|version
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
comment|/**    * Creates a version Response class.    * @param version    */
DECL|method|VersionResponse (int version)
specifier|public
name|VersionResponse
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a new Builder.    * @return - Builder.    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Returns this class from protobuf message.    * @param response - SCMVersionResponseProto    * @return VersionResponse    */
DECL|method|getFromProtobuf (SCMVersionResponseProto response)
specifier|public
specifier|static
name|VersionResponse
name|getFromProtobuf
parameter_list|(
name|SCMVersionResponseProto
name|response
parameter_list|)
block|{
return|return
operator|new
name|VersionResponse
argument_list|(
name|response
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|,
name|response
operator|.
name|getKeysList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|KeyValue
operator|::
name|getKey
argument_list|,
name|KeyValue
operator|::
name|getValue
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Adds a value to version Response.    * @param key - String    * @param value - String    */
DECL|method|put (String key, String value)
specifier|public
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|values
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate key in version response"
argument_list|)
throw|;
block|}
name|values
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a protobuf message.    * @return SCMVersionResponseProto.    */
DECL|method|getProtobufMessage ()
specifier|public
name|SCMVersionResponseProto
name|getProtobufMessage
parameter_list|()
block|{
name|List
argument_list|<
name|KeyValue
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|values
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|KeyValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|SCMVersionResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSoftwareVersion
argument_list|(
name|this
operator|.
name|version
argument_list|)
operator|.
name|addAllKeys
argument_list|(
name|list
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Builder class.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
DECL|field|values
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|values
decl_stmt|;
DECL|method|Builder ()
name|Builder
parameter_list|()
block|{
name|values
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the version.      * @param ver - version      * @return Builder      */
DECL|method|setVersion (int ver)
specifier|public
name|Builder
name|setVersion
parameter_list|(
name|int
name|ver
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|ver
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a value to version Response.      * @param key - String      * @param value - String      */
DECL|method|addValue (String key, String value)
specifier|public
name|Builder
name|addValue
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|values
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate key in version response"
argument_list|)
throw|;
block|}
name|values
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds the version response.      * @return VersionResponse.      */
DECL|method|build ()
specifier|public
name|VersionResponse
name|build
parameter_list|()
block|{
return|return
operator|new
name|VersionResponse
argument_list|(
name|this
operator|.
name|version
argument_list|,
name|this
operator|.
name|values
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

