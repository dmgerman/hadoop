begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|fs
operator|.
name|StorageStatistics
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
name|java
operator|.
name|util
operator|.
name|EnumMap
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
name|Iterator
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * This storage statistics tracks how many times each DFS operation was issued.  *  * For each tracked DFS operation, there is a respective entry in the enum  * {@link OpType}. To use, increment the value the {@link DistributedFileSystem}  * and {@link org.apache.hadoop.hdfs.web.WebHdfsFileSystem}.  *  * This class is thread safe, and is generally shared by multiple threads.  */
end_comment

begin_class
DECL|class|DFSOpsCountStatistics
specifier|public
class|class
name|DFSOpsCountStatistics
extends|extends
name|StorageStatistics
block|{
comment|/** This is for counting distributed file system operations. */
DECL|enum|OpType
specifier|public
enum|enum
name|OpType
block|{
DECL|enumConstant|ADD_EC_POLICY
name|ADD_EC_POLICY
argument_list|(
literal|"op_add_ec_policy"
argument_list|)
block|,
DECL|enumConstant|ALLOW_SNAPSHOT
name|ALLOW_SNAPSHOT
argument_list|(
literal|"op_allow_snapshot"
argument_list|)
block|,
DECL|enumConstant|APPEND
name|APPEND
parameter_list|(
name|CommonStatisticNames
operator|.
name|OP_APPEND
parameter_list|)
operator|,
DECL|enumConstant|CONCAT
constructor|CONCAT("op_concat"
block|)
enum|,
DECL|enumConstant|COPY_FROM_LOCAL_FILE
name|COPY_FROM_LOCAL_FILE
parameter_list|(
name|CommonStatisticNames
operator|.
name|OP_COPY_FROM_LOCAL_FILE
parameter_list|)
operator|,
DECL|enumConstant|CREATE
constructor|CREATE(CommonStatisticNames.OP_CREATE
block|)
operator|,
DECL|enumConstant|CREATE_NON_RECURSIVE
name|CREATE_NON_RECURSIVE
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_CREATE_NON_RECURSIVE
argument_list|)
operator|,
DECL|enumConstant|CREATE_SNAPSHOT
name|CREATE_SNAPSHOT
argument_list|(
literal|"op_create_snapshot"
argument_list|)
operator|,
DECL|enumConstant|CREATE_SYM_LINK
name|CREATE_SYM_LINK
argument_list|(
literal|"op_create_symlink"
argument_list|)
operator|,
DECL|enumConstant|DELETE
name|DELETE
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_DELETE
argument_list|)
operator|,
DECL|enumConstant|DELETE_SNAPSHOT
name|DELETE_SNAPSHOT
argument_list|(
literal|"op_delete_snapshot"
argument_list|)
operator|,
DECL|enumConstant|DISABLE_EC_POLICY
name|DISABLE_EC_POLICY
argument_list|(
literal|"op_disable_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|DISALLOW_SNAPSHOT
name|DISALLOW_SNAPSHOT
argument_list|(
literal|"op_disallow_snapshot"
argument_list|)
operator|,
DECL|enumConstant|ENABLE_EC_POLICY
name|ENABLE_EC_POLICY
argument_list|(
literal|"op_enable_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|EXISTS
name|EXISTS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_EXISTS
argument_list|)
operator|,
DECL|enumConstant|GET_BYTES_WITH_FUTURE_GS
name|GET_BYTES_WITH_FUTURE_GS
argument_list|(
literal|"op_get_bytes_with_future_generation_stamps"
argument_list|)
operator|,
DECL|enumConstant|GET_CONTENT_SUMMARY
name|GET_CONTENT_SUMMARY
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_GET_CONTENT_SUMMARY
argument_list|)
operator|,
DECL|enumConstant|GET_EC_CODECS
name|GET_EC_CODECS
argument_list|(
literal|"op_get_ec_codecs"
argument_list|)
operator|,
DECL|enumConstant|GET_EC_POLICY
name|GET_EC_POLICY
argument_list|(
literal|"op_get_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|GET_EC_POLICIES
name|GET_EC_POLICIES
argument_list|(
literal|"op_get_ec_policies"
argument_list|)
operator|,
DECL|enumConstant|GET_FILE_BLOCK_LOCATIONS
name|GET_FILE_BLOCK_LOCATIONS
argument_list|(
literal|"op_get_file_block_locations"
argument_list|)
operator|,
DECL|enumConstant|GET_FILE_CHECKSUM
name|GET_FILE_CHECKSUM
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_GET_FILE_CHECKSUM
argument_list|)
operator|,
DECL|enumConstant|GET_FILE_LINK_STATUS
name|GET_FILE_LINK_STATUS
argument_list|(
literal|"op_get_file_link_status"
argument_list|)
operator|,
DECL|enumConstant|GET_FILE_STATUS
name|GET_FILE_STATUS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_GET_FILE_STATUS
argument_list|)
operator|,
DECL|enumConstant|GET_LINK_TARGET
name|GET_LINK_TARGET
argument_list|(
literal|"op_get_link_target"
argument_list|)
operator|,
DECL|enumConstant|GET_QUOTA_USAGE
name|GET_QUOTA_USAGE
argument_list|(
literal|"op_get_quota_usage"
argument_list|)
operator|,
DECL|enumConstant|GET_STATUS
name|GET_STATUS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_GET_STATUS
argument_list|)
operator|,
DECL|enumConstant|GET_STORAGE_POLICIES
name|GET_STORAGE_POLICIES
argument_list|(
literal|"op_get_storage_policies"
argument_list|)
operator|,
DECL|enumConstant|GET_STORAGE_POLICY
name|GET_STORAGE_POLICY
argument_list|(
literal|"op_get_storage_policy"
argument_list|)
operator|,
DECL|enumConstant|GET_TRASH_ROOT
name|GET_TRASH_ROOT
argument_list|(
literal|"op_get_trash_root"
argument_list|)
operator|,
DECL|enumConstant|GET_XATTR
name|GET_XATTR
argument_list|(
literal|"op_get_xattr"
argument_list|)
operator|,
DECL|enumConstant|LIST_LOCATED_STATUS
name|LIST_LOCATED_STATUS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_LIST_LOCATED_STATUS
argument_list|)
operator|,
DECL|enumConstant|LIST_STATUS
name|LIST_STATUS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_LIST_STATUS
argument_list|)
operator|,
DECL|enumConstant|MKDIRS
name|MKDIRS
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_MKDIRS
argument_list|)
operator|,
DECL|enumConstant|MODIFY_ACL_ENTRIES
name|MODIFY_ACL_ENTRIES
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_MODIFY_ACL_ENTRIES
argument_list|)
operator|,
DECL|enumConstant|OPEN
name|OPEN
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_OPEN
argument_list|)
operator|,
DECL|enumConstant|PRIMITIVE_CREATE
name|PRIMITIVE_CREATE
argument_list|(
literal|"op_primitive_create"
argument_list|)
operator|,
DECL|enumConstant|PRIMITIVE_MKDIR
name|PRIMITIVE_MKDIR
argument_list|(
literal|"op_primitive_mkdir"
argument_list|)
operator|,
DECL|enumConstant|REMOVE_ACL
name|REMOVE_ACL
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_REMOVE_ACL
argument_list|)
operator|,
DECL|enumConstant|REMOVE_ACL_ENTRIES
name|REMOVE_ACL_ENTRIES
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_REMOVE_ACL_ENTRIES
argument_list|)
operator|,
DECL|enumConstant|REMOVE_DEFAULT_ACL
name|REMOVE_DEFAULT_ACL
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_REMOVE_DEFAULT_ACL
argument_list|)
operator|,
DECL|enumConstant|REMOVE_EC_POLICY
name|REMOVE_EC_POLICY
argument_list|(
literal|"op_remove_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|REMOVE_XATTR
name|REMOVE_XATTR
argument_list|(
literal|"op_remove_xattr"
argument_list|)
operator|,
DECL|enumConstant|RENAME
name|RENAME
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_RENAME
argument_list|)
operator|,
DECL|enumConstant|RENAME_SNAPSHOT
name|RENAME_SNAPSHOT
argument_list|(
literal|"op_rename_snapshot"
argument_list|)
operator|,
DECL|enumConstant|RESOLVE_LINK
name|RESOLVE_LINK
argument_list|(
literal|"op_resolve_link"
argument_list|)
operator|,
DECL|enumConstant|SET_ACL
name|SET_ACL
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_SET_ACL
argument_list|)
operator|,
DECL|enumConstant|SET_EC_POLICY
name|SET_EC_POLICY
argument_list|(
literal|"op_set_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|SET_OWNER
name|SET_OWNER
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_SET_OWNER
argument_list|)
operator|,
DECL|enumConstant|SET_PERMISSION
name|SET_PERMISSION
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_SET_PERMISSION
argument_list|)
operator|,
DECL|enumConstant|SET_REPLICATION
name|SET_REPLICATION
argument_list|(
literal|"op_set_replication"
argument_list|)
operator|,
DECL|enumConstant|SET_STORAGE_POLICY
name|SET_STORAGE_POLICY
argument_list|(
literal|"op_set_storagePolicy"
argument_list|)
operator|,
DECL|enumConstant|SET_TIMES
name|SET_TIMES
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_SET_TIMES
argument_list|)
operator|,
DECL|enumConstant|SET_XATTR
name|SET_XATTR
argument_list|(
literal|"op_set_xattr"
argument_list|)
operator|,
DECL|enumConstant|GET_SNAPSHOT_DIFF
name|GET_SNAPSHOT_DIFF
argument_list|(
literal|"op_get_snapshot_diff"
argument_list|)
operator|,
DECL|enumConstant|GET_SNAPSHOTTABLE_DIRECTORY_LIST
name|GET_SNAPSHOTTABLE_DIRECTORY_LIST
argument_list|(
literal|"op_get_snapshottable_directory_list"
argument_list|)
operator|,
DECL|enumConstant|TRUNCATE
name|TRUNCATE
argument_list|(
name|CommonStatisticNames
operator|.
name|OP_TRUNCATE
argument_list|)
operator|,
DECL|enumConstant|UNSET_EC_POLICY
name|UNSET_EC_POLICY
argument_list|(
literal|"op_unset_ec_policy"
argument_list|)
operator|,
DECL|enumConstant|UNSET_STORAGE_POLICY
name|UNSET_STORAGE_POLICY
argument_list|(
literal|"op_unset_storage_policy"
argument_list|)
expr_stmt|;
end_class

