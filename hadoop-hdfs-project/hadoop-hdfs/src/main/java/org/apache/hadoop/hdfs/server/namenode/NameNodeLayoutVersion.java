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
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|hdfs
operator|.
name|protocol
operator|.
name|LayoutVersion
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
name|LayoutVersion
operator|.
name|FeatureInfo
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
name|LayoutVersion
operator|.
name|LayoutFeature
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeLayoutVersion
specifier|public
class|class
name|NameNodeLayoutVersion
block|{
comment|/** Build layout version and corresponding feature matrix */
DECL|field|FEATURES
specifier|public
specifier|final
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|FEATURES
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|CURRENT_LAYOUT_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|CURRENT_LAYOUT_VERSION
init|=
name|LayoutVersion
operator|.
name|getCurrentLayoutVersion
argument_list|(
name|Feature
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|LayoutVersion
operator|.
name|updateMap
argument_list|(
name|FEATURES
argument_list|,
name|LayoutVersion
operator|.
name|Feature
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|LayoutVersion
operator|.
name|updateMap
argument_list|(
name|FEATURES
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|Feature
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getFeatures (int lv)
specifier|public
specifier|static
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
name|getFeatures
parameter_list|(
name|int
name|lv
parameter_list|)
block|{
return|return
name|FEATURES
operator|.
name|get
argument_list|(
name|lv
argument_list|)
return|;
block|}
DECL|method|supports (final LayoutFeature f, final int lv)
specifier|public
specifier|static
name|boolean
name|supports
parameter_list|(
specifier|final
name|LayoutFeature
name|f
parameter_list|,
specifier|final
name|int
name|lv
parameter_list|)
block|{
return|return
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|FEATURES
argument_list|,
name|f
argument_list|,
name|lv
argument_list|)
return|;
block|}
comment|/**    * Enums for features that change the layout version.    *<br><br>    * To add a new layout version:    *<ul>    *<li>Define a new enum constant with a short enum name, the new layout version     * and description of the added feature.</li>    *<li>When adding a layout version with an ancestor that is not same as    * its immediate predecessor, use the constructor where a specific ancestor    * can be passed.    *</li>    *</ul>    */
DECL|enum|Feature
specifier|public
specifier|static
enum|enum
name|Feature
implements|implements
name|LayoutFeature
block|{
DECL|enumConstant|ROLLING_UPGRADE
name|ROLLING_UPGRADE
argument_list|(
operator|-
literal|55
argument_list|,
operator|-
literal|53
argument_list|,
literal|"Support rolling upgrade"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|EDITLOG_LENGTH
name|EDITLOG_LENGTH
argument_list|(
operator|-
literal|56
argument_list|,
literal|"Add length field to every edit log op"
argument_list|)
block|,
DECL|enumConstant|XATTRS
name|XATTRS
argument_list|(
operator|-
literal|57
argument_list|,
literal|"Extended attributes"
argument_list|)
block|,
DECL|enumConstant|CREATE_OVERWRITE
name|CREATE_OVERWRITE
argument_list|(
operator|-
literal|58
argument_list|,
literal|"Use single editlog record for "
operator|+
literal|"creating file with overwrite"
argument_list|)
block|,
DECL|enumConstant|XATTRS_NAMESPACE_EXT
name|XATTRS_NAMESPACE_EXT
argument_list|(
operator|-
literal|59
argument_list|,
literal|"Increase number of xattr namespaces"
argument_list|)
block|,
DECL|enumConstant|LAZY_PERSIST_FILES
name|LAZY_PERSIST_FILES
argument_list|(
operator|-
literal|60
argument_list|,
literal|"Support for optional lazy persistence of "
operator|+
literal|" files with reduced durability guarantees"
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
comment|/**      * NameNode feature that is added at layout version {@code ancestoryLV}.      * @param lv new layout version with the addition of this feature      * @param ancestorLV layout version from which the new lv is derived from.      * @param description description of the feature      * @param reserved true when this is a layout version reserved for previous      *        versions      * @param features set of features that are to be enabled for this version      */
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
block|}
end_class

end_unit

