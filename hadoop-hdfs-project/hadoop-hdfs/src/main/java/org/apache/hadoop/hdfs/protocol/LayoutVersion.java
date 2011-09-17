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
name|EnumSet
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
comment|/**    * Enums for features that change the layout version.    *<br><br>    * To add a new layout version:    *<ul>    *<li>Define a new enum constant with a short enum name, the new layout version     * and description of the added feature.</li>    *<li>When adding a layout version with an ancestor that is not same as    * its immediate predecessor, use the constructor where a spacific ancestor    * can be passed.    *</li>    *</ul>    */
DECL|enum|Feature
specifier|public
specifier|static
enum|enum
name|Feature
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
argument_list|)
block|,
DECL|enumConstant|RESERVED_REL20_204
name|RESERVED_REL20_204
argument_list|(
operator|-
literal|32
argument_list|,
literal|"Reserved for release 0.20.204"
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
block|;
DECL|field|lv
specifier|final
name|int
name|lv
decl_stmt|;
DECL|field|ancestorLV
specifier|final
name|int
name|ancestorLV
decl_stmt|;
DECL|field|description
specifier|final
name|String
name|description
decl_stmt|;
comment|/**      * Feature that is added at {@code currentLV}.       * @param lv new layout version with the addition of this feature      * @param description description of the feature      */
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
argument_list|)
expr_stmt|;
block|}
comment|/**      * Feature that is added at {@code currentLV}.      * @param lv new layout version with the addition of this feature      * @param ancestorLV layout version from which the new lv is derived      *          from.      * @param description description of the feature      */
DECL|method|Feature (final int lv, final int ancestorLV, final String description)
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
block|}
comment|// Build layout version and corresponding feature matrix
DECL|field|map
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|EnumSet
argument_list|<
name|Feature
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|EnumSet
argument_list|<
name|Feature
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// Static initialization
static|static
block|{
name|initMap
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initialize the map of a layout version and EnumSet of {@link Feature}s     * supported.    */
DECL|method|initMap ()
specifier|private
specifier|static
name|void
name|initMap
parameter_list|()
block|{
comment|// Go through all the enum constants and build a map of
comment|// LayoutVersion<-> EnumSet of all supported features in that LayoutVersion
for|for
control|(
name|Feature
name|f
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
name|EnumSet
argument_list|<
name|Feature
argument_list|>
name|ancestorSet
init|=
name|map
operator|.
name|get
argument_list|(
name|f
operator|.
name|ancestorLV
argument_list|)
decl_stmt|;
if|if
condition|(
name|ancestorSet
operator|==
literal|null
condition|)
block|{
name|ancestorSet
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Feature
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Empty enum set
name|map
operator|.
name|put
argument_list|(
name|f
operator|.
name|ancestorLV
argument_list|,
name|ancestorSet
argument_list|)
expr_stmt|;
block|}
name|EnumSet
argument_list|<
name|Feature
argument_list|>
name|featureSet
init|=
name|EnumSet
operator|.
name|copyOf
argument_list|(
name|ancestorSet
argument_list|)
decl_stmt|;
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
name|f
operator|.
name|lv
argument_list|,
name|featureSet
argument_list|)
expr_stmt|;
block|}
comment|// Special initialization for 0.20.203 and 0.20.204
comment|// to add Feature#DELEGATION_TOKEN
name|specialInit
argument_list|(
name|Feature
operator|.
name|RESERVED_REL20_203
operator|.
name|lv
argument_list|,
name|Feature
operator|.
name|DELEGATION_TOKEN
argument_list|)
expr_stmt|;
name|specialInit
argument_list|(
name|Feature
operator|.
name|RESERVED_REL20_204
operator|.
name|lv
argument_list|,
name|Feature
operator|.
name|DELEGATION_TOKEN
argument_list|)
expr_stmt|;
block|}
DECL|method|specialInit (int lv, Feature f)
specifier|private
specifier|static
name|void
name|specialInit
parameter_list|(
name|int
name|lv
parameter_list|,
name|Feature
name|f
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|Feature
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
name|set
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets formatted string that describes {@link LayoutVersion} information.    */
DECL|method|getString ()
specifier|public
specifier|static
name|String
name|getString
parameter_list|()
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
name|Feature
name|f
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
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
name|f
operator|.
name|lv
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|f
operator|.
name|description
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
name|Feature
name|f
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|f
operator|.
name|lv
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
name|f
operator|.
name|lv
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
comment|/**    * Returns true if a given feature is supported in the given layout version    * @param f Feature    * @param lv LayoutVersion    * @return true if {@code f} is supported in layout version {@code lv}    */
DECL|method|supports (final Feature f, final int lv)
specifier|public
specifier|static
name|boolean
name|supports
parameter_list|(
specifier|final
name|Feature
name|f
parameter_list|,
specifier|final
name|int
name|lv
parameter_list|)
block|{
specifier|final
name|EnumSet
argument_list|<
name|Feature
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
DECL|method|getCurrentLayoutVersion ()
specifier|public
specifier|static
name|int
name|getCurrentLayoutVersion
parameter_list|()
block|{
name|Feature
index|[]
name|values
init|=
name|Feature
operator|.
name|values
argument_list|()
decl_stmt|;
return|return
name|values
index|[
name|values
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|lv
return|;
block|}
block|}
end_class

end_unit

