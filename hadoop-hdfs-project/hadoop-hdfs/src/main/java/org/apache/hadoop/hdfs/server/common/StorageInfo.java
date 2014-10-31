begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
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
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|RandomAccessFile
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
name|Properties
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
name|Feature
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NodeType
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|server
operator|.
name|datanode
operator|.
name|DataNodeLayoutVersion
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
name|server
operator|.
name|namenode
operator|.
name|NameNodeLayoutVersion
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
name|Joiner
import|;
end_import

begin_comment
comment|/**  * Common class for storage information.  *   * TODO namespaceID should be long and computed as hash(address + port)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageInfo
specifier|public
class|class
name|StorageInfo
block|{
DECL|field|layoutVersion
specifier|public
name|int
name|layoutVersion
decl_stmt|;
comment|// layout version of the storage data
DECL|field|namespaceID
specifier|public
name|int
name|namespaceID
decl_stmt|;
comment|// id of the file system
DECL|field|clusterID
specifier|public
name|String
name|clusterID
decl_stmt|;
comment|// id of the cluster
DECL|field|cTime
specifier|public
name|long
name|cTime
decl_stmt|;
comment|// creation time of the file system state
DECL|field|storageType
specifier|protected
specifier|final
name|NodeType
name|storageType
decl_stmt|;
comment|// Type of the node using this storage
DECL|field|STORAGE_FILE_VERSION
specifier|protected
specifier|static
specifier|final
name|String
name|STORAGE_FILE_VERSION
init|=
literal|"VERSION"
decl_stmt|;
DECL|method|StorageInfo (NodeType type)
specifier|public
name|StorageInfo
parameter_list|(
name|NodeType
name|type
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|,
literal|0L
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|StorageInfo (int layoutV, int nsID, String cid, long cT, NodeType type)
specifier|public
name|StorageInfo
parameter_list|(
name|int
name|layoutV
parameter_list|,
name|int
name|nsID
parameter_list|,
name|String
name|cid
parameter_list|,
name|long
name|cT
parameter_list|,
name|NodeType
name|type
parameter_list|)
block|{
name|layoutVersion
operator|=
name|layoutV
expr_stmt|;
name|clusterID
operator|=
name|cid
expr_stmt|;
name|namespaceID
operator|=
name|nsID
expr_stmt|;
name|cTime
operator|=
name|cT
expr_stmt|;
name|storageType
operator|=
name|type
expr_stmt|;
block|}
DECL|method|StorageInfo (StorageInfo from)
specifier|public
name|StorageInfo
parameter_list|(
name|StorageInfo
name|from
parameter_list|)
block|{
name|this
argument_list|(
name|from
operator|.
name|layoutVersion
argument_list|,
name|from
operator|.
name|namespaceID
argument_list|,
name|from
operator|.
name|clusterID
argument_list|,
name|from
operator|.
name|cTime
argument_list|,
name|from
operator|.
name|storageType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Layout version of the storage data.    */
DECL|method|getLayoutVersion ()
specifier|public
name|int
name|getLayoutVersion
parameter_list|()
block|{
return|return
name|layoutVersion
return|;
block|}
comment|/**    * Namespace id of the file system.<p>    * Assigned to the file system at formatting and never changes after that.    * Shared by all file system components.    */
DECL|method|getNamespaceID ()
specifier|public
name|int
name|getNamespaceID
parameter_list|()
block|{
return|return
name|namespaceID
return|;
block|}
comment|/**    * cluster id of the file system.<p>    */
DECL|method|getClusterID ()
specifier|public
name|String
name|getClusterID
parameter_list|()
block|{
return|return
name|clusterID
return|;
block|}
comment|/**    * Creation time of the file system state.<p>    * Modified during upgrades.    */
DECL|method|getCTime ()
specifier|public
name|long
name|getCTime
parameter_list|()
block|{
return|return
name|cTime
return|;
block|}
DECL|method|setStorageInfo (StorageInfo from)
specifier|public
name|void
name|setStorageInfo
parameter_list|(
name|StorageInfo
name|from
parameter_list|)
block|{
name|layoutVersion
operator|=
name|from
operator|.
name|layoutVersion
expr_stmt|;
name|clusterID
operator|=
name|from
operator|.
name|clusterID
expr_stmt|;
name|namespaceID
operator|=
name|from
operator|.
name|namespaceID
expr_stmt|;
name|cTime
operator|=
name|from
operator|.
name|cTime
expr_stmt|;
block|}
DECL|method|versionSupportsFederation ( Map<Integer, SortedSet<LayoutFeature>> map)
specifier|public
name|boolean
name|versionSupportsFederation
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
parameter_list|)
block|{
return|return
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|map
argument_list|,
name|LayoutVersion
operator|.
name|Feature
operator|.
name|FEDERATION
argument_list|,
name|layoutVersion
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"lv="
argument_list|)
operator|.
name|append
argument_list|(
name|layoutVersion
argument_list|)
operator|.
name|append
argument_list|(
literal|";cid="
argument_list|)
operator|.
name|append
argument_list|(
name|clusterID
argument_list|)
operator|.
name|append
argument_list|(
literal|";nsid="
argument_list|)
operator|.
name|append
argument_list|(
name|namespaceID
argument_list|)
operator|.
name|append
argument_list|(
literal|";c="
argument_list|)
operator|.
name|append
argument_list|(
name|cTime
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toColonSeparatedString ()
specifier|public
name|String
name|toColonSeparatedString
parameter_list|()
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|":"
argument_list|)
operator|.
name|join
argument_list|(
name|layoutVersion
argument_list|,
name|namespaceID
argument_list|,
name|cTime
argument_list|,
name|clusterID
argument_list|)
return|;
block|}
DECL|method|getNsIdFromColonSeparatedString (String in)
specifier|public
specifier|static
name|int
name|getNsIdFromColonSeparatedString
parameter_list|(
name|String
name|in
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|in
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
argument_list|)
return|;
block|}
DECL|method|getClusterIdFromColonSeparatedString (String in)
specifier|public
specifier|static
name|String
name|getClusterIdFromColonSeparatedString
parameter_list|(
name|String
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|3
index|]
return|;
block|}
comment|/**    * Read properties from the VERSION file in the given storage directory.    */
DECL|method|readProperties (StorageDirectory sd)
specifier|public
name|void
name|readProperties
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|props
init|=
name|readPropertiesFile
argument_list|(
name|sd
operator|.
name|getVersionFile
argument_list|()
argument_list|)
decl_stmt|;
name|setFieldsFromProperties
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read properties from the the previous/VERSION file in the given storage directory.    */
DECL|method|readPreviousVersionProperties (StorageDirectory sd)
specifier|public
name|void
name|readPreviousVersionProperties
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|props
init|=
name|readPropertiesFile
argument_list|(
name|sd
operator|.
name|getPreviousVersionFile
argument_list|()
argument_list|)
decl_stmt|;
name|setFieldsFromProperties
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get common storage fields.    * Should be overloaded if additional fields need to be get.    *     * @param props properties    * @throws IOException on error    */
DECL|method|setFieldsFromProperties ( Properties props, StorageDirectory sd)
specifier|protected
name|void
name|setFieldsFromProperties
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|setLayoutVersion
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|setNamespaceID
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|setcTime
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|setClusterId
argument_list|(
name|props
argument_list|,
name|layoutVersion
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|checkStorageType
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
block|}
comment|/** Validate and set storage type from {@link Properties}*/
DECL|method|checkStorageType (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|checkStorageType
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
if|if
condition|(
name|storageType
operator|==
literal|null
condition|)
block|{
comment|//don't care about storage type
return|return;
block|}
name|NodeType
name|type
init|=
name|NodeType
operator|.
name|valueOf
argument_list|(
name|getProperty
argument_list|(
name|props
argument_list|,
name|sd
argument_list|,
literal|"storageType"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|storageType
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|sd
operator|.
name|root
argument_list|,
literal|"Incompatible node types: storageType="
operator|+
name|storageType
operator|+
literal|" but StorageDirectory type="
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/** Validate and set ctime from {@link Properties}*/
DECL|method|setcTime (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|setcTime
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
name|cTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|getProperty
argument_list|(
name|props
argument_list|,
name|sd
argument_list|,
literal|"cTime"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Validate and set clusterId from {@link Properties}*/
DECL|method|setClusterId (Properties props, int layoutVersion, StorageDirectory sd)
specifier|protected
name|void
name|setClusterId
parameter_list|(
name|Properties
name|props
parameter_list|,
name|int
name|layoutVersion
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
comment|// Set cluster ID in version that supports federation
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|getServiceLayoutFeatureMap
argument_list|()
argument_list|,
name|Feature
operator|.
name|FEDERATION
argument_list|,
name|layoutVersion
argument_list|)
condition|)
block|{
name|String
name|cid
init|=
name|getProperty
argument_list|(
name|props
argument_list|,
name|sd
argument_list|,
literal|"clusterID"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|clusterID
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|cid
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|clusterID
operator|.
name|equals
argument_list|(
name|cid
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"cluster Id is incompatible with others."
argument_list|)
throw|;
block|}
name|clusterID
operator|=
name|cid
expr_stmt|;
block|}
block|}
comment|/** Validate and set layout version from {@link Properties}*/
DECL|method|setLayoutVersion (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|setLayoutVersion
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IncorrectVersionException
throws|,
name|InconsistentFSStateException
block|{
name|int
name|lv
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getProperty
argument_list|(
name|props
argument_list|,
name|sd
argument_list|,
literal|"layoutVersion"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|lv
operator|<
name|getServiceLayoutVersion
argument_list|()
condition|)
block|{
comment|// future version
throw|throw
operator|new
name|IncorrectVersionException
argument_list|(
name|getServiceLayoutVersion
argument_list|()
argument_list|,
name|lv
argument_list|,
literal|"storage directory "
operator|+
name|sd
operator|.
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|layoutVersion
operator|=
name|lv
expr_stmt|;
block|}
comment|/** Validate and set namespaceID version from {@link Properties}*/
DECL|method|setNamespaceID (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|setNamespaceID
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
name|int
name|nsId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getProperty
argument_list|(
name|props
argument_list|,
name|sd
argument_list|,
literal|"namespaceID"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaceID
operator|!=
literal|0
operator|&&
name|nsId
operator|!=
literal|0
operator|&&
name|namespaceID
operator|!=
name|nsId
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|sd
operator|.
name|root
argument_list|,
literal|"namespaceID is incompatible with others."
argument_list|)
throw|;
block|}
name|namespaceID
operator|=
name|nsId
expr_stmt|;
block|}
DECL|method|setServiceLayoutVersion (int lv)
specifier|public
name|void
name|setServiceLayoutVersion
parameter_list|(
name|int
name|lv
parameter_list|)
block|{
name|this
operator|.
name|layoutVersion
operator|=
name|lv
expr_stmt|;
block|}
DECL|method|getServiceLayoutVersion ()
specifier|public
name|int
name|getServiceLayoutVersion
parameter_list|()
block|{
return|return
name|storageType
operator|==
name|NodeType
operator|.
name|DATA_NODE
condition|?
name|HdfsConstants
operator|.
name|DATANODE_LAYOUT_VERSION
else|:
name|HdfsConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
return|;
block|}
DECL|method|getServiceLayoutFeatureMap ()
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSet
argument_list|<
name|LayoutFeature
argument_list|>
argument_list|>
name|getServiceLayoutFeatureMap
parameter_list|()
block|{
return|return
name|storageType
operator|==
name|NodeType
operator|.
name|DATA_NODE
condition|?
name|DataNodeLayoutVersion
operator|.
name|FEATURES
else|:
name|NameNodeLayoutVersion
operator|.
name|FEATURES
return|;
block|}
DECL|method|getProperty (Properties props, StorageDirectory sd, String name)
specifier|protected
specifier|static
name|String
name|getProperty
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
name|String
name|property
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|sd
operator|.
name|root
argument_list|,
literal|"file "
operator|+
name|STORAGE_FILE_VERSION
operator|+
literal|" has "
operator|+
name|name
operator|+
literal|" missing."
argument_list|)
throw|;
block|}
return|return
name|property
return|;
block|}
DECL|method|readPropertiesFile (File from)
specifier|public
specifier|static
name|Properties
name|readPropertiesFile
parameter_list|(
name|File
name|from
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|from
argument_list|,
literal|"rws"
argument_list|)
decl_stmt|;
name|FileInputStream
name|in
init|=
literal|null
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
operator|.
name|getFD
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
block|}
end_class

end_unit

