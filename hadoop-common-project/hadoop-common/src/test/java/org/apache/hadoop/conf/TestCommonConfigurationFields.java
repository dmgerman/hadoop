begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
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
name|AbstractFileSystem
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
name|CommonConfigurationKeys
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
name|CommonConfigurationKeysPublic
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
name|ftp
operator|.
name|FtpConfigKeys
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
name|local
operator|.
name|LocalConfigKeys
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
name|ha
operator|.
name|SshFenceByTcpPort
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
name|ha
operator|.
name|ZKFailoverController
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
name|http
operator|.
name|HttpServer2
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
name|io
operator|.
name|erasurecode
operator|.
name|CodecUtil
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|security
operator|.
name|CompositeGroupsMapping
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
name|security
operator|.
name|HttpCrossOriginFilterInitializer
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
name|security
operator|.
name|LdapGroupsMapping
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
name|security
operator|.
name|http
operator|.
name|CrossOriginFilter
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
name|security
operator|.
name|ssl
operator|.
name|SSLFactory
import|;
end_import

begin_comment
comment|/**  * Unit test class to compare the following Hadoop Configuration classes:  *<p></p>  * {@link org.apache.hadoop.fs.AbstractFileSystem}  * {@link org.apache.hadoop.fs.CommonConfigurationKeys}  * {@link org.apache.hadoop.fs.CommonConfigurationKeysPublic}  * {@link org.apache.hadoop.fs.ftp.FtpConfigKeys}  * {@link org.apache.hadoop.fs.local.LocalConfigKeys}  * {@link org.apache.hadoop.ha.SshFenceByTcpPort}  * {@link org.apache.hadoop.http.HttpServer2}  * {@link org.apache.hadoop.security.LdapGroupsMapping}  * {@link org.apache.hadoop.security.http.CrossOriginFilter}  * {@link org.apache.hadoop.security.ssl.SSLFactory}  * {@link org.apache.hadoop.io.erasurecode.rawcoder.CoderUtil}  *<p></p>  * against core-site.xml for missing properties.  Currently only  * throws an error if the class is missing a property.  *<p></p>  * Refer to {@link org.apache.hadoop.conf.TestConfigurationFieldsBase}  * for how this class works.  */
end_comment

