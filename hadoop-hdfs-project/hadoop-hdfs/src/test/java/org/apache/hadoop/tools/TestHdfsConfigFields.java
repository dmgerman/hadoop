begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|conf
operator|.
name|TestConfigurationFieldsBase
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
name|DFSConfigKeys
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
name|client
operator|.
name|HdfsClientConfigKeys
import|;
end_import

begin_comment
comment|/**  * Unit test class to compare the following MR Configuration classes:  *<p></p>  * {@link org.apache.hadoop.hdfs.DFSConfigKeys}  *<p></p>  * against hdfs-default.xml for missing properties.  Currently only  * throws an error if the class is missing a property.  *<p></p>  * Refer to {@link org.apache.hadoop.conf.TestConfigurationFieldsBase}  * for how this class works.  */
end_comment

begin_class
DECL|class|TestHdfsConfigFields
specifier|public
class|class
name|TestHdfsConfigFields
extends|extends
name|TestConfigurationFieldsBase
block|{
annotation|@
name|Override
DECL|method|initializeMemberVariables ()
specifier|public
name|void
name|initializeMemberVariables
parameter_list|()
block|{
name|xmlFilename
operator|=
operator|new
name|String
argument_list|(
literal|"hdfs-default.xml"
argument_list|)
expr_stmt|;
name|configurationClasses
operator|=
operator|new
name|Class
index|[]
block|{
name|HdfsClientConfigKeys
operator|.
name|class
block|,
name|DFSConfigKeys
operator|.
name|class
block|}
expr_stmt|;
comment|// Set error modes
name|errorIfMissingConfigProps
operator|=
literal|true
expr_stmt|;
name|errorIfMissingXmlProps
operator|=
literal|true
expr_stmt|;
comment|// Initialize used variables
name|configurationPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Ignore testing based parameter
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"ignore.secure.ports.for.testing"
argument_list|)
expr_stmt|;
comment|// Remove deprecated properties listed in Configuration#DeprecationDelta
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DF_INTERVAL_KEY
argument_list|)
expr_stmt|;
comment|// Remove default properties
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESSION_CODEC_DEFAULT
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_AUTHENTICATION_FILTER_DEFAULT
argument_list|)
expr_stmt|;
comment|// Remove support property
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MIN_SUPPORTED_DATANODE_VERSION_KEY
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_KEY
argument_list|)
expr_stmt|;
comment|// Purposely hidden, based on comments in DFSConfigKeys
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY
argument_list|)
expr_stmt|;
comment|// Fully deprecated properties?
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.corruptfilesreturned.max"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.metrics.session-id"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.datanode.synconclose"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.datanode.non.local.lazy.persist"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.namenode.tolerate.heartbeat.multiplier"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.namenode.stripe.min"
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.namenode.replqueue.threshold-pct"
argument_list|)
expr_stmt|;
comment|// Removed by HDFS-6440
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.ha.log-roll.rpc.timeout"
argument_list|)
expr_stmt|;
comment|// Example (not real) property in hdfs-default.xml
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.ha.namenodes"
argument_list|)
expr_stmt|;
comment|// Property used for internal testing only
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DUPLICATE_REPLICA_DELETION
argument_list|)
expr_stmt|;
comment|// Property not intended for users
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_STARTUP_KEY
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STARTUP_KEY
argument_list|)
expr_stmt|;
comment|// Allocate
name|xmlPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Used in native code fuse_connect.c
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.fuse.timer.period"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.fuse.connection.timeout"
argument_list|)
expr_stmt|;
comment|// Used dynamically as part of DFSConfigKeys.DFS_NAMENODE_EDITS_PLUGIN_PREFIX
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.namenode.edits.journal-plugin.qjournal"
argument_list|)
expr_stmt|;
comment|// Defined in org.apache.hadoop.fs.CommonConfigurationKeys
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.user.group.metrics.percentiles.intervals"
argument_list|)
expr_stmt|;
comment|// Used oddly by DataNode to create new config String
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.hdfs.configuration.version"
argument_list|)
expr_stmt|;
comment|// Skip comparing in branch-2.  Removed in trunk with HDFS-7985.
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.webhdfs.enabled"
argument_list|)
expr_stmt|;
comment|// Some properties have moved to HdfsClientConfigKeys
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.client.short.circuit.replica.stale.threshold.ms"
argument_list|)
expr_stmt|;
comment|// Ignore HTrace properties
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.client.htrace"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.htrace"
argument_list|)
expr_stmt|;
comment|// Ignore SpanReceiveHost properties
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.htrace.spanreceiver.classes"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.client.htrace.spanreceiver.classes"
argument_list|)
expr_stmt|;
comment|// Remove deprecated properties listed in Configuration#DeprecationDelta
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DF_INTERVAL_KEY
argument_list|)
expr_stmt|;
comment|// Kept in the NfsConfiguration class in the hadoop-hdfs-nfs module
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"nfs"
argument_list|)
expr_stmt|;
comment|// Not a hardcoded property.  Used by SaslRpcClient
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.namenode.kerberos.principal.pattern"
argument_list|)
expr_stmt|;
comment|// Skip over example property
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.ha.namenodes"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

