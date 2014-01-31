begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_comment
comment|/**  * This class tracks changes in the layout version of HDFS.  *   * Layout version is changed for following reasons:  *<ol>  *<li>The layout of how namenode or datanode stores information   * on disk changes.</li>  *<li>A new operation code is added to the editlog.</li>  *<li>Modification such as format of a record, content of a record   * in editlog or fsimage.</li>  *</ol>  *<br>  *<b>How to update layout version:<br></b>  * When a change requires new layout version, please add an entry into  * {@link Feature} with a short enum name, new layout version and description  * of the change. Please see {@link Feature} for further details.  *<br>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LayoutVersion
specifier|public
class|class
name|LayoutVersion
block|{
comment|/**    * Version in which HDFS-2991 was fixed. This bug caused OP_ADD to    * sometimes be skipped for append() calls. If we see such a case when    * loading the edits, but the version is known to have that bug, we    * workaround the issue. Otherwise we should consider it a corruption    * and bail.    */
DECL|field|BUGFIX_HDFS_2991_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|BUGFIX_HDFS_2991_VERSION
init|=
operator|-
literal|40
decl_stmt|;
comment|/**    * The interface to be implemented by NameNode and DataNode layout features     */
DECL|interface|LayoutFeature
specifier|public
interface|interface
name|LayoutFeature
block|{
DECL|method|getInfo ()
specifier|public
name|FeatureInfo
name|getInfo
parameter_list|()
function_decl|;
block|}
comment|/**    * Enums for features that change the layout version before rolling    * upgrade is supported.    *<br><br>    * To add a new layout version:    *<ul>    *<li>Define a new enum constant with a short enum name, the new layout version     * and description of the added feature.</li>    *<li>When adding a layout version with an ancestor that is not same as    * its immediate predecessor, use the constructor where a specific ancestor    * can be passed.    *</li>    *</ul>    */
DECL|enum|Feature
specifier|public
specifier|static
enum|enum
name|Feature
implements|implements
name|LayoutFeature
block|{
DECL|enumConstant|NAMESPACE_QUOTA
name|NAMESPACE_QUOTA
argument_list|(
operator|-
literal|16
argument_list|,
literal|"Support for namespace quotas"
argument_list|)
block|,
DECL|enumConstant|FILE_ACCESS_TIME
name|FILE_ACCESS_TIME
argument_list|(
operator|-
literal|17
argument_list|,
literal|"Support for access time on files"
argument_list|)
block|,
DECL|enumConstant|DISKSPACE_QUOTA
name|DISKSPACE_QUOTA
argument_list|(
operator|-
literal|18
argument_list|,
literal|"Support for disk space quotas"
argument_list|)
block|,
DECL|enumConstant|STICKY_BIT
name|STICKY_BIT
argument_list|(
operator|-
literal|19
argument_list|,
literal|"Support for sticky bits"
argument_list|)
block|,
DECL|enumConstant|APPEND_RBW_DIR
name|APPEND_RBW_DIR
argument_list|(
operator|-
literal|20
argument_list|,
literal|"Datanode has \"rbw\" subdirectory for append"
argument_list|)
block|,
DECL|enumConstant|ATOMIC_RENAME
name|ATOMIC_RENAME
argument_list|(
operator|-
literal|21
argument_list|,
literal|"Support for atomic rename"
argument_list|)
block|,
DECL|enumConstant|CONCAT
name|CONCAT
argument_list|(
operator|-
literal|22
argument_list|,
literal|"Support for concat operation"
argument_list|)
block|,
DECL|enumConstant|SYMLINKS
name|SYMLINKS
argument_list|(
operator|-
literal|23
argument_list|,
literal|"Support for symbolic links"
argument_list|)
block|,
DECL|enumConstant|DELEGATION_TOKEN
name|DELEGATION_TOKEN
argument_list|(
operator|-
literal|24
argument_list|,
literal|"Support for delegation tokens for security"
argument_list|)
block|,
DECL|enumConstant|FSIMAGE_COMPRESSION
name|FSIMAGE_COMPRESSION
argument_list|(
operator|-
literal|25
argument_list|,
literal|"Support for fsimage compression"
argument_list|)
block|,
DECL|enumConstant|FSIMAGE_CHECKSUM
name|FSIMAGE_CHECKSUM
argument_list|(
operator|-
literal|26
argument_list|,
literal|"Support checksum for fsimage"
argument_list|)
block|,
DECL|enumConstant|REMOVE_REL13_DISK_LAYOUT_SUPPORT
name|REMOVE_REL13_DISK_LAYOUT_SUPPORT
argument_list|(
operator|-
literal|27
argument_list|,
literal|"Remove support for 0.13 disk layout"
argument_list|)
block|,
DECL|enumConstant|EDITS_CHESKUM
name|EDITS_CHESKUM
argument_list|(
operator|-
literal|28
argument_list|,
literal|"Support checksum for editlog"
argument_list|)
block|,
DECL|enumConstant|UNUSED
name|UNUSED
argument_list|(
operator|-
literal|29
argument_list|,
literal|"Skipped version"
argument_list|)
block|,
DECL|enumConstant|FSIMAGE_NAME_OPTIMIZATION
name|FSIMAGE_NAME_OPTIMIZATION
argument_list|(
operator|-
literal|30
argument_list|,
literal|"Store only last part of path in fsimage"
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL20_203
name|RESERVED_REL20_203
argument_list|(
operator|-
literal|31
argument_list|,
operator|-
literal|19
argument_list|,
literal|"Reserved for release 0.20.203"
argument_list|,
literal|true
argument_list|,
name|DELEGATION_TOKEN
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL20_204
name|RESERVED_REL20_204
argument_list|(
operator|-
literal|32
argument_list|,
operator|-
literal|31
argument_list|,
literal|"Reserved for release 0.20.204"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL22
name|RESERVED_REL22
argument_list|(
operator|-
literal|33
argument_list|,
operator|-
literal|27
argument_list|,
literal|"Reserved for release 0.22"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL23
name|RESERVED_REL23
argument_list|(
operator|-
literal|34
argument_list|,
operator|-
literal|30
argument_list|,
literal|"Reserved for release 0.23"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|FEDERATION
name|FEDERATION
argument_list|(
operator|-
literal|35
argument_list|,
literal|"Support for namenode federation"
argument_list|)
block|,
DECL|enumConstant|LEASE_REASSIGNMENT
name|LEASE_REASSIGNMENT
argument_list|(
operator|-
literal|36
argument_list|,
literal|"Support for persisting lease holder reassignment"
argument_list|)
block|,
DECL|enumConstant|STORED_TXIDS
name|STORED_TXIDS
argument_list|(
operator|-
literal|37
argument_list|,
literal|"Transaction IDs are stored in edits log and image files"
argument_list|)
block|,
DECL|enumConstant|TXID_BASED_LAYOUT
name|TXID_BASED_LAYOUT
argument_list|(
operator|-
literal|38
argument_list|,
literal|"File names in NN Storage are based on transaction IDs"
argument_list|)
block|,
DECL|enumConstant|EDITLOG_OP_OPTIMIZATION
name|EDITLOG_OP_OPTIMIZATION
argument_list|(
operator|-
literal|39
argument_list|,
literal|"Use LongWritable and ShortWritable directly instead of ArrayWritable of UTF8"
argument_list|)
block|,
DECL|enumConstant|OPTIMIZE_PERSIST_BLOCKS
name|OPTIMIZE_PERSIST_BLOCKS
argument_list|(
operator|-
literal|40
argument_list|,
literal|"Serialize block lists with delta-encoded variable length ints, "
operator|+
literal|"add OP_UPDATE_BLOCKS"
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL1_2_0
name|RESERVED_REL1_2_0
argument_list|(
operator|-
literal|41
argument_list|,
operator|-
literal|32
argument_list|,
literal|"Reserved for release 1.2.0"
argument_list|,
literal|true
argument_list|,
name|CONCAT
argument_list|)
block|,
DECL|enumConstant|ADD_INODE_ID
name|ADD_INODE_ID
argument_list|(
operator|-
literal|42
argument_list|,
operator|-
literal|40
argument_list|,
literal|"Assign a unique inode id for each inode"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|SNAPSHOT
name|SNAPSHOT
argument_list|(
operator|-
literal|43
argument_list|,
literal|"Support for snapshot feature"
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL1_3_0
name|RESERVED_REL1_3_0
argument_list|(
operator|-
literal|44
argument_list|,
operator|-
literal|41
argument_list|,
literal|"Reserved for release 1.3.0"
argument_list|,
literal|true
argument_list|,
name|ADD_INODE_ID
argument_list|,
name|SNAPSHOT
argument_list|)
block|,
DECL|enumConstant|OPTIMIZE_SNAPSHOT_INODES
name|OPTIMIZE_SNAPSHOT_INODES
argument_list|(
operator|-
literal|45
argument_list|,
operator|-
literal|43
argument_list|,
literal|"Reduce snapshot inode memory footprint"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|SEQUENTIAL_BLOCK_ID
name|SEQUENTIAL_BLOCK_ID
argument_list|(
operator|-
literal|46
argument_list|,
literal|"Allocate block IDs sequentially and store "
operator|+
literal|"block IDs in the edits log and image files"
argument_list|)
block|,
DECL|enumConstant|EDITLOG_SUPPORT_RETRYCACHE
name|EDITLOG_SUPPORT_RETRYCACHE
argument_list|(
operator|-
literal|47
argument_list|,
literal|"Record ClientId and CallId in editlog to "
operator|+
literal|"enable rebuilding retry cache in case of HA failover"
argument_list|)
block|,
DECL|enumConstant|EDITLOG_ADD_BLOCK
name|EDITLOG_ADD_BLOCK
argument_list|(
operator|-
literal|48
argument_list|,
literal|"Add new editlog that only records allocation of "
operator|+
literal|"the new block instead of the entire block list"
argument_list|)
block|,
DECL|enumConstant|ADD_DATANODE_AND_STORAGE_UUIDS
name|ADD_DATANODE_AND_STORAGE_UUIDS
argument_list|(
operator|-
literal|49
argument_list|,
literal|"Replace StorageID with DatanodeUuid."
operator|+
literal|" Use distinct StorageUuid per storage directory."
argument_list|)
block|,
DECL|enumConstant|ADD_LAYOUT_FLAGS
name|ADD_LAYOUT_FLAGS
argument_list|(
operator|-
literal|50
argument_list|,
literal|"Add support for layout flags."
argument_list|)
block|,
DECL|enumConstant|CACHING
name|CACHING
argument_list|(
operator|-
literal|51
argument_list|,
literal|"Support for cache pools and path-based caching"
argument_list|)
block|;
DECL|field|info
specifier|private
specifier|final
name|FeatureInfo
name|info
decl_stmt|;
comment|/**      * Feature that is added at layout version {@code lv} - 1.       * @param lv new layout version with the addition of this feature      * @param description description of the feature      */
DECL|method|Feature (final int lv, final String description)
name|Feature
parameter_list|(
specifier|final
name|int
name|lv
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|lv
argument_list|,
name|lv
operator|+
literal|1
argument_list|,
name|description
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Feature that is added at layout version {@code ancestoryLV}.      * @param lv new layout version with the addition of this feature      * @param ancestorLV layout version from which the new lv is derived from.      * @param description description of the feature      * @param reserved true when this is a layout version reserved for previous      *        version      * @param features set of features that are to be enabled for this version      */
DECL|method|Feature (final int lv, final int ancestorLV, final String description, boolean reserved, Feature... features)
name|Feature
parameter_list|(
specifier|final
name|int
name|lv
parameter_list|,
specifier|final
name|int
name|ancestorLV
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
name|boolean
name|reserved
parameter_list|,
name|Feature
modifier|...
name|features
parameter_list|)
block|{
name|info
operator|=
operator|new
name|FeatureInfo
argument_list|(
name|lv
argument_list|,
name|ancestorLV
argument_list|,
name|description
argument_list|,
name|reserved
argument_list|,
name|features
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInfo ()
specifier|public
name|FeatureInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
block|}
comment|/** Feature information. */
DECL|class|FeatureInfo
specifier|public
specifier|static
class|class
name|FeatureInfo
block|{
DECL|field|lv
specifier|private
specifier|final
name|int
name|lv
decl_stmt|;
DECL|field|ancestorLV
specifier|private
specifier|final
name|int
name|ancestorLV
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|reserved
specifier|private
specifier|final
name|boolean
name|reserved
decl_stmt|;
DECL|field|specialFeatures
specifier|private
specifier|final
name|LayoutFeature
index|[]
name|specialFeatures
decl_stmt|;
DECL|method|FeatureInfo (final int lv, final int ancestorLV, final String description, boolean reserved, LayoutFeature... specialFeatures)
specifier|public
name|FeatureInfo
parameter_list|(
specifier|final
name|int
name|lv
parameter_list|,
specifier|final
name|int
name|ancestorLV
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
name|boolean
name|reserved
parameter_list|,
name|LayoutFeature
modifier|...
name|specialFeatures
parameter_list|)
block|{
name|this
operator|.
name|lv
operator|=
name|lv
expr_stmt|;
name|this
operator|.
name|ancestorLV
operator|=
name|ancestorLV
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|reserved
operator|=
name|reserved
expr_stmt|;
name|this
operator|.
name|specialFeatures
operator|=
name|specialFeatures
expr_stmt|;
block|}
comment|/**       * Accessor method for feature layout version       * @return int lv value      */
DECL|method|getLayoutVersion ()
specifier|public
name|int
name|getLayoutVersion
parameter_list|()
block|{
return|return
name|lv
return|;
block|}
comment|/**       * Accessor method for feature ancestor layout version       * @return int ancestor LV value      */
DECL|method|getAncestorLayoutVersion ()
specifier|public
name|int
name|getAncestorLayoutVersion
parameter_list|()
block|{
return|return
name|ancestorLV
return|;
block|}
comment|/**       * Accessor method for feature description       * @return String feature description       */
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|isReservedForOldRelease ()
specifier|public
name|boolean
name|isReservedForOldRelease
parameter_list|()
block|{
return|return
name|reserved
return|;
block|}
DECL|method|getSpecialFeatures ()
specifier|public
name|LayoutFeature
index|[]
name|getSpecialFeatures
parameter_list|()
block|{
return|return
name|specialFeatures
return|;
block|}
block|}
DECL|class|LayoutFeatureComparator
specifier|static
class|class
name|LayoutFeatureComparator
implements|implements
name|Comparator
argument_list|<
name|LayoutFeature
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (LayoutFeature arg0, LayoutFeature arg1)
specifier|public
name|int
name|compare
parameter_list|(
name|LayoutFeature
name|arg0
parameter_list|,
name|LayoutFeature
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
operator|-
name|arg1
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
return|;
block|}
block|}
DECL|method|updateMap (Map<Integer, SortedSet<LayoutFeature>> map, LayoutFeature[] features)
specifier|public
specifier|static
name|void
name|updateMap
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|map
parameter_list|,
name|LayoutFeature
index|[]
name|features
parameter_list|)
block|{
comment|// Go through all the enum constants and build a map of
comment|// LayoutVersion<-> Set of all supported features in that LayoutVersion
for|for
control|(
name|LayoutFeature
name|f
range|:
name|features
control|)
block|{
specifier|final
name|FeatureInfo
name|info
init|=
name|f
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
name|ancestorSet
init|=
name|map
operator|.
name|get
argument_list|(
name|info
operator|.
name|getAncestorLayoutVersion
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ancestorSet
operator|==
literal|null
condition|)
block|{
comment|// Empty set
name|ancestorSet
operator|=
operator|new
name|TreeSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|(
operator|new
name|LayoutFeatureComparator
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|info
operator|.
name|getAncestorLayoutVersion
argument_list|()
argument_list|,
name|ancestorSet
argument_list|)
expr_stmt|;
block|}
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
name|featureSet
init|=
operator|new
name|TreeSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|(
name|ancestorSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getSpecialFeatures
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|LayoutFeature
name|specialFeature
range|:
name|info
operator|.
name|getSpecialFeatures
argument_list|()
control|)
block|{
name|featureSet
operator|.
name|add
argument_list|(
name|specialFeature
argument_list|)
expr_stmt|;
block|}
block|}
name|featureSet
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|info
operator|.
name|getLayoutVersion
argument_list|()
argument_list|,
name|featureSet
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets formatted string that describes {@link LayoutVersion} information.    */
DECL|method|getString (Map<Integer, SortedSet<LayoutFeature>> map, LayoutFeature[] values)
specifier|public
name|String
name|getString
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|map
parameter_list|,
name|LayoutFeature
index|[]
name|values
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Feature List:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|LayoutFeature
name|f
range|:
name|values
control|)
block|{
specifier|final
name|FeatureInfo
name|info
init|=
name|f
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|f
argument_list|)
operator|.
name|append
argument_list|(
literal|" introduced in layout version "
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")\n"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\n\nLayoutVersion and supported features:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|LayoutFeature
name|f
range|:
name|values
control|)
block|{
specifier|final
name|FeatureInfo
name|info
init|=
name|f
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|info
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|info
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns true if a given feature is supported in the given layout version    * @param map layout feature map    * @param f Feature    * @param lv LayoutVersion    * @return true if {@code f} is supported in layout version {@code lv}    */
DECL|method|supports (Map<Integer, SortedSet<LayoutFeature>> map, final LayoutFeature f, final int lv)
specifier|public
specifier|static
name|boolean
name|supports
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|map
parameter_list|,
specifier|final
name|LayoutFeature
name|f
parameter_list|,
specifier|final
name|int
name|lv
parameter_list|)
block|{
specifier|final
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|lv
argument_list|)
decl_stmt|;
return|return
name|set
operator|!=
literal|null
operator|&&
name|set
operator|.
name|contains
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Get the current layout version    */
DECL|method|getCurrentLayoutVersion ( Map<Integer, SortedSet<LayoutFeature>> map, LayoutFeature[] values)
specifier|public
specifier|static
name|int
name|getCurrentLayoutVersion
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|map
parameter_list|,
name|LayoutFeature
index|[]
name|values
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|values
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|FeatureInfo
name|info
init|=
name|values
index|[
name|i
index|]
operator|.
name|getInfo
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|isReservedForOldRelease
argument_list|()
condition|)
block|{
return|return
name|info
operator|.
name|getLayoutVersion
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"All layout versions are reserved."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

