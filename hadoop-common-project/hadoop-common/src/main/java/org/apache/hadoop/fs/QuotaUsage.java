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
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/** Store the quota usage of a directory. */
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
DECL|class|QuotaUsage
specifier|public
class|class
name|QuotaUsage
block|{
DECL|field|fileAndDirectoryCount
specifier|private
name|long
name|fileAndDirectoryCount
decl_stmt|;
comment|// Make the followings protected so that
comment|// deprecated ContentSummary constructor can use them.
DECL|field|quota
specifier|private
name|long
name|quota
decl_stmt|;
DECL|field|spaceConsumed
specifier|private
name|long
name|spaceConsumed
decl_stmt|;
DECL|field|spaceQuota
specifier|private
name|long
name|spaceQuota
decl_stmt|;
DECL|field|typeConsumed
specifier|private
name|long
index|[]
name|typeConsumed
decl_stmt|;
DECL|field|typeQuota
specifier|private
name|long
index|[]
name|typeQuota
decl_stmt|;
comment|/** Builder class for QuotaUsage. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{
name|this
operator|.
name|quota
operator|=
operator|-
literal|1L
expr_stmt|;
name|this
operator|.
name|spaceQuota
operator|=
operator|-
literal|1L
expr_stmt|;
name|typeConsumed
operator|=
operator|new
name|long
index|[
name|StorageType
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
name|typeQuota
operator|=
operator|new
name|long
index|[
name|StorageType
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|typeQuota
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
DECL|method|fileAndDirectoryCount (long count)
specifier|public
name|Builder
name|fileAndDirectoryCount
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|fileAndDirectoryCount
operator|=
name|count
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|quota (long quota)
specifier|public
name|Builder
name|quota
parameter_list|(
name|long
name|quota
parameter_list|)
block|{
name|this
operator|.
name|quota
operator|=
name|quota
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|spaceConsumed (long spaceConsumed)
specifier|public
name|Builder
name|spaceConsumed
parameter_list|(
name|long
name|spaceConsumed
parameter_list|)
block|{
name|this
operator|.
name|spaceConsumed
operator|=
name|spaceConsumed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|spaceQuota (long spaceQuota)
specifier|public
name|Builder
name|spaceQuota
parameter_list|(
name|long
name|spaceQuota
parameter_list|)
block|{
name|this
operator|.
name|spaceQuota
operator|=
name|spaceQuota
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeConsumed (long[] typeConsumed)
specifier|public
name|Builder
name|typeConsumed
parameter_list|(
name|long
index|[]
name|typeConsumed
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|typeConsumed
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|typeConsumed
argument_list|,
literal|0
argument_list|,
name|typeConsumed
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeQuota (StorageType type, long quota)
specifier|public
name|Builder
name|typeQuota
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|quota
parameter_list|)
block|{
name|this
operator|.
name|typeQuota
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|quota
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeConsumed (StorageType type, long consumed)
specifier|public
name|Builder
name|typeConsumed
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|consumed
parameter_list|)
block|{
name|this
operator|.
name|typeConsumed
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|consumed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeQuota (long[] typeQuota)
specifier|public
name|Builder
name|typeQuota
parameter_list|(
name|long
index|[]
name|typeQuota
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|typeQuota
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|typeQuota
argument_list|,
literal|0
argument_list|,
name|typeQuota
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|QuotaUsage
name|build
parameter_list|()
block|{
return|return
operator|new
name|QuotaUsage
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|field|fileAndDirectoryCount
specifier|private
name|long
name|fileAndDirectoryCount
decl_stmt|;
DECL|field|quota
specifier|private
name|long
name|quota
decl_stmt|;
DECL|field|spaceConsumed
specifier|private
name|long
name|spaceConsumed
decl_stmt|;
DECL|field|spaceQuota
specifier|private
name|long
name|spaceQuota
decl_stmt|;
DECL|field|typeConsumed
specifier|private
name|long
index|[]
name|typeConsumed
decl_stmt|;
DECL|field|typeQuota
specifier|private
name|long
index|[]
name|typeQuota
decl_stmt|;
block|}
comment|// Make it protected for the deprecated ContentSummary constructor.
DECL|method|QuotaUsage ()
specifier|protected
name|QuotaUsage
parameter_list|()
block|{ }
comment|/** Build the instance based on the builder. */
DECL|method|QuotaUsage (Builder builder)
specifier|protected
name|QuotaUsage
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|fileAndDirectoryCount
operator|=
name|builder
operator|.
name|fileAndDirectoryCount
expr_stmt|;
name|this
operator|.
name|quota
operator|=
name|builder
operator|.
name|quota
expr_stmt|;
name|this
operator|.
name|spaceConsumed
operator|=
name|builder
operator|.
name|spaceConsumed
expr_stmt|;
name|this
operator|.
name|spaceQuota
operator|=
name|builder
operator|.
name|spaceQuota
expr_stmt|;
name|this
operator|.
name|typeConsumed
operator|=
name|builder
operator|.
name|typeConsumed
expr_stmt|;
name|this
operator|.
name|typeQuota
operator|=
name|builder
operator|.
name|typeQuota
expr_stmt|;
block|}
DECL|method|setQuota (long quota)
specifier|protected
name|void
name|setQuota
parameter_list|(
name|long
name|quota
parameter_list|)
block|{
name|this
operator|.
name|quota
operator|=
name|quota
expr_stmt|;
block|}
DECL|method|setSpaceConsumed (long spaceConsumed)
specifier|protected
name|void
name|setSpaceConsumed
parameter_list|(
name|long
name|spaceConsumed
parameter_list|)
block|{
name|this
operator|.
name|spaceConsumed
operator|=
name|spaceConsumed
expr_stmt|;
block|}
DECL|method|setSpaceQuota (long spaceQuota)
specifier|protected
name|void
name|setSpaceQuota
parameter_list|(
name|long
name|spaceQuota
parameter_list|)
block|{
name|this
operator|.
name|spaceQuota
operator|=
name|spaceQuota
expr_stmt|;
block|}
comment|/** Return the directory count. */
DECL|method|getFileAndDirectoryCount ()
specifier|public
name|long
name|getFileAndDirectoryCount
parameter_list|()
block|{
return|return
name|fileAndDirectoryCount
return|;
block|}
comment|/** Return the directory quota. */
DECL|method|getQuota ()
specifier|public
name|long
name|getQuota
parameter_list|()
block|{
return|return
name|quota
return|;
block|}
comment|/** Return (disk) space consumed. */
DECL|method|getSpaceConsumed ()
specifier|public
name|long
name|getSpaceConsumed
parameter_list|()
block|{
return|return
name|spaceConsumed
return|;
block|}
comment|/** Return (disk) space quota. */
DECL|method|getSpaceQuota ()
specifier|public
name|long
name|getSpaceQuota
parameter_list|()
block|{
return|return
name|spaceQuota
return|;
block|}
comment|/** Return storage type quota. */
DECL|method|getTypeQuota (StorageType type)
specifier|public
name|long
name|getTypeQuota
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
return|return
operator|(
name|typeQuota
operator|!=
literal|null
operator|)
condition|?
name|typeQuota
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
else|:
operator|-
literal|1L
return|;
block|}
comment|/** Return storage type consumed. */
DECL|method|getTypeConsumed (StorageType type)
specifier|public
name|long
name|getTypeConsumed
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
return|return
operator|(
name|typeConsumed
operator|!=
literal|null
operator|)
condition|?
name|typeConsumed
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
else|:
literal|0L
return|;
block|}
comment|/** Return true if any storage type quota has been set. */
DECL|method|isTypeQuotaSet ()
specifier|public
name|boolean
name|isTypeQuotaSet
parameter_list|()
block|{
if|if
condition|(
name|typeQuota
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|StorageType
name|t
range|:
name|StorageType
operator|.
name|getTypesSupportingQuota
argument_list|()
control|)
block|{
if|if
condition|(
name|typeQuota
index|[
name|t
operator|.
name|ordinal
argument_list|()
index|]
operator|>
literal|0L
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Return true if any storage type consumption information is available. */
DECL|method|isTypeConsumedAvailable ()
specifier|public
name|boolean
name|isTypeConsumedAvailable
parameter_list|()
block|{
if|if
condition|(
name|typeConsumed
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|StorageType
name|t
range|:
name|StorageType
operator|.
name|getTypesSupportingQuota
argument_list|()
control|)
block|{
if|if
condition|(
name|typeConsumed
index|[
name|t
operator|.
name|ordinal
argument_list|()
index|]
operator|>
literal|0L
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|fileAndDirectoryCount
operator|^
operator|(
name|fileAndDirectoryCount
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|quota
operator|^
operator|(
name|quota
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|spaceConsumed
operator|^
operator|(
name|spaceConsumed
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|spaceQuota
operator|^
operator|(
name|spaceQuota
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|typeConsumed
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|typeQuota
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|QuotaUsage
name|other
init|=
operator|(
name|QuotaUsage
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|fileAndDirectoryCount
operator|!=
name|other
operator|.
name|fileAndDirectoryCount
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|quota
operator|!=
name|other
operator|.
name|quota
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|spaceConsumed
operator|!=
name|other
operator|.
name|spaceConsumed
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|spaceQuota
operator|!=
name|other
operator|.
name|spaceQuota
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|typeConsumed
argument_list|,
name|other
operator|.
name|typeConsumed
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|typeQuota
argument_list|,
name|other
operator|.
name|typeQuota
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Output format:    * |----12----| |----15----| |----15----| |----15----| |-------18-------|    *    QUOTA   REMAINING_QUOTA SPACE_QUOTA SPACE_QUOTA_REM FILE_NAME    */
DECL|field|QUOTA_STRING_FORMAT
specifier|protected
specifier|static
specifier|final
name|String
name|QUOTA_STRING_FORMAT
init|=
literal|"%12s %15s "
decl_stmt|;
DECL|field|SPACE_QUOTA_STRING_FORMAT
specifier|protected
specifier|static
specifier|final
name|String
name|SPACE_QUOTA_STRING_FORMAT
init|=
literal|"%15s %15s "
decl_stmt|;
DECL|field|QUOTA_HEADER_FIELDS
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|QUOTA_HEADER_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"QUOTA"
block|,
literal|"REM_QUOTA"
block|,
literal|"SPACE_QUOTA"
block|,
literal|"REM_SPACE_QUOTA"
block|}
decl_stmt|;
DECL|field|QUOTA_HEADER
specifier|protected
specifier|static
specifier|final
name|String
name|QUOTA_HEADER
init|=
name|String
operator|.
name|format
argument_list|(
name|QUOTA_STRING_FORMAT
operator|+
name|SPACE_QUOTA_STRING_FORMAT
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|QUOTA_HEADER_FIELDS
argument_list|)
decl_stmt|;
comment|/**    * Output format:    * |----12----| |------15-----| |------15-----| |------15-----|    *        QUOTA       REM_QUOTA     SPACE_QUOTA REM_SPACE_QUOTA    * |----12----| |----12----| |-------18-------|    *    DIR_COUNT   FILE_COUNT       CONTENT_SIZE    */
DECL|field|STORAGE_TYPE_SUMMARY_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|STORAGE_TYPE_SUMMARY_FORMAT
init|=
literal|"%13s %17s "
decl_stmt|;
comment|/** Return the header of the output.    * @return the header of the output    */
DECL|method|getHeader ()
specifier|public
specifier|static
name|String
name|getHeader
parameter_list|()
block|{
return|return
name|QUOTA_HEADER
return|;
block|}
comment|/** default quota display string */
DECL|field|QUOTA_NONE
specifier|private
specifier|static
specifier|final
name|String
name|QUOTA_NONE
init|=
literal|"none"
decl_stmt|;
DECL|field|QUOTA_INF
specifier|private
specifier|static
specifier|final
name|String
name|QUOTA_INF
init|=
literal|"inf"
decl_stmt|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|method|toString (boolean hOption)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|hOption
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|hOption
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Return the string representation of the object in the output format.    * if hOption is false file sizes are returned in bytes    * if hOption is true file sizes are returned in human readable    *    * @param hOption a flag indicating if human readable output if to be used    * @return the string representation of the object    */
DECL|method|toString (boolean hOption, boolean tOption, List<StorageType> types)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|hOption
parameter_list|,
name|boolean
name|tOption
parameter_list|,
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
parameter_list|)
block|{
if|if
condition|(
name|tOption
condition|)
block|{
return|return
name|getTypesQuotaUsage
argument_list|(
name|hOption
argument_list|,
name|types
argument_list|)
return|;
block|}
return|return
name|getQuotaUsage
argument_list|(
name|hOption
argument_list|)
return|;
block|}
DECL|method|getQuotaUsage (boolean hOption)
specifier|protected
name|String
name|getQuotaUsage
parameter_list|(
name|boolean
name|hOption
parameter_list|)
block|{
name|String
name|quotaStr
init|=
name|QUOTA_NONE
decl_stmt|;
name|String
name|quotaRem
init|=
name|QUOTA_INF
decl_stmt|;
name|String
name|spaceQuotaStr
init|=
name|QUOTA_NONE
decl_stmt|;
name|String
name|spaceQuotaRem
init|=
name|QUOTA_INF
decl_stmt|;
if|if
condition|(
name|quota
operator|>
literal|0L
condition|)
block|{
name|quotaStr
operator|=
name|formatSize
argument_list|(
name|quota
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
name|quotaRem
operator|=
name|formatSize
argument_list|(
name|quota
operator|-
name|fileAndDirectoryCount
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|spaceQuota
operator|>=
literal|0L
condition|)
block|{
name|spaceQuotaStr
operator|=
name|formatSize
argument_list|(
name|spaceQuota
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
name|spaceQuotaRem
operator|=
name|formatSize
argument_list|(
name|spaceQuota
operator|-
name|spaceConsumed
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
block|}
return|return
name|String
operator|.
name|format
argument_list|(
name|QUOTA_STRING_FORMAT
operator|+
name|SPACE_QUOTA_STRING_FORMAT
argument_list|,
name|quotaStr
argument_list|,
name|quotaRem
argument_list|,
name|spaceQuotaStr
argument_list|,
name|spaceQuotaRem
argument_list|)
return|;
block|}
DECL|method|getTypesQuotaUsage (boolean hOption, List<StorageType> types)
specifier|protected
name|String
name|getTypesQuotaUsage
parameter_list|(
name|boolean
name|hOption
parameter_list|,
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
parameter_list|)
block|{
name|StringBuilder
name|content
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|types
control|)
block|{
name|long
name|typeQuota
init|=
name|getTypeQuota
argument_list|(
name|st
argument_list|)
decl_stmt|;
name|long
name|typeConsumed
init|=
name|getTypeConsumed
argument_list|(
name|st
argument_list|)
decl_stmt|;
name|String
name|quotaStr
init|=
name|QUOTA_NONE
decl_stmt|;
name|String
name|quotaRem
init|=
name|QUOTA_INF
decl_stmt|;
if|if
condition|(
name|typeQuota
operator|>=
literal|0
condition|)
block|{
name|quotaStr
operator|=
name|formatSize
argument_list|(
name|typeQuota
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
name|quotaRem
operator|=
name|formatSize
argument_list|(
name|typeQuota
operator|-
name|typeConsumed
argument_list|,
name|hOption
argument_list|)
expr_stmt|;
block|}
name|content
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|STORAGE_TYPE_SUMMARY_FORMAT
argument_list|,
name|quotaStr
argument_list|,
name|quotaRem
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * return the header of with the StorageTypes.    *    * @param storageTypes    * @return storage header string    */
DECL|method|getStorageTypeHeader (List<StorageType> storageTypes)
specifier|public
specifier|static
name|String
name|getStorageTypeHeader
parameter_list|(
name|List
argument_list|<
name|StorageType
argument_list|>
name|storageTypes
parameter_list|)
block|{
name|StringBuilder
name|header
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|storageTypes
control|)
block|{
comment|/* the field length is 13/17 for quota and remain quota        * as the max length for quota name is ARCHIVE_QUOTA         * and remain quota name REM_ARCHIVE_QUOTA */
specifier|final
name|String
name|storageName
init|=
name|st
operator|.
name|toString
argument_list|()
decl_stmt|;
name|header
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|STORAGE_TYPE_SUMMARY_FORMAT
argument_list|,
name|storageName
operator|+
literal|"_QUOTA"
argument_list|,
literal|"REM_"
operator|+
name|storageName
operator|+
literal|"_QUOTA"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|header
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Formats a size to be human readable or in bytes.    * @param size value to be formatted    * @param humanReadable flag indicating human readable or not    * @return String representation of the size   */
DECL|method|formatSize (long size, boolean humanReadable)
specifier|private
name|String
name|formatSize
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|humanReadable
parameter_list|)
block|{
return|return
name|humanReadable
condition|?
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|size
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
end_class

end_unit

