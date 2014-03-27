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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

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
name|FileOutputStream
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
name|PrintWriter
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|DistributedFileSystem
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|FSImageTestUtil
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
name|IOUtils
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
name|net
operator|.
name|NetUtils
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
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|type
operator|.
name|TypeReference
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
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
DECL|class|TestOfflineImageViewer
specifier|public
class|class
name|TestOfflineImageViewer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|OfflineImageViewerPB
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_DIRS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DIRS
init|=
literal|3
decl_stmt|;
DECL|field|FILES_PER_DIR
specifier|private
specifier|static
specifier|final
name|int
name|FILES_PER_DIR
init|=
literal|4
decl_stmt|;
DECL|field|TEST_RENEWER
specifier|private
specifier|static
specifier|final
name|String
name|TEST_RENEWER
init|=
literal|"JobTracker"
decl_stmt|;
DECL|field|originalFsimage
specifier|private
specifier|static
name|File
name|originalFsimage
init|=
literal|null
decl_stmt|;
comment|// Elements of lines of ls-file output to be compared to FileStatus instance
DECL|class|LsElements
specifier|private
specifier|static
specifier|final
class|class
name|LsElements
block|{
DECL|field|perms
specifier|private
name|String
name|perms
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
decl_stmt|;
DECL|field|username
specifier|private
name|String
name|username
decl_stmt|;
DECL|field|groupname
specifier|private
name|String
name|groupname
decl_stmt|;
DECL|field|filesize
specifier|private
name|long
name|filesize
decl_stmt|;
DECL|field|isDir
specifier|private
name|boolean
name|isDir
decl_stmt|;
block|}
comment|// namespace as written to dfs, to be compared with viewer's output
DECL|field|writtenFiles
specifier|final
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileStatus
argument_list|>
name|writtenFiles
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
comment|// Create a populated namespace for later testing. Save its contents to a
comment|// data structure and store its fsimage location.
comment|// We only want to generate the fsimage file once and use it for
comment|// multiple tests.
annotation|@
name|BeforeClass
DECL|method|createOriginalFSImage ()
specifier|public
specifier|static
name|void
name|createOriginalFSImage
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
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
name|conf
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
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTH_TO_LOCAL
argument_list|,
literal|"RULE:[2:$1@$0](JobTracker@.*FOO.COM)s/@.*//"
operator|+
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
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
name|DistributedFileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create a reasonable namespace
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_DIRS
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
operator|+
name|i
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|writtenFiles
operator|.
name|put
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|,
name|pathToFileEntry
argument_list|(
name|hdfs
argument_list|,
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|FILES_PER_DIR
condition|;
name|j
operator|++
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
operator|+
name|j
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|o
operator|.
name|write
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|writtenFiles
operator|.
name|put
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|pathToFileEntry
argument_list|(
name|hdfs
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Get delegation tokens so we log the delegation token op
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|delegationTokens
init|=
name|hdfs
operator|.
name|addDelegationTokens
argument_list|(
name|TEST_RENEWER
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|t
range|:
name|delegationTokens
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"got token "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|snapshot
init|=
operator|new
name|Path
argument_list|(
literal|"/snapshot"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/snapshot/1"
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|snapshot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Write results to the fsimage file
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
comment|// Determine location of fsimage file
name|originalFsimage
operator|=
name|FSImageTestUtil
operator|.
name|findLatestImageFile
argument_list|(
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|originalFsimage
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Didn't generate or can't find fsimage"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"original FS image file is "
operator|+
name|originalFsimage
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|deleteOriginalFSImage ()
specifier|public
specifier|static
name|void
name|deleteOriginalFSImage
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|originalFsimage
operator|!=
literal|null
operator|&&
name|originalFsimage
operator|.
name|exists
argument_list|()
condition|)
block|{
name|originalFsimage
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Convenience method to generate a file status from file system for
comment|// later comparison
DECL|method|pathToFileEntry (FileSystem hdfs, String file)
specifier|private
specifier|static
name|FileStatus
name|pathToFileEntry
parameter_list|(
name|FileSystem
name|hdfs
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hdfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|)
return|;
block|}
comment|// Verify that we can correctly generate an ls-style output for a valid
comment|// fsimage
annotation|@
name|Test
DECL|method|outputOfLSVisitor ()
specifier|public
name|void
name|outputOfLSVisitor
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|output
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|LsrPBImage
name|v
init|=
operator|new
name|LsrPBImage
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|out
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
operator|new
name|RandomAccessFile
argument_list|(
name|originalFsimage
argument_list|,
literal|"r"
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([d\\-])([rwx\\-]{9})\\s*(-|\\d+)\\s*(\\w+)\\s*(\\w+)\\s*(\\d+)\\s*(\\d+)\\s*([\b/]+)"
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|output
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|LsElements
name|e
init|=
operator|new
name|LsElements
argument_list|()
decl_stmt|;
name|e
operator|.
name|isDir
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|e
operator|.
name|perms
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|e
operator|.
name|replication
operator|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|?
literal|0
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|username
operator|=
name|m
operator|.
name|group
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|e
operator|.
name|groupname
operator|=
name|m
operator|.
name|group
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|e
operator|.
name|filesize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|m
operator|.
name|group
argument_list|(
literal|8
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|compareFiles
argument_list|(
name|writtenFiles
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
operator|++
name|count
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|writtenFiles
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testTruncatedFSImage ()
specifier|public
name|void
name|testTruncatedFSImage
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|truncatedFile
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|StringWriter
name|output
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|copyPartOfFile
argument_list|(
name|originalFsimage
argument_list|,
name|truncatedFile
argument_list|)
expr_stmt|;
operator|new
name|FileDistributionCalculator
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|PrintWriter
argument_list|(
name|output
argument_list|)
argument_list|)
operator|.
name|visit
argument_list|(
operator|new
name|RandomAccessFile
argument_list|(
name|truncatedFile
argument_list|,
literal|"r"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Compare two files as listed in the original namespace FileStatus and
comment|// the output of the ls file from the image processor
DECL|method|compareFiles (FileStatus fs, LsElements elements)
specifier|private
name|void
name|compareFiles
parameter_list|(
name|FileStatus
name|fs
parameter_list|,
name|LsElements
name|elements
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"directory listed as such"
argument_list|,
name|fs
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|elements
operator|.
name|isDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"perms string equal"
argument_list|,
name|fs
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|elements
operator|.
name|perms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"replication equal"
argument_list|,
name|fs
operator|.
name|getReplication
argument_list|()
argument_list|,
name|elements
operator|.
name|replication
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"owner equal"
argument_list|,
name|fs
operator|.
name|getOwner
argument_list|()
argument_list|,
name|elements
operator|.
name|username
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"group equal"
argument_list|,
name|fs
operator|.
name|getGroup
argument_list|()
argument_list|,
name|elements
operator|.
name|groupname
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lengths equal"
argument_list|,
name|fs
operator|.
name|getLen
argument_list|()
argument_list|,
name|elements
operator|.
name|filesize
argument_list|)
expr_stmt|;
block|}
DECL|method|copyPartOfFile (File src, File dest)
specifier|private
name|void
name|copyPartOfFile
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|in
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|out
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|MAX_BYTES
init|=
literal|700
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|in
operator|.
name|getChannel
argument_list|()
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|MAX_BYTES
argument_list|,
name|out
operator|.
name|getChannel
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFileDistributionCalculator ()
specifier|public
name|void
name|testFileDistributionCalculator
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|output
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|o
init|=
operator|new
name|PrintWriter
argument_list|(
name|output
argument_list|)
decl_stmt|;
operator|new
name|FileDistributionCalculator
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
operator|.
name|visit
argument_list|(
operator|new
name|RandomAccessFile
argument_list|(
name|originalFsimage
argument_list|,
literal|"r"
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"totalFiles = (\\d+)\n"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|p
operator|.
name|matcher
argument_list|(
name|output
operator|.
name|getBuffer
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
operator|&&
name|matcher
operator|.
name|groupCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|int
name|totalFiles
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DIRS
operator|*
name|FILES_PER_DIR
argument_list|,
name|totalFiles
argument_list|)
expr_stmt|;
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"totalDirectories = (\\d+)\n"
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|output
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
operator|&&
name|matcher
operator|.
name|groupCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|int
name|totalDirs
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// totalDirs includes root directory
name|assertEquals
argument_list|(
name|NUM_DIRS
operator|+
literal|1
argument_list|,
name|totalDirs
argument_list|)
expr_stmt|;
name|FileStatus
name|maxFile
init|=
name|Collections
operator|.
name|max
argument_list|(
name|writtenFiles
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FileStatus
name|first
parameter_list|,
name|FileStatus
name|second
parameter_list|)
block|{
return|return
name|first
operator|.
name|getLen
argument_list|()
operator|<
name|second
operator|.
name|getLen
argument_list|()
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|first
operator|.
name|getLen
argument_list|()
operator|==
name|second
operator|.
name|getLen
argument_list|()
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"maxFileSize = (\\d+)\n"
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|output
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
operator|&&
name|matcher
operator|.
name|groupCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxFile
operator|.
name|getLen
argument_list|()
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileDistributionCalculatorWithOptions ()
specifier|public
name|void
name|testFileDistributionCalculatorWithOptions
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|status
init|=
name|OfflineImageViewerPB
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-i"
block|,
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-o"
block|,
literal|"-"
block|,
literal|"-p"
block|,
literal|"FileDistribution"
block|,
literal|"-maxSize"
block|,
literal|"512"
block|,
literal|"-step"
block|,
literal|"8"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPBImageXmlWriter ()
specifier|public
name|void
name|testPBImageXmlWriter
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
block|{
name|StringWriter
name|output
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|o
init|=
operator|new
name|PrintWriter
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|PBImageXmlWriter
name|v
init|=
operator|new
name|PBImageXmlWriter
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|o
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
operator|new
name|RandomAccessFile
argument_list|(
name|originalFsimage
argument_list|,
literal|"r"
argument_list|)
argument_list|)
expr_stmt|;
name|SAXParserFactory
name|spf
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|SAXParser
name|parser
init|=
name|spf
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|String
name|xml
init|=
name|output
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWebImageViewer ()
specifier|public
name|void
name|testWebImageViewer
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
comment|// 1. LISTSTATUS operation to a valid path
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/?op=LISTSTATUS"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/json"
argument_list|,
name|connection
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|toString
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"content: "
operator|+
name|content
argument_list|)
expr_stmt|;
comment|// verify the number of directories listed
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|fileStatuses
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|content
argument_list|,
operator|new
name|TypeReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fileStatusList
init|=
name|fileStatuses
operator|.
name|get
argument_list|(
literal|"FileStatuses"
argument_list|)
operator|.
name|get
argument_list|(
literal|"FileStatus"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DIRS
argument_list|,
name|fileStatusList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the number of files in a directory
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fileStatusMap
init|=
name|fileStatusList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|FILES_PER_DIR
argument_list|,
name|fileStatusMap
operator|.
name|get
argument_list|(
literal|"childrenNum"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2. LISTSTATUS operation to a invalid path
name|url
operator|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/invalid/?op=LISTSTATUS"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3. invalid operation
name|url
operator|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/?op=INVALID"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// 4. invalid method
name|url
operator|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/?op=LISTSTATUS"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_BAD_METHOD
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// shutdown the viewer
name|viewer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

