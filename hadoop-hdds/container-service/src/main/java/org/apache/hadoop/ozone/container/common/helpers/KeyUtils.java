begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
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
name|helpers
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|utils
operator|.
name|ContainerCache
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
name|utils
operator|.
name|MetadataStore
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|NO_SUCH_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNABLE_TO_READ_METADATA_DB
import|;
end_import

begin_comment
comment|/**  * Utils functions to help key functions.  */
end_comment

begin_class
DECL|class|KeyUtils
specifier|public
specifier|final
class|class
name|KeyUtils
block|{
DECL|field|ENCODING_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ENCODING_NAME
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|ENCODING
specifier|public
specifier|static
specifier|final
name|Charset
name|ENCODING
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|ENCODING_NAME
argument_list|)
decl_stmt|;
comment|/**    * Never Constructed.    */
DECL|method|KeyUtils ()
specifier|private
name|KeyUtils
parameter_list|()
block|{   }
comment|/**    * Get a DB handler for a given container.    * If the handler doesn't exist in cache yet, first create one and    * add into cache. This function is called with containerManager    * ReadLock held.    *    * @param container container.    * @param conf configuration.    * @return MetadataStore handle.    * @throws StorageContainerException    */
DECL|method|getDB (ContainerData container, Configuration conf)
specifier|public
specifier|static
name|MetadataStore
name|getDB
parameter_list|(
name|ContainerData
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|ContainerCache
name|cache
init|=
name|ContainerCache
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|cache
operator|.
name|getDB
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerDBType
argument_list|()
argument_list|,
name|container
operator|.
name|getDBPath
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Unable to open DB. DB Name: %s, Path: %s. ex: %s"
argument_list|,
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|container
operator|.
name|getDBPath
argument_list|()
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|message
argument_list|,
name|UNABLE_TO_READ_METADATA_DB
argument_list|)
throw|;
block|}
block|}
comment|/**    * Remove a DB handler from cache.    *    * @param container - Container data.    * @param conf - Configuration.    */
DECL|method|removeDB (ContainerData container, Configuration conf)
specifier|public
specifier|static
name|void
name|removeDB
parameter_list|(
name|ContainerData
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|ContainerCache
name|cache
init|=
name|ContainerCache
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|cache
operator|.
name|removeDB
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown all DB Handles.    *    * @param cache - Cache for DB Handles.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|shutdownCache (ContainerCache cache)
specifier|public
specifier|static
name|void
name|shutdownCache
parameter_list|(
name|ContainerCache
name|cache
parameter_list|)
block|{
name|cache
operator|.
name|shutdownCache
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns successful keyResponse.    * @param msg - Request.    * @return Response.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|getKeyResponse (ContainerProtos.ContainerCommandRequestProto msg)
name|getKeyResponse
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|ContainerUtils
operator|.
name|getContainerResponse
argument_list|(
name|msg
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|getKeyDataResponse (ContainerProtos.ContainerCommandRequestProto msg, KeyData data)
name|getKeyDataResponse
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|KeyData
name|data
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|GetKeyResponseProto
operator|.
name|Builder
name|getKey
init|=
name|ContainerProtos
operator|.
name|GetKeyResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|getKey
operator|.
name|setKeyData
argument_list|(
name|data
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setGetKey
argument_list|(
name|getKey
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parses the key name from a bytes array.    * @param bytes key name in bytes.    * @return key name string.    */
DECL|method|getKeyName (byte[] bytes)
specifier|public
specifier|static
name|String
name|getKeyName
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|ENCODING
argument_list|)
return|;
block|}
comment|/**    * Parses the {@link KeyData} from a bytes array.    *    * @param bytes key data in bytes.    * @return key data.    * @throws IOException if the bytes array is malformed or invalid.    */
DECL|method|getKeyData (byte[] bytes)
specifier|public
specifier|static
name|KeyData
name|getKeyData
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|ContainerProtos
operator|.
name|KeyData
name|kd
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|KeyData
name|data
init|=
name|KeyData
operator|.
name|getFromProtoBuf
argument_list|(
name|kd
argument_list|)
decl_stmt|;
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Failed to parse key data from the"
operator|+
literal|" bytes array."
argument_list|,
name|NO_SUCH_KEY
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

