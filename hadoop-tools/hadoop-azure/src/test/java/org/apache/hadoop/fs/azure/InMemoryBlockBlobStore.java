begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|OutputStream
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
name|HashMap
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

begin_comment
comment|/**  * A simple memory key-value store to help mock the Windows Azure Storage  * implementation for unit testing.  */
end_comment

begin_class
DECL|class|InMemoryBlockBlobStore
specifier|public
class|class
name|InMemoryBlockBlobStore
block|{
DECL|field|blobs
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Entry
argument_list|>
name|blobs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Entry
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|containerMetadata
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerMetadata
decl_stmt|;
DECL|method|getKeys ()
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|blobs
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ListBlobEntry
specifier|public
specifier|static
class|class
name|ListBlobEntry
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|metadata
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
DECL|field|contentLength
specifier|private
specifier|final
name|int
name|contentLength
decl_stmt|;
DECL|field|isPageBlob
specifier|private
specifier|final
name|boolean
name|isPageBlob
decl_stmt|;
DECL|method|ListBlobEntry (String key, HashMap<String, String> metadata, int contentLength, boolean isPageBlob)
name|ListBlobEntry
parameter_list|(
name|String
name|key
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
name|int
name|contentLength
parameter_list|,
name|boolean
name|isPageBlob
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|this
operator|.
name|isPageBlob
operator|=
name|isPageBlob
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getMetadata ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
DECL|method|getContentLength ()
specifier|public
name|int
name|getContentLength
parameter_list|()
block|{
return|return
name|contentLength
return|;
block|}
DECL|method|isPageBlob ()
specifier|public
name|boolean
name|isPageBlob
parameter_list|()
block|{
return|return
name|isPageBlob
return|;
block|}
block|}
comment|/**    * List all the blobs whose key starts with the given prefix.    *     * @param prefix    *          The prefix to check.    * @param includeMetadata    *          If set, the metadata in the returned listing will be populated;    *          otherwise it'll be null.    * @return The listing.    */
DECL|method|listBlobs (String prefix, boolean includeMetadata)
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|ListBlobEntry
argument_list|>
name|listBlobs
parameter_list|(
name|String
name|prefix
parameter_list|,
name|boolean
name|includeMetadata
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ListBlobEntry
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ListBlobEntry
argument_list|>
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
name|Entry
argument_list|>
name|entry
range|:
name|blobs
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|ListBlobEntry
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|includeMetadata
condition|?
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|metadata
argument_list|)
else|:
literal|null
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|content
operator|.
name|length
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|isPageBlob
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
DECL|method|getContent (String key)
specifier|public
specifier|synchronized
name|byte
index|[]
name|getContent
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|blobs
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|content
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setContent (String key, byte[] value, HashMap<String, String> metadata, boolean isPageBlob, long length)
specifier|public
specifier|synchronized
name|void
name|setContent
parameter_list|(
name|String
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
name|boolean
name|isPageBlob
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|blobs
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Entry
argument_list|(
name|value
argument_list|,
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|metadata
operator|.
name|clone
argument_list|()
argument_list|,
name|isPageBlob
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setMetadata (String key, HashMap<String, String> metadata)
specifier|public
specifier|synchronized
name|void
name|setMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|blobs
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|metadata
operator|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|metadata
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
DECL|method|uploadBlockBlob (final String key, final HashMap<String, String> metadata)
specifier|public
name|OutputStream
name|uploadBlockBlob
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|setContent
argument_list|(
name|key
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayOutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
name|byte
index|[]
name|tempBytes
init|=
name|toByteArray
argument_list|()
decl_stmt|;
name|setContent
argument_list|(
name|key
argument_list|,
name|tempBytes
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|,
name|tempBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|tempBytes
init|=
name|toByteArray
argument_list|()
decl_stmt|;
name|setContent
argument_list|(
name|key
argument_list|,
name|tempBytes
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|,
name|tempBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|uploadPageBlob (final String key, final HashMap<String, String> metadata, final long length)
specifier|public
name|OutputStream
name|uploadPageBlob
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
block|{
name|setContent
argument_list|(
name|key
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|metadata
argument_list|,
literal|true
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayOutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
name|setContent
argument_list|(
name|key
argument_list|,
name|toByteArray
argument_list|()
argument_list|,
name|metadata
argument_list|,
literal|true
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|copy (String sourceKey, String destKey)
specifier|public
specifier|synchronized
name|void
name|copy
parameter_list|(
name|String
name|sourceKey
parameter_list|,
name|String
name|destKey
parameter_list|)
block|{
name|blobs
operator|.
name|put
argument_list|(
name|destKey
argument_list|,
name|blobs
operator|.
name|get
argument_list|(
name|sourceKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (String key)
specifier|public
specifier|synchronized
name|void
name|delete
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|blobs
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|exists (String key)
specifier|public
specifier|synchronized
name|boolean
name|exists
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|blobs
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getMetadata (String key)
specifier|public
specifier|synchronized
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|blobs
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|metadata
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getContainerMetadata ()
specifier|public
specifier|synchronized
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getContainerMetadata
parameter_list|()
block|{
return|return
name|containerMetadata
return|;
block|}
DECL|method|setContainerMetadata (HashMap<String, String> metadata)
specifier|public
specifier|synchronized
name|void
name|setContainerMetadata
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|containerMetadata
operator|=
name|metadata
expr_stmt|;
block|}
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|content
specifier|private
name|byte
index|[]
name|content
decl_stmt|;
DECL|field|metadata
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
DECL|field|isPageBlob
specifier|private
name|boolean
name|isPageBlob
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// TODO: use it
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|method|Entry (byte[] content, HashMap<String, String> metadata, boolean isPageBlob, long length)
specifier|public
name|Entry
parameter_list|(
name|byte
index|[]
name|content
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
name|boolean
name|isPageBlob
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|isPageBlob
operator|=
name|isPageBlob
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

