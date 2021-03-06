begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|annotations
operator|.
name|VisibleForTesting
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
name|fs
operator|.
name|StorageType
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
name|protocol
operator|.
name|HdfsConstants
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
name|util
operator|.
name|ConstEnumCounters
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
name|util
operator|.
name|EnumCounters
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
name|util
operator|.
name|ConstEnumCounters
operator|.
name|ConstEnumException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Counters for namespace, storage space and storage type space quota and usage.  */
end_comment

begin_class
DECL|class|QuotaCounts
specifier|public
class|class
name|QuotaCounts
block|{
comment|/**    * We pre-define 4 most common used EnumCounters objects. When the nsSsCounts    * and tsCounts are set to the 4 most common used value, we just point them to    * the pre-defined const EnumCounters objects instead of constructing many    * objects with the same value. See HDFS-14547.    */
DECL|field|QUOTA_RESET
specifier|final
specifier|static
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|QUOTA_RESET
init|=
operator|new
name|ConstEnumCounters
argument_list|<>
argument_list|(
name|Quota
operator|.
name|class
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|)
decl_stmt|;
DECL|field|QUOTA_DEFAULT
specifier|final
specifier|static
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|QUOTA_DEFAULT
init|=
operator|new
name|ConstEnumCounters
argument_list|<>
argument_list|(
name|Quota
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|STORAGE_TYPE_RESET
specifier|final
specifier|static
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|STORAGE_TYPE_RESET
init|=
operator|new
name|ConstEnumCounters
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|)
decl_stmt|;
DECL|field|STORAGE_TYPE_DEFAULT
specifier|final
specifier|static
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|STORAGE_TYPE_DEFAULT
init|=
operator|new
name|ConstEnumCounters
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Modify counter with action. If the counter is ConstEnumCounters, copy all    * the values of it to a new EnumCounters object, and modify the new obj.    *    * @param counter the EnumCounters to be modified.    * @param action the modifying action on counter.    * @return the modified counter.    */
DECL|method|modify (EnumCounters<T> counter, Consumer<EnumCounters<T>> action)
specifier|static
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|T
argument_list|>
parameter_list|>
name|EnumCounters
argument_list|<
name|T
argument_list|>
name|modify
parameter_list|(
name|EnumCounters
argument_list|<
name|T
argument_list|>
name|counter
parameter_list|,
name|Consumer
argument_list|<
name|EnumCounters
argument_list|<
name|T
argument_list|>
argument_list|>
name|action
parameter_list|)
block|{
try|try
block|{
name|action
operator|.
name|accept
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstEnumException
name|cee
parameter_list|)
block|{
comment|// We don't call clone here because ConstEnumCounters.clone() will return
comment|// an object of class ConstEnumCounters. We want EnumCounters.
name|counter
operator|=
name|counter
operator|.
name|deepCopyEnumCounter
argument_list|()
expr_stmt|;
name|action
operator|.
name|accept
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
return|return
name|counter
return|;
block|}
comment|// Name space and storage space counts (HDFS-7775 refactors the original disk
comment|// space count to storage space counts)
annotation|@
name|VisibleForTesting
DECL|field|nsSsCounts
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|nsSsCounts
decl_stmt|;
comment|// Storage type space counts
annotation|@
name|VisibleForTesting
DECL|field|tsCounts
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|tsCounts
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|nsSsCounts
specifier|private
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|nsSsCounts
decl_stmt|;
DECL|field|tsCounts
specifier|private
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|tsCounts
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{
name|this
operator|.
name|nsSsCounts
operator|=
name|QUOTA_DEFAULT
expr_stmt|;
name|this
operator|.
name|tsCounts
operator|=
name|STORAGE_TYPE_DEFAULT
expr_stmt|;
block|}
DECL|method|nameSpace (long val)
specifier|public
name|Builder
name|nameSpace
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|setQuotaCounter
argument_list|(
name|nsSsCounts
argument_list|,
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|Quota
operator|.
name|STORAGESPACE
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|storageSpace (long val)
specifier|public
name|Builder
name|storageSpace
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|setQuotaCounter
argument_list|(
name|nsSsCounts
argument_list|,
name|Quota
operator|.
name|STORAGESPACE
argument_list|,
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeSpaces (EnumCounters<StorageType> val)
specifier|public
name|Builder
name|typeSpaces
parameter_list|(
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|val
operator|==
name|STORAGE_TYPE_DEFAULT
operator|||
name|val
operator|==
name|STORAGE_TYPE_RESET
condition|)
block|{
name|tsCounts
operator|=
name|val
expr_stmt|;
block|}
else|else
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
DECL|method|typeSpaces (long val)
specifier|public
name|Builder
name|typeSpaces
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
name|tsCounts
operator|=
name|STORAGE_TYPE_RESET
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|==
literal|0
condition|)
block|{
name|tsCounts
operator|=
name|STORAGE_TYPE_DEFAULT
expr_stmt|;
block|}
else|else
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|reset
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|quotaCount (QuotaCounts that)
specifier|public
name|Builder
name|quotaCount
parameter_list|(
name|QuotaCounts
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|.
name|nsSsCounts
operator|==
name|QUOTA_DEFAULT
operator|||
name|that
operator|.
name|nsSsCounts
operator|==
name|QUOTA_RESET
condition|)
block|{
name|nsSsCounts
operator|=
name|that
operator|.
name|nsSsCounts
expr_stmt|;
block|}
else|else
block|{
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|that
operator|.
name|nsSsCounts
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|that
operator|.
name|tsCounts
operator|==
name|STORAGE_TYPE_DEFAULT
operator|||
name|that
operator|.
name|tsCounts
operator|==
name|STORAGE_TYPE_RESET
condition|)
block|{
name|tsCounts
operator|=
name|that
operator|.
name|tsCounts
expr_stmt|;
block|}
else|else
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|that
operator|.
name|tsCounts
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|QuotaCounts
name|build
parameter_list|()
block|{
return|return
operator|new
name|QuotaCounts
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|QuotaCounts (Builder builder)
specifier|private
name|QuotaCounts
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|nsSsCounts
operator|=
name|builder
operator|.
name|nsSsCounts
expr_stmt|;
name|this
operator|.
name|tsCounts
operator|=
name|builder
operator|.
name|tsCounts
expr_stmt|;
block|}
DECL|method|add (QuotaCounts that)
specifier|public
name|QuotaCounts
name|add
parameter_list|(
name|QuotaCounts
name|that
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|add
argument_list|(
name|that
operator|.
name|nsSsCounts
argument_list|)
argument_list|)
expr_stmt|;
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|add
argument_list|(
name|that
operator|.
name|tsCounts
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|subtract (QuotaCounts that)
specifier|public
name|QuotaCounts
name|subtract
parameter_list|(
name|QuotaCounts
name|that
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|subtract
argument_list|(
name|that
operator|.
name|nsSsCounts
argument_list|)
argument_list|)
expr_stmt|;
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|subtract
argument_list|(
name|that
operator|.
name|tsCounts
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns a QuotaCounts whose value is {@code (-this)}.    *    * @return {@code -this}    */
DECL|method|negation ()
specifier|public
name|QuotaCounts
name|negation
parameter_list|()
block|{
name|QuotaCounts
name|ret
init|=
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|quotaCount
argument_list|(
name|this
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ret
operator|.
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|ret
operator|.
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|negation
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|tsCounts
operator|=
name|modify
argument_list|(
name|ret
operator|.
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|negation
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|getNameSpace ()
specifier|public
name|long
name|getNameSpace
parameter_list|()
block|{
return|return
name|nsSsCounts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|)
return|;
block|}
DECL|method|setNameSpace (long nameSpaceCount)
specifier|public
name|void
name|setNameSpace
parameter_list|(
name|long
name|nameSpaceCount
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|setQuotaCounter
argument_list|(
name|nsSsCounts
argument_list|,
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|Quota
operator|.
name|STORAGESPACE
argument_list|,
name|nameSpaceCount
argument_list|)
expr_stmt|;
block|}
DECL|method|addNameSpace (long nsDelta)
specifier|public
name|void
name|addNameSpace
parameter_list|(
name|long
name|nsDelta
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|nsDelta
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getStorageSpace ()
specifier|public
name|long
name|getStorageSpace
parameter_list|()
block|{
return|return
name|nsSsCounts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|STORAGESPACE
argument_list|)
return|;
block|}
DECL|method|setStorageSpace (long spaceCount)
specifier|public
name|void
name|setStorageSpace
parameter_list|(
name|long
name|spaceCount
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|setQuotaCounter
argument_list|(
name|nsSsCounts
argument_list|,
name|Quota
operator|.
name|STORAGESPACE
argument_list|,
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|spaceCount
argument_list|)
expr_stmt|;
block|}
DECL|method|addStorageSpace (long dsDelta)
specifier|public
name|void
name|addStorageSpace
parameter_list|(
name|long
name|dsDelta
parameter_list|)
block|{
name|nsSsCounts
operator|=
name|modify
argument_list|(
name|nsSsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|STORAGESPACE
argument_list|,
name|dsDelta
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeSpaces ()
specifier|public
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|getTypeSpaces
parameter_list|()
block|{
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|ret
init|=
operator|new
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
decl_stmt|;
name|ret
operator|.
name|set
argument_list|(
name|tsCounts
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|setTypeSpaces (EnumCounters<StorageType> that)
name|void
name|setTypeSpaces
parameter_list|(
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|==
name|STORAGE_TYPE_DEFAULT
operator|||
name|that
operator|==
name|STORAGE_TYPE_RESET
condition|)
block|{
name|tsCounts
operator|=
name|that
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|that
operator|!=
literal|null
condition|)
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|that
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTypeSpace (StorageType type)
name|long
name|getTypeSpace
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
return|return
name|this
operator|.
name|tsCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|setTypeSpace (StorageType type, long spaceCount)
name|void
name|setTypeSpace
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|spaceCount
parameter_list|)
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|type
argument_list|,
name|spaceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addTypeSpace (StorageType type, long delta)
specifier|public
name|void
name|addTypeSpace
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|delta
parameter_list|)
block|{
name|tsCounts
operator|=
name|modify
argument_list|(
name|tsCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|add
argument_list|(
name|type
argument_list|,
name|delta
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|anyNsSsCountGreaterOrEqual (long val)
specifier|public
name|boolean
name|anyNsSsCountGreaterOrEqual
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|nsSsCounts
operator|==
name|QUOTA_DEFAULT
condition|)
block|{
return|return
name|val
operator|<=
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|nsSsCounts
operator|==
name|QUOTA_RESET
condition|)
block|{
return|return
name|val
operator|<=
name|HdfsConstants
operator|.
name|QUOTA_RESET
return|;
block|}
return|return
name|nsSsCounts
operator|.
name|anyGreaterOrEqual
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|anyTypeSpaceCountGreaterOrEqual (long val)
specifier|public
name|boolean
name|anyTypeSpaceCountGreaterOrEqual
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|tsCounts
operator|==
name|STORAGE_TYPE_DEFAULT
condition|)
block|{
return|return
name|val
operator|<=
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|tsCounts
operator|==
name|STORAGE_TYPE_RESET
condition|)
block|{
return|return
name|val
operator|<=
name|HdfsConstants
operator|.
name|QUOTA_RESET
return|;
block|}
return|return
name|tsCounts
operator|.
name|anyGreaterOrEqual
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * Set inputCounts' value of Quota type quotaToSet to val.    * inputCounts should be the left side value of this method.    *    * @param inputCounts the EnumCounters instance.    * @param quotaToSet the quota type to be set.    * @param otherQuota the other quota type besides quotaToSet.    * @param val the value to be set.    * @return the modified inputCounts.    */
DECL|method|setQuotaCounter ( EnumCounters<Quota> inputCounts, Quota quotaToSet, Quota otherQuota, long val)
specifier|private
specifier|static
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|setQuotaCounter
parameter_list|(
name|EnumCounters
argument_list|<
name|Quota
argument_list|>
name|inputCounts
parameter_list|,
name|Quota
name|quotaToSet
parameter_list|,
name|Quota
name|otherQuota
parameter_list|,
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
name|HdfsConstants
operator|.
name|QUOTA_RESET
operator|&&
name|inputCounts
operator|.
name|get
argument_list|(
name|otherQuota
argument_list|)
operator|==
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
return|return
name|QUOTA_RESET
return|;
block|}
elseif|else
if|if
condition|(
name|val
operator|==
literal|0
operator|&&
name|inputCounts
operator|.
name|get
argument_list|(
name|otherQuota
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|QUOTA_DEFAULT
return|;
block|}
else|else
block|{
return|return
name|modify
argument_list|(
name|inputCounts
argument_list|,
name|ec
lambda|->
name|ec
operator|.
name|set
argument_list|(
name|quotaToSet
argument_list|,
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"name space="
operator|+
name|getNameSpace
argument_list|()
operator|+
literal|"\nstorage space="
operator|+
name|getStorageSpace
argument_list|()
operator|+
literal|"\nstorage types="
operator|+
name|getTypeSpaces
argument_list|()
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
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|QuotaCounts
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|QuotaCounts
name|that
init|=
operator|(
name|QuotaCounts
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|nsSsCounts
operator|.
name|equals
argument_list|(
name|that
operator|.
name|nsSsCounts
argument_list|)
operator|&&
name|this
operator|.
name|tsCounts
operator|.
name|equals
argument_list|(
name|that
operator|.
name|tsCounts
argument_list|)
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
assert|assert
literal|false
operator|:
literal|"hashCode not designed"
assert|;
return|return
literal|42
return|;
comment|// any arbitrary constant will do
block|}
block|}
end_class

end_unit

