begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * An implementation of ImageVisitor can traverse the structure of an  * Hadoop fsimage and respond to each of the structures within the file.  */
end_comment

begin_class
DECL|class|ImageVisitor
specifier|abstract
class|class
name|ImageVisitor
block|{
comment|/**    * Structural elements of an FSImage that may be encountered within the    * file. ImageVisitors are able to handle processing any of these elements.    */
DECL|enum|ImageElement
specifier|public
enum|enum
name|ImageElement
block|{
DECL|enumConstant|FS_IMAGE
name|FS_IMAGE
block|,
DECL|enumConstant|IMAGE_VERSION
name|IMAGE_VERSION
block|,
DECL|enumConstant|NAMESPACE_ID
name|NAMESPACE_ID
block|,
DECL|enumConstant|IS_COMPRESSED
name|IS_COMPRESSED
block|,
DECL|enumConstant|COMPRESS_CODEC
name|COMPRESS_CODEC
block|,
DECL|enumConstant|LAYOUT_VERSION
name|LAYOUT_VERSION
block|,
DECL|enumConstant|NUM_INODES
name|NUM_INODES
block|,
DECL|enumConstant|GENERATION_STAMP
name|GENERATION_STAMP
block|,
DECL|enumConstant|GENERATION_STAMP_V2
name|GENERATION_STAMP_V2
block|,
DECL|enumConstant|GENERATION_STAMP_V1_LIMIT
name|GENERATION_STAMP_V1_LIMIT
block|,
DECL|enumConstant|LAST_ALLOCATED_BLOCK_ID
name|LAST_ALLOCATED_BLOCK_ID
block|,
DECL|enumConstant|INODES
name|INODES
block|,
DECL|enumConstant|INODE
name|INODE
block|,
DECL|enumConstant|INODE_PATH
name|INODE_PATH
block|,
DECL|enumConstant|REPLICATION
name|REPLICATION
block|,
DECL|enumConstant|MODIFICATION_TIME
name|MODIFICATION_TIME
block|,
DECL|enumConstant|ACCESS_TIME
name|ACCESS_TIME
block|,
DECL|enumConstant|BLOCK_SIZE
name|BLOCK_SIZE
block|,
DECL|enumConstant|NUM_BLOCKS
name|NUM_BLOCKS
block|,
DECL|enumConstant|BLOCKS
name|BLOCKS
block|,
DECL|enumConstant|BLOCK
name|BLOCK
block|,
DECL|enumConstant|BLOCK_ID
name|BLOCK_ID
block|,
DECL|enumConstant|NUM_BYTES
name|NUM_BYTES
block|,
DECL|enumConstant|NS_QUOTA
name|NS_QUOTA
block|,
DECL|enumConstant|DS_QUOTA
name|DS_QUOTA
block|,
DECL|enumConstant|PERMISSIONS
name|PERMISSIONS
block|,
DECL|enumConstant|SYMLINK
name|SYMLINK
block|,
DECL|enumConstant|NUM_INODES_UNDER_CONSTRUCTION
name|NUM_INODES_UNDER_CONSTRUCTION
block|,
DECL|enumConstant|INODES_UNDER_CONSTRUCTION
name|INODES_UNDER_CONSTRUCTION
block|,
DECL|enumConstant|INODE_UNDER_CONSTRUCTION
name|INODE_UNDER_CONSTRUCTION
block|,
DECL|enumConstant|PREFERRED_BLOCK_SIZE
name|PREFERRED_BLOCK_SIZE
block|,
DECL|enumConstant|CLIENT_NAME
name|CLIENT_NAME
block|,
DECL|enumConstant|CLIENT_MACHINE
name|CLIENT_MACHINE
block|,
DECL|enumConstant|USER_NAME
name|USER_NAME
block|,
DECL|enumConstant|GROUP_NAME
name|GROUP_NAME
block|,
DECL|enumConstant|PERMISSION_STRING
name|PERMISSION_STRING
block|,
DECL|enumConstant|CURRENT_DELEGATION_KEY_ID
name|CURRENT_DELEGATION_KEY_ID
block|,
DECL|enumConstant|NUM_DELEGATION_KEYS
name|NUM_DELEGATION_KEYS
block|,
DECL|enumConstant|DELEGATION_KEYS
name|DELEGATION_KEYS
block|,
DECL|enumConstant|DELEGATION_KEY
name|DELEGATION_KEY
block|,
DECL|enumConstant|DELEGATION_TOKEN_SEQUENCE_NUMBER
name|DELEGATION_TOKEN_SEQUENCE_NUMBER
block|,
DECL|enumConstant|NUM_DELEGATION_TOKENS
name|NUM_DELEGATION_TOKENS
block|,
DECL|enumConstant|DELEGATION_TOKENS
name|DELEGATION_TOKENS
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER
name|DELEGATION_TOKEN_IDENTIFIER
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_KIND
name|DELEGATION_TOKEN_IDENTIFIER_KIND
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_SEQNO
name|DELEGATION_TOKEN_IDENTIFIER_SEQNO
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_OWNER
name|DELEGATION_TOKEN_IDENTIFIER_OWNER
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_RENEWER
name|DELEGATION_TOKEN_IDENTIFIER_RENEWER
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_REALUSER
name|DELEGATION_TOKEN_IDENTIFIER_REALUSER
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_ISSUE_DATE
name|DELEGATION_TOKEN_IDENTIFIER_ISSUE_DATE
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_MAX_DATE
name|DELEGATION_TOKEN_IDENTIFIER_MAX_DATE
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_EXPIRY_TIME
name|DELEGATION_TOKEN_IDENTIFIER_EXPIRY_TIME
block|,
DECL|enumConstant|DELEGATION_TOKEN_IDENTIFIER_MASTER_KEY_ID
name|DELEGATION_TOKEN_IDENTIFIER_MASTER_KEY_ID
block|,
DECL|enumConstant|TRANSACTION_ID
name|TRANSACTION_ID
block|,
DECL|enumConstant|LAST_INODE_ID
name|LAST_INODE_ID
block|,
DECL|enumConstant|INODE_ID
name|INODE_ID
block|,
DECL|enumConstant|SNAPSHOT_COUNTER
name|SNAPSHOT_COUNTER
block|,
DECL|enumConstant|NUM_SNAPSHOTS_TOTAL
name|NUM_SNAPSHOTS_TOTAL
block|,
DECL|enumConstant|NUM_SNAPSHOTS
name|NUM_SNAPSHOTS
block|,
DECL|enumConstant|SNAPSHOTS
name|SNAPSHOTS
block|,
DECL|enumConstant|SNAPSHOT
name|SNAPSHOT
block|,
DECL|enumConstant|SNAPSHOT_ID
name|SNAPSHOT_ID
block|,
DECL|enumConstant|SNAPSHOT_ROOT
name|SNAPSHOT_ROOT
block|,
DECL|enumConstant|SNAPSHOT_QUOTA
name|SNAPSHOT_QUOTA
block|,
DECL|enumConstant|NUM_SNAPSHOT_DIR_DIFF
name|NUM_SNAPSHOT_DIR_DIFF
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFFS
name|SNAPSHOT_DIR_DIFFS
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF
name|SNAPSHOT_DIR_DIFF
block|,
DECL|enumConstant|SNAPSHOT_DIFF_SNAPSHOTID
name|SNAPSHOT_DIFF_SNAPSHOTID
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_CHILDREN_SIZE
name|SNAPSHOT_DIR_DIFF_CHILDREN_SIZE
block|,
DECL|enumConstant|SNAPSHOT_INODE_FILE_ATTRIBUTES
name|SNAPSHOT_INODE_FILE_ATTRIBUTES
block|,
DECL|enumConstant|SNAPSHOT_INODE_DIRECTORY_ATTRIBUTES
name|SNAPSHOT_INODE_DIRECTORY_ATTRIBUTES
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_CREATEDLIST
name|SNAPSHOT_DIR_DIFF_CREATEDLIST
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_CREATEDLIST_SIZE
name|SNAPSHOT_DIR_DIFF_CREATEDLIST_SIZE
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_CREATED_INODE
name|SNAPSHOT_DIR_DIFF_CREATED_INODE
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_DELETEDLIST
name|SNAPSHOT_DIR_DIFF_DELETEDLIST
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_DELETEDLIST_SIZE
name|SNAPSHOT_DIR_DIFF_DELETEDLIST_SIZE
block|,
DECL|enumConstant|SNAPSHOT_DIR_DIFF_DELETED_INODE
name|SNAPSHOT_DIR_DIFF_DELETED_INODE
block|,
DECL|enumConstant|IS_SNAPSHOTTABLE_DIR
name|IS_SNAPSHOTTABLE_DIR
block|,
DECL|enumConstant|IS_WITHSNAPSHOT_DIR
name|IS_WITHSNAPSHOT_DIR
block|,
DECL|enumConstant|SNAPSHOT_FILE_DIFFS
name|SNAPSHOT_FILE_DIFFS
block|,
DECL|enumConstant|SNAPSHOT_FILE_DIFF
name|SNAPSHOT_FILE_DIFF
block|,
DECL|enumConstant|NUM_SNAPSHOT_FILE_DIFF
name|NUM_SNAPSHOT_FILE_DIFF
block|,
DECL|enumConstant|SNAPSHOT_FILE_SIZE
name|SNAPSHOT_FILE_SIZE
block|,
DECL|enumConstant|SNAPSHOT_DST_SNAPSHOT_ID
name|SNAPSHOT_DST_SNAPSHOT_ID
block|,
DECL|enumConstant|SNAPSHOT_LAST_SNAPSHOT_ID
name|SNAPSHOT_LAST_SNAPSHOT_ID
block|,
DECL|enumConstant|SNAPSHOT_REF_INODE_ID
name|SNAPSHOT_REF_INODE_ID
block|,
DECL|enumConstant|SNAPSHOT_REF_INODE
name|SNAPSHOT_REF_INODE
block|,
DECL|enumConstant|CACHE_NEXT_ENTRY_ID
name|CACHE_NEXT_ENTRY_ID
block|,
DECL|enumConstant|CACHE_NUM_POOLS
name|CACHE_NUM_POOLS
block|,
DECL|enumConstant|CACHE_POOL_NAME
name|CACHE_POOL_NAME
block|,
DECL|enumConstant|CACHE_POOL_OWNER_NAME
name|CACHE_POOL_OWNER_NAME
block|,
DECL|enumConstant|CACHE_POOL_GROUP_NAME
name|CACHE_POOL_GROUP_NAME
block|,
DECL|enumConstant|CACHE_POOL_PERMISSION_STRING
name|CACHE_POOL_PERMISSION_STRING
block|,
DECL|enumConstant|CACHE_POOL_WEIGHT
name|CACHE_POOL_WEIGHT
block|,
DECL|enumConstant|CACHE_NUM_ENTRIES
name|CACHE_NUM_ENTRIES
block|,
DECL|enumConstant|CACHE_ENTRY_PATH
name|CACHE_ENTRY_PATH
block|,
DECL|enumConstant|CACHE_ENTRY_REPLICATION
name|CACHE_ENTRY_REPLICATION
block|,
DECL|enumConstant|CACHE_ENTRY_POOL_NAME
name|CACHE_ENTRY_POOL_NAME
block|}
comment|/**    * Begin visiting the fsimage structure.  Opportunity to perform    * any initialization necessary for the implementing visitor.    */
DECL|method|start ()
specifier|abstract
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Finish visiting the fsimage structure.  Opportunity to perform any    * clean up necessary for the implementing visitor.    */
DECL|method|finish ()
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Finish visiting the fsimage structure after an error has occurred    * during the processing.  Opportunity to perform any clean up necessary    * for the implementing visitor.    */
DECL|method|finishAbnormally ()
specifier|abstract
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Visit non enclosing element of fsimage with specified value.    *    * @param element FSImage element    * @param value Element's value    */
DECL|method|visit (ImageElement element, String value)
specifier|abstract
name|void
name|visit
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// Convenience methods to automatically convert numeric value types to strings
DECL|method|visit (ImageElement element, int value)
name|void
name|visit
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visit
argument_list|(
name|element
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|visit (ImageElement element, long value)
name|void
name|visit
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visit
argument_list|(
name|element
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Begin visiting an element that encloses another element, such as    * the beginning of the list of blocks that comprise a file.    *    * @param element Element being visited    */
DECL|method|visitEnclosingElement (ImageElement element)
specifier|abstract
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Begin visiting an element that encloses another element, such as    * the beginning of the list of blocks that comprise a file.    *    * Also provide an additional key and value for the element, such as the    * number items within the element.    *    * @param element Element being visited    * @param key Key describing the element being visited    * @param value Value associated with element being visited    */
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, String value)
specifier|abstract
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|ImageElement
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// Convenience methods to automatically convert value types to strings
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, int value)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|ImageElement
name|key
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitEnclosingElement
argument_list|(
name|element
argument_list|,
name|key
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, long value)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|ImageElement
name|key
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitEnclosingElement
argument_list|(
name|element
argument_list|,
name|key
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Leave current enclosing element.  Called, for instance, at the end of    * processing the blocks that compromise a file.    */
DECL|method|leaveEnclosingElement ()
specifier|abstract
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