begin_decl_stmt
DECL|field|SYMBOL_MAP
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OpType
argument_list|>
name|SYMBOL_MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|OpType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
for|for
control|(
name|OpType
name|opType
range|:
name|values
argument_list|()
control|)
block|{
name|SYMBOL_MAP
operator|.
name|put
argument_list|(
name|opType
operator|.
name|getSymbol
argument_list|()
argument_list|,
name|opType
argument_list|)
expr_stmt|;
block|}
block|}
end_static

begin_decl_stmt
DECL|field|symbol
specifier|private
specifier|final
name|String
name|symbol
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|OpType (String symbol)
name|OpType
argument_list|(
name|String
name|symbol
argument_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
block|;     }
DECL|method|getSymbol ()
specifier|public
name|String
name|getSymbol
argument_list|()
block|{
return|return
name|symbol
return|;
block|}
end_expr_stmt

begin_function
DECL|method|fromSymbol (String symbol)
specifier|public
specifier|static
name|OpType
name|fromSymbol
parameter_list|(
name|String
name|symbol
parameter_list|)
block|{
return|return
name|SYMBOL_MAP
operator|.
name|get
argument_list|(
name|symbol
argument_list|)
return|;
block|}
end_function

begin_decl_stmt
unit|}    public
DECL|field|NAME
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"DFSOpsCountStatistics"
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|opsCount
specifier|private
specifier|final
name|Map
argument_list|<
name|OpType
argument_list|,
name|AtomicLong
argument_list|>
name|opsCount
init|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|OpType
operator|.
name|class
argument_list|)
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|DFSOpsCountStatistics ()
specifier|public
name|DFSOpsCountStatistics
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
for|for
control|(
name|OpType
name|opType
range|:
name|OpType
operator|.
name|values
argument_list|()
control|)
block|{
name|opsCount
operator|.
name|put
argument_list|(
name|opType
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_constructor

begin_function
DECL|method|incrementOpCounter (OpType op)
specifier|public
name|void
name|incrementOpCounter
parameter_list|(
name|OpType
name|op
parameter_list|)
block|{
name|opsCount
operator|.
name|get
argument_list|(
name|op
argument_list|)
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
end_function

begin_class
DECL|class|LongIterator
specifier|private
class|class
name|LongIterator
implements|implements
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|OpType
argument_list|,
name|AtomicLong
argument_list|>
argument_list|>
name|iterator
init|=
name|opsCount
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|LongStatistic
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
specifier|final
name|Entry
argument_list|<
name|OpType
argument_list|,
name|AtomicLong
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|LongStatistic
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getSymbol
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

begin_function
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getLongStatistics ()
specifier|public
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
name|getLongStatistics
parameter_list|()
block|{
return|return
operator|new
name|LongIterator
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getLong (String key)
specifier|public
name|Long
name|getLong
parameter_list|(
name|String
name|key
parameter_list|)
block|{
specifier|final
name|OpType
name|type
init|=
name|OpType
operator|.
name|fromSymbol
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|type
operator|==
literal|null
condition|?
literal|null
else|:
name|opsCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|isTracked (String key)
specifier|public
name|boolean
name|isTracked
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|OpType
operator|.
name|fromSymbol
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|AtomicLong
name|count
range|:
name|opsCount
operator|.
name|values
argument_list|()
control|)
block|{
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