begin_class
DECL|class|TestCommonConfigurationFields
specifier|public
class|class
name|TestCommonConfigurationFields
extends|extends
name|TestConfigurationFieldsBase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
literal|"core-default.xml"
argument_list|)
expr_stmt|;
name|configurationClasses
operator|=
operator|new
name|Class
index|[]
block|{
name|CommonConfigurationKeys
operator|.
name|class
block|,
name|CommonConfigurationKeysPublic
operator|.
name|class
block|,
name|LocalConfigKeys
operator|.
name|class
block|,
name|FtpConfigKeys
operator|.
name|class
block|,
name|SshFenceByTcpPort
operator|.
name|class
block|,
name|LdapGroupsMapping
operator|.
name|class
block|,
name|ZKFailoverController
operator|.
name|class
block|,
name|SSLFactory
operator|.
name|class
block|,
name|CompositeGroupsMapping
operator|.
name|class
block|,
name|CodecUtil
operator|.
name|class
block|}
expr_stmt|;
comment|// Initialize used variables
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
name|configurationPropsToSkipCompare
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// Set error modes
name|errorIfMissingConfigProps
operator|=
literal|true
expr_stmt|;
name|errorIfMissingXmlProps
operator|=
literal|false
expr_stmt|;
comment|// Lots of properties not in the above classes
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.ftp.password.localhost"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.ftp.user.localhost"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.ftp.data.connection.mode"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.ftp.transfer.mode"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"nfs3.mountd.port"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"nfs3.server.port"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.viewfs.rename.strategy"
argument_list|)
expr_stmt|;
comment|// S3A properties are in a different subtree.
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.s3a."
argument_list|)
expr_stmt|;
comment|// WASB properties are in a different subtree.
comment|// - org.apache.hadoop.fs.azure.NativeAzureFileSystem
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.wasb.impl"
argument_list|)
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.wasbs.impl"
argument_list|)
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure."
argument_list|)
expr_stmt|;
comment|// ADL properties are in a different subtree
comment|// - org.apache.hadoop.hdfs.web.ADLConfKeys
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"adl."
argument_list|)
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.adl."
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.AbstractFileSystem.adl.impl"
argument_list|)
expr_stmt|;
comment|// Azure properties are in a different class
comment|// - org.apache.hadoop.fs.azure.AzureNativeFileSystemStore
comment|// - org.apache.hadoop.fs.azure.SASKeyGeneratorImpl
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.sas.expiry.period"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.local.sas.key.mode"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.secure.mode"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.authorization"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.authorization.caching.enable"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.saskey.usecontainersaskeyforallaccess"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.azure.user.agent.prefix"
argument_list|)
expr_stmt|;
comment|// Deprecated properties.  These should eventually be removed from the
comment|// class.
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IO_SORT_MB_KEY
argument_list|)
expr_stmt|;
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IO_SORT_FACTOR_KEY
argument_list|)
expr_stmt|;
comment|// Irrelevant property
name|configurationPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dr.who"
argument_list|)
expr_stmt|;
comment|// XML deprecated properties.
comment|// - org.apache.hadoop.hdfs.client.HdfsClientConfigKeys
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|)
expr_stmt|;
comment|// Properties in other classes that aren't easily determined
comment|// (not following naming convention, in a different project, not public,
comment|// etc.)
comment|// - org.apache.hadoop.http.HttpServer2.FILTER_INITIALIZER_PROPERTY
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.security.HttpCrossOriginFilterInitializer
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|HttpCrossOriginFilterInitializer
operator|.
name|PREFIX
argument_list|)
expr_stmt|;
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.AbstractFileSystem."
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.ha.SshFenceByTcpPort
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.ha.fencing.ssh."
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.classification.RegistryConstants
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.registry."
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.security.AuthenticationFilterInitializer
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.http.authentication."
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.crypto.key.kms.KMSClientProvider;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
name|KMSClientProvider
operator|.
name|AUTH_RETRY
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.io.nativeio.NativeIO
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.workaround.non.threadsafe.getpwuid"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.hdfs.DFSConfigKeys
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"dfs.ha.fencing.methods"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.fs.CommonConfigurationKeysPublic
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_KEY_PREFIX
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.hdfs.server.datanode.DataNode
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.common.configuration.version"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.fs.FileSystem
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.har.impl.disable.cache"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.fs.FileSystem#getFileSystemClass()
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"fs.swift.impl"
argument_list|)
expr_stmt|;
comment|// - package org.apache.hadoop.tracing.TraceUtils ?
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.htrace.span.receiver.classes"
argument_list|)
expr_stmt|;
comment|// Private keys
comment|// - org.apache.hadoop.ha.ZKFailoverController;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"ha.zookeeper.parent-znode"
argument_list|)
expr_stmt|;
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"ha.zookeeper.session-timeout.ms"
argument_list|)
expr_stmt|;
comment|// - Where is this used?
name|xmlPrefixToSkipCompare
operator|.
name|add
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_CLIENT_HTRACE_PREFIX
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.security.UserGroupInformation
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.kerberos.kinit.command"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.net.NetUtils
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.rpc.socket.factory.class.ClientProtocol"
argument_list|)
expr_stmt|;
comment|// - Where is this used?
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"hadoop.ssl.enabled"
argument_list|)
expr_stmt|;
comment|// Keys with no corresponding variable
comment|// - org.apache.hadoop.io.compress.bzip2.Bzip2Factory
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"io.compression.codec.bzip2.library"
argument_list|)
expr_stmt|;
comment|// - org.apache.hadoop.io.SequenceFile
name|xmlPropsToSkipCompare
operator|.
name|add
argument_list|(
literal|"io.seqfile.local.dir"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

