begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|hdfs
operator|.
name|DFSUtil
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
name|OzoneConsts
import|;
end_import

begin_comment
comment|/**  * An utility class to filter levelDB keys.  */
end_comment

begin_class
DECL|class|MetadataKeyFilters
specifier|public
specifier|final
class|class
name|MetadataKeyFilters
block|{
DECL|field|deletingKeyFilter
specifier|private
specifier|static
name|KeyPrefixFilter
name|deletingKeyFilter
init|=
operator|new
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
DECL|field|normalKeyFilter
specifier|private
specifier|static
name|KeyPrefixFilter
name|normalKeyFilter
init|=
operator|new
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|method|MetadataKeyFilters ()
specifier|private
name|MetadataKeyFilters
parameter_list|()
block|{   }
DECL|method|getDeletingKeyFilter ()
specifier|public
specifier|static
name|KeyPrefixFilter
name|getDeletingKeyFilter
parameter_list|()
block|{
return|return
name|deletingKeyFilter
return|;
block|}
DECL|method|getNormalKeyFilter ()
specifier|public
specifier|static
name|KeyPrefixFilter
name|getNormalKeyFilter
parameter_list|()
block|{
return|return
name|normalKeyFilter
return|;
block|}
comment|/**    * Interface for levelDB key filters.    */
DECL|interface|MetadataKeyFilter
specifier|public
interface|interface
name|MetadataKeyFilter
block|{
comment|/**      * Filter levelDB key with a certain condition.      *      * @param preKey     previous key.      * @param currentKey current key.      * @param nextKey    next key.      * @return true if a certain condition satisfied, return false otherwise.      */
DECL|method|filterKey (byte[] preKey, byte[] currentKey, byte[] nextKey)
name|boolean
name|filterKey
parameter_list|(
name|byte
index|[]
name|preKey
parameter_list|,
name|byte
index|[]
name|currentKey
parameter_list|,
name|byte
index|[]
name|nextKey
parameter_list|)
function_decl|;
DECL|method|getKeysScannedNum ()
specifier|default
name|int
name|getKeysScannedNum
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getKeysHintedNum ()
specifier|default
name|int
name|getKeysHintedNum
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Utility class to filter key by a string prefix. This filter    * assumes keys can be parsed to a string.    */
DECL|class|KeyPrefixFilter
specifier|public
specifier|static
class|class
name|KeyPrefixFilter
implements|implements
name|MetadataKeyFilter
block|{
DECL|field|keyPrefix
specifier|private
name|String
name|keyPrefix
init|=
literal|null
decl_stmt|;
DECL|field|keysScanned
specifier|private
name|int
name|keysScanned
init|=
literal|0
decl_stmt|;
DECL|field|keysHinted
specifier|private
name|int
name|keysHinted
init|=
literal|0
decl_stmt|;
DECL|field|negative
specifier|private
name|Boolean
name|negative
decl_stmt|;
DECL|method|KeyPrefixFilter (String keyPrefix)
specifier|public
name|KeyPrefixFilter
parameter_list|(
name|String
name|keyPrefix
parameter_list|)
block|{
name|this
argument_list|(
name|keyPrefix
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|KeyPrefixFilter (String keyPrefix, boolean negative)
specifier|public
name|KeyPrefixFilter
parameter_list|(
name|String
name|keyPrefix
parameter_list|,
name|boolean
name|negative
parameter_list|)
block|{
name|this
operator|.
name|keyPrefix
operator|=
name|keyPrefix
expr_stmt|;
name|this
operator|.
name|negative
operator|=
name|negative
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|filterKey (byte[] preKey, byte[] currentKey, byte[] nextKey)
specifier|public
name|boolean
name|filterKey
parameter_list|(
name|byte
index|[]
name|preKey
parameter_list|,
name|byte
index|[]
name|currentKey
parameter_list|,
name|byte
index|[]
name|nextKey
parameter_list|)
block|{
name|keysScanned
operator|++
expr_stmt|;
name|boolean
name|accept
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|keyPrefix
argument_list|)
condition|)
block|{
name|accept
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|byte
index|[]
name|prefixBytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|keyPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentKey
operator|!=
literal|null
operator|&&
name|prefixMatch
argument_list|(
name|prefixBytes
argument_list|,
name|currentKey
argument_list|)
condition|)
block|{
name|keysHinted
operator|++
expr_stmt|;
name|accept
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|accept
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
operator|(
name|negative
operator|)
condition|?
operator|!
name|accept
else|:
name|accept
return|;
block|}
annotation|@
name|Override
DECL|method|getKeysScannedNum ()
specifier|public
name|int
name|getKeysScannedNum
parameter_list|()
block|{
return|return
name|keysScanned
return|;
block|}
annotation|@
name|Override
DECL|method|getKeysHintedNum ()
specifier|public
name|int
name|getKeysHintedNum
parameter_list|()
block|{
return|return
name|keysHinted
return|;
block|}
DECL|method|prefixMatch (byte[] prefix, byte[] key)
specifier|private
name|boolean
name|prefixMatch
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|byte
index|[]
name|key
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|length
operator|<
name|prefix
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|key
index|[
name|i
index|]
operator|!=
name|prefix
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

