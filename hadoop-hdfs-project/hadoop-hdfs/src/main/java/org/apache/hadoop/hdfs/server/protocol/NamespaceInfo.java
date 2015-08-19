begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
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
name|StorageInfo
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
name|namenode
operator|.
name|NNStorage
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
name|VersionInfo
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * NamespaceInfo is returned by the name-node in reply   * to a data-node handshake.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NamespaceInfo
specifier|public
class|class
name|NamespaceInfo
extends|extends
name|StorageInfo
block|{
DECL|field|buildVersion
specifier|final
name|String
name|buildVersion
decl_stmt|;
DECL|field|blockPoolID
name|String
name|blockPoolID
init|=
literal|""
decl_stmt|;
comment|// id of the block pool
DECL|field|softwareVersion
name|String
name|softwareVersion
decl_stmt|;
DECL|field|capabilities
name|long
name|capabilities
decl_stmt|;
comment|// only authoritative on the server-side to determine advertisement to
comment|// clients.  enum will update the supported values
DECL|field|CAPABILITIES_SUPPORTED
specifier|private
specifier|static
specifier|final
name|long
name|CAPABILITIES_SUPPORTED
init|=
name|getSupportedCapabilities
argument_list|()
decl_stmt|;
DECL|method|getSupportedCapabilities ()
specifier|private
specifier|static
name|long
name|getSupportedCapabilities
parameter_list|()
block|{
name|long
name|mask
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Capability
name|c
range|:
name|Capability
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|supported
condition|)
block|{
name|mask
operator||=
name|c
operator|.
name|mask
expr_stmt|;
block|}
block|}
return|return
name|mask
return|;
block|}
DECL|enum|Capability
specifier|public
enum|enum
name|Capability
block|{
DECL|enumConstant|UNKNOWN
name|UNKNOWN
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|STORAGE_BLOCK_REPORT_BUFFERS
name|STORAGE_BLOCK_REPORT_BUFFERS
argument_list|(
literal|true
argument_list|)
block|;
comment|// use optimized ByteString buffers
DECL|field|supported
specifier|private
specifier|final
name|boolean
name|supported
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|long
name|mask
decl_stmt|;
DECL|method|Capability (boolean isSupported)
name|Capability
parameter_list|(
name|boolean
name|isSupported
parameter_list|)
block|{
name|supported
operator|=
name|isSupported
expr_stmt|;
name|int
name|bits
init|=
name|ordinal
argument_list|()
operator|-
literal|1
decl_stmt|;
name|mask
operator|=
operator|(
name|bits
operator|<
literal|0
operator|)
condition|?
literal|0
else|:
operator|(
literal|1L
operator|<<
name|bits
operator|)
expr_stmt|;
block|}
DECL|method|getMask ()
specifier|public
name|long
name|getMask
parameter_list|()
block|{
return|return
name|mask
return|;
block|}
block|}
comment|// defaults to enabled capabilites since this ctor is for server
DECL|method|NamespaceInfo ()
specifier|public
name|NamespaceInfo
parameter_list|()
block|{
name|super
argument_list|(
name|NodeType
operator|.
name|NAME_NODE
argument_list|)
expr_stmt|;
name|buildVersion
operator|=
literal|null
expr_stmt|;
name|capabilities
operator|=
name|CAPABILITIES_SUPPORTED
expr_stmt|;
block|}
comment|// defaults to enabled capabilites since this ctor is for server
DECL|method|NamespaceInfo (int nsID, String clusterID, String bpID, long cT, String buildVersion, String softwareVersion)
specifier|public
name|NamespaceInfo
parameter_list|(
name|int
name|nsID
parameter_list|,
name|String
name|clusterID
parameter_list|,
name|String
name|bpID
parameter_list|,
name|long
name|cT
parameter_list|,
name|String
name|buildVersion
parameter_list|,
name|String
name|softwareVersion
parameter_list|)
block|{
name|this
argument_list|(
name|nsID
argument_list|,
name|clusterID
argument_list|,
name|bpID
argument_list|,
name|cT
argument_list|,
name|buildVersion
argument_list|,
name|softwareVersion
argument_list|,
name|CAPABILITIES_SUPPORTED
argument_list|)
expr_stmt|;
block|}
comment|// for use by server and/or client
DECL|method|NamespaceInfo (int nsID, String clusterID, String bpID, long cT, String buildVersion, String softwareVersion, long capabilities)
specifier|public
name|NamespaceInfo
parameter_list|(
name|int
name|nsID
parameter_list|,
name|String
name|clusterID
parameter_list|,
name|String
name|bpID
parameter_list|,
name|long
name|cT
parameter_list|,
name|String
name|buildVersion
parameter_list|,
name|String
name|softwareVersion
parameter_list|,
name|long
name|capabilities
parameter_list|)
block|{
name|super
argument_list|(
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
argument_list|,
name|nsID
argument_list|,
name|clusterID
argument_list|,
name|cT
argument_list|,
name|NodeType
operator|.
name|NAME_NODE
argument_list|)
expr_stmt|;
name|blockPoolID
operator|=
name|bpID
expr_stmt|;
name|this
operator|.
name|buildVersion
operator|=
name|buildVersion
expr_stmt|;
name|this
operator|.
name|softwareVersion
operator|=
name|softwareVersion
expr_stmt|;
name|this
operator|.
name|capabilities
operator|=
name|capabilities
expr_stmt|;
block|}
DECL|method|NamespaceInfo (int nsID, String clusterID, String bpID, long cT)
specifier|public
name|NamespaceInfo
parameter_list|(
name|int
name|nsID
parameter_list|,
name|String
name|clusterID
parameter_list|,
name|String
name|bpID
parameter_list|,
name|long
name|cT
parameter_list|)
block|{
name|this
argument_list|(
name|nsID
argument_list|,
name|clusterID
argument_list|,
name|bpID
argument_list|,
name|cT
argument_list|,
name|Storage
operator|.
name|getBuildVersion
argument_list|()
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCapabilities ()
specifier|public
name|long
name|getCapabilities
parameter_list|()
block|{
return|return
name|capabilities
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setCapabilities (long capabilities)
specifier|public
name|void
name|setCapabilities
parameter_list|(
name|long
name|capabilities
parameter_list|)
block|{
name|this
operator|.
name|capabilities
operator|=
name|capabilities
expr_stmt|;
block|}
DECL|method|isCapabilitySupported (Capability capability)
specifier|public
name|boolean
name|isCapabilitySupported
parameter_list|(
name|Capability
name|capability
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|capability
operator|!=
name|Capability
operator|.
name|UNKNOWN
argument_list|,
literal|"cannot test for unknown capability"
argument_list|)
expr_stmt|;
name|long
name|mask
init|=
name|capability
operator|.
name|getMask
argument_list|()
decl_stmt|;
return|return
operator|(
name|capabilities
operator|&
name|mask
operator|)
operator|==
name|mask
return|;
block|}
DECL|method|getBuildVersion ()
specifier|public
name|String
name|getBuildVersion
parameter_list|()
block|{
return|return
name|buildVersion
return|;
block|}
DECL|method|getBlockPoolID ()
specifier|public
name|String
name|getBlockPoolID
parameter_list|()
block|{
return|return
name|blockPoolID
return|;
block|}
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
parameter_list|()
block|{
return|return
name|softwareVersion
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
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|";bpid="
operator|+
name|blockPoolID
return|;
block|}
DECL|method|validateStorage (NNStorage storage)
specifier|public
name|void
name|validateStorage
parameter_list|(
name|NNStorage
name|storage
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|layoutVersion
operator|!=
name|storage
operator|.
name|getLayoutVersion
argument_list|()
operator|||
name|namespaceID
operator|!=
name|storage
operator|.
name|getNamespaceID
argument_list|()
operator|||
name|cTime
operator|!=
name|storage
operator|.
name|cTime
operator|||
operator|!
name|clusterID
operator|.
name|equals
argument_list|(
name|storage
operator|.
name|getClusterID
argument_list|()
argument_list|)
operator|||
operator|!
name|blockPoolID
operator|.
name|equals
argument_list|(
name|storage
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Inconsistent namespace information:\n"
operator|+
literal|"NamespaceInfo has:\n"
operator|+
literal|"LV="
operator|+
name|layoutVersion
operator|+
literal|";"
operator|+
literal|"NS="
operator|+
name|namespaceID
operator|+
literal|";"
operator|+
literal|"cTime="
operator|+
name|cTime
operator|+
literal|";"
operator|+
literal|"CID="
operator|+
name|clusterID
operator|+
literal|";"
operator|+
literal|"BPID="
operator|+
name|blockPoolID
operator|+
literal|".\nStorage has:\n"
operator|+
literal|"LV="
operator|+
name|storage
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|";"
operator|+
literal|"NS="
operator|+
name|storage
operator|.
name|getNamespaceID
argument_list|()
operator|+
literal|";"
operator|+
literal|"cTime="
operator|+
name|storage
operator|.
name|getCTime
argument_list|()
operator|+
literal|";"
operator|+
literal|"CID="
operator|+
name|storage
operator|.
name|getClusterID
argument_list|()
operator|+
literal|";"
operator|+
literal|"BPID="
operator|+
name|storage
operator|.
name|getBlockPoolID
argument_list|()
operator|+
literal|"."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

