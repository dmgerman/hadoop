begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|NetworkInterface
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Configuration
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
name|FSDataOutputStream
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
name|FileStatus
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
name|FileSystem
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
name|Path
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
name|permission
operator|.
name|FsPermission
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|web
operator|.
name|WebHdfsConstants
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
name|web
operator|.
name|WebHdfsFileSystem
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
name|web
operator|.
name|WebHdfsTestUtil
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
name|TestDoAsEffectiveUser
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
name|UserGroupInformation
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
name|authorize
operator|.
name|DefaultImpersonationProvider
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
name|authorize
operator|.
name|ProxyUsers
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
name|token
operator|.
name|Token
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
name|test
operator|.
name|Whitebox
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestDelegationTokenForProxyUser
specifier|public
class|class
name|TestDelegationTokenForProxyUser
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|Configuration
name|config
decl_stmt|;
DECL|field|GROUP1_NAME
specifier|final
specifier|private
specifier|static
name|String
name|GROUP1_NAME
init|=
literal|"group1"
decl_stmt|;
DECL|field|GROUP2_NAME
specifier|final
specifier|private
specifier|static
name|String
name|GROUP2_NAME
init|=
literal|"group2"
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|GROUP1_NAME
block|,
name|GROUP2_NAME
block|}
decl_stmt|;
DECL|field|REAL_USER
specifier|final
specifier|private
specifier|static
name|String
name|REAL_USER
init|=
literal|"RealUser"
decl_stmt|;
DECL|field|PROXY_USER
specifier|final
specifier|private
specifier|static
name|String
name|PROXY_USER
init|=
literal|"ProxyUser"
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|static
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|proxyUgi
specifier|private
specifier|static
name|UserGroupInformation
name|proxyUgi
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDoAsEffectiveUser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|configureSuperUserIPAddresses (Configuration conf, String superUserShortName)
specifier|private
specifier|static
name|void
name|configureSuperUserIPAddresses
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|superUserShortName
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ipList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|netInterfaceList
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|netInterfaceList
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|inf
init|=
name|netInterfaceList
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|InetAddress
argument_list|>
name|addrList
init|=
name|inf
operator|.
name|getInetAddresses
argument_list|()
decl_stmt|;
while|while
condition|(
name|addrList
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|InetAddress
name|addr
init|=
name|addrList
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|ipList
operator|.
name|add
argument_list|(
name|addr
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|ip
range|:
name|ipList
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"127.0.1.1,"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Local Ip addresses: "
operator|+
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getTestProvider
argument_list|()
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|superUserShortName
argument_list|)
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_MAX_LIFETIME_KEY
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_RENEW_INTERVAL_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|config
operator|.
name|setStrings
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getTestProvider
argument_list|()
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER
argument_list|)
argument_list|,
literal|"group1"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|configureSuperUserIPAddresses
argument_list|(
name|config
argument_list|,
name|REAL_USER
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|config
argument_list|,
literal|"hdfs://localhost:"
operator|+
literal|"0"
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER
argument_list|)
expr_stmt|;
name|proxyUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER
argument_list|,
name|ugi
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testDelegationTokenWithRealUser ()
specifier|public
name|void
name|testDelegationTokenWithRealUser
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|tokens
init|=
name|proxyUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|addDelegationTokens
argument_list|(
literal|"RenewerUser"
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|)
empty_stmt|;
name|DelegationTokenIdentifier
name|identifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|tokens
index|[
literal|0
index|]
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|identifier
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|,
name|PROXY_USER
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|identifier
operator|.
name|getUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|,
name|REAL_USER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//Do Nothing
block|}
block|}
end_class

begin_function
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testWebHdfsDoAs ()
specifier|public
name|void
name|testWebHdfsDoAs
parameter_list|()
throws|throws
name|Exception
block|{
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"START: testWebHdfsDoAs()"
argument_list|)
expr_stmt|;
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"ugi.getShortUserName()="
operator|+
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|ugi
argument_list|,
name|config
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|setPermission
argument_list|(
name|root
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|webhdfs
argument_list|,
literal|"ugi"
argument_list|,
name|proxyUgi
argument_list|)
expr_stmt|;
block|{
name|Path
name|responsePath
init|=
name|webhdfs
operator|.
name|getHomeDirectory
argument_list|()
decl_stmt|;
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"responsePath="
operator|+
name|responsePath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|webhdfs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/user/"
operator|+
name|PROXY_USER
argument_list|,
name|responsePath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/testWebHdfsDoAs/a.txt"
argument_list|)
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|webhdfs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"Hello, webhdfs user!"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FileStatus
name|status
init|=
name|webhdfs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"status.getOwner()="
operator|+
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PROXY_USER
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|webhdfs
operator|.
name|append
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\nHello again!"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FileStatus
name|status
init|=
name|webhdfs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"status.getOwner()="
operator|+
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|WebHdfsTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"status.getLen()  ="
operator|+
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PROXY_USER
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

