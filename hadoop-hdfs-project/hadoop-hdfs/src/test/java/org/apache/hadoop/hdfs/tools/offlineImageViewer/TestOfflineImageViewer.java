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
name|BufferedReader
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|FileReader
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|LinkedList
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
name|*
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
import|;
end_import

begin_comment
comment|/**  * Test function of OfflineImageViewer by:  *   * confirming it can correctly process a valid fsimage file and that  *     the processing generates a correct representation of the namespace  *   * confirming it correctly fails to process an fsimage file with a layout  *     version it shouldn't be able to handle  *   * confirm it correctly bails on malformed image files, in particular, a  *     file that ends suddenly.  */
end_comment

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
name|OfflineImageViewer
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
class|class
name|LsElements
block|{
DECL|field|perms
specifier|public
name|String
name|perms
decl_stmt|;
DECL|field|replication
specifier|public
name|int
name|replication
decl_stmt|;
DECL|field|username
specifier|public
name|String
name|username
decl_stmt|;
DECL|field|groupname
specifier|public
name|String
name|groupname
decl_stmt|;
DECL|field|filesize
specifier|public
name|long
name|filesize
decl_stmt|;
DECL|field|dir
specifier|public
name|char
name|dir
decl_stmt|;
comment|// d if dir, - otherwise
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
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ROOT
specifier|private
specifier|static
name|String
name|ROOT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
comment|// Create a populated namespace for later testing.  Save its contents to a
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
name|HdfsConfiguration
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
literal|4
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
name|FileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|int
name|filesize
init|=
literal|256
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
operator|new
name|byte
index|[
name|filesize
operator|++
index|]
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
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|delegationTokens
init|=
name|hdfs
operator|.
name|getDelegationTokens
argument_list|(
name|TEST_RENEWER
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
comment|// Write results to the fsimage file
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
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
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/basicCheck"
argument_list|)
decl_stmt|;
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/basicCheckOutput"
argument_list|)
decl_stmt|;
try|try
block|{
name|copyFile
argument_list|(
name|originalFsimage
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|ImageVisitor
name|v
init|=
operator|new
name|LsImageVisitor
argument_list|(
name|outputFile
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|OfflineImageViewer
name|oiv
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|testFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|v
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|oiv
operator|.
name|go
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
name|fileOutput
init|=
name|readLsfile
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
name|compareNamespaces
argument_list|(
name|writtenFiles
argument_list|,
name|fileOutput
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|testFile
operator|.
name|exists
argument_list|()
condition|)
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputFile
operator|.
name|exists
argument_list|()
condition|)
name|outputFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Correctly generated ls-style output."
argument_list|)
expr_stmt|;
block|}
comment|// Confirm that attempting to read an fsimage file with an unsupported
comment|// layout results in an error
annotation|@
name|Test
DECL|method|unsupportedFSLayoutVersion ()
specifier|public
name|void
name|unsupportedFSLayoutVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/invalidLayoutVersion"
argument_list|)
decl_stmt|;
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"invalidLayoutVersionOutput"
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|badVersionNum
init|=
operator|-
literal|432
decl_stmt|;
name|changeLayoutVersion
argument_list|(
name|originalFsimage
argument_list|,
name|testFile
argument_list|,
name|badVersionNum
argument_list|)
expr_stmt|;
name|ImageVisitor
name|v
init|=
operator|new
name|LsImageVisitor
argument_list|(
name|outputFile
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|OfflineImageViewer
name|oiv
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|testFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|v
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|oiv
operator|.
name|go
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to read invalid laytout version"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|badVersionNum
argument_list|)
argument_list|)
condition|)
throw|throw
name|e
throw|;
comment|// wasn't error we were expecting
name|LOG
operator|.
name|debug
argument_list|(
literal|"Correctly failed at reading bad image version."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testFile
operator|.
name|exists
argument_list|()
condition|)
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputFile
operator|.
name|exists
argument_list|()
condition|)
name|outputFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Verify that image viewer will bail on a file that ends unexpectedly
annotation|@
name|Test
DECL|method|truncatedFSImage ()
specifier|public
name|void
name|truncatedFSImage
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/truncatedFSImage"
argument_list|)
decl_stmt|;
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/trucnatedFSImageOutput"
argument_list|)
decl_stmt|;
try|try
block|{
name|copyPartOfFile
argument_list|(
name|originalFsimage
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Created truncated fsimage"
argument_list|,
name|testFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|ImageVisitor
name|v
init|=
operator|new
name|LsImageVisitor
argument_list|(
name|outputFile
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|OfflineImageViewer
name|oiv
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|testFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|v
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|oiv
operator|.
name|go
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Managed to process a truncated fsimage file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Correctly handled EOF"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testFile
operator|.
name|exists
argument_list|()
condition|)
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputFile
operator|.
name|exists
argument_list|()
condition|)
name|outputFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Test that our ls file has all the same compenents of the original namespace
DECL|method|compareNamespaces (HashMap<String, FileStatus> written, HashMap<String, LsElements> fileOutput)
specifier|private
name|void
name|compareNamespaces
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileStatus
argument_list|>
name|written
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
name|fileOutput
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Should be the same number of files in both, plus one for root"
operator|+
literal|" in fileoutput"
argument_list|,
name|fileOutput
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|written
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|inFile
init|=
name|fileOutput
operator|.
name|keySet
argument_list|()
decl_stmt|;
comment|// For each line in the output file, verify that the namespace had a
comment|// filestatus counterpart
for|for
control|(
name|String
name|path
range|:
name|inFile
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
comment|// root's not included in output from system call
continue|continue;
name|assertTrue
argument_list|(
literal|"Path in file ("
operator|+
name|path
operator|+
literal|") was written to fs"
argument_list|,
name|written
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|compareFiles
argument_list|(
name|written
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|fileOutput
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|written
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"No more files were written to fs"
argument_list|,
literal|0
argument_list|,
name|written
operator|.
name|size
argument_list|()
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
condition|?
literal|'d'
else|:
literal|'-'
argument_list|,
name|elements
operator|.
name|dir
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
comment|// Read the contents of the file created by the Ls processor
DECL|method|readLsfile (File lsFile)
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
name|readLsfile
parameter_list|(
name|File
name|lsFile
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|lsFile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
name|fileContents
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|readLsLine
argument_list|(
name|line
argument_list|,
name|fileContents
argument_list|)
expr_stmt|;
return|return
name|fileContents
return|;
block|}
comment|// Parse a line from the ls output.  Store permissions, replication,
comment|// username, groupname and filesize in hashmap keyed to the path name
DECL|method|readLsLine (String line, HashMap<String, LsElements> fileContents)
specifier|private
name|void
name|readLsLine
parameter_list|(
name|String
name|line
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|LsElements
argument_list|>
name|fileContents
parameter_list|)
block|{
name|String
name|elements
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Not enough elements in ls output"
argument_list|,
literal|8
argument_list|,
name|elements
operator|.
name|length
argument_list|)
expr_stmt|;
name|LsElements
name|lsLine
init|=
operator|new
name|LsElements
argument_list|()
decl_stmt|;
name|lsLine
operator|.
name|dir
operator|=
name|elements
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lsLine
operator|.
name|perms
operator|=
name|elements
index|[
literal|0
index|]
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|lsLine
operator|.
name|replication
operator|=
name|elements
index|[
literal|1
index|]
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
name|valueOf
argument_list|(
name|elements
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|lsLine
operator|.
name|username
operator|=
name|elements
index|[
literal|2
index|]
expr_stmt|;
name|lsLine
operator|.
name|groupname
operator|=
name|elements
index|[
literal|3
index|]
expr_stmt|;
name|lsLine
operator|.
name|filesize
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|elements
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
comment|// skipping date and time
name|String
name|path
init|=
name|elements
index|[
literal|7
index|]
decl_stmt|;
comment|// Check that each file in the ls output was listed once
name|assertFalse
argument_list|(
literal|"LS file had duplicate file entries"
argument_list|,
name|fileContents
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|fileContents
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|lsLine
argument_list|)
expr_stmt|;
block|}
comment|// Copy one fsimage to another, changing the layout version in the process
DECL|method|changeLayoutVersion (File src, File dest, int newVersion)
specifier|private
name|void
name|changeLayoutVersion
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dest
parameter_list|,
name|int
name|newVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|DataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|newVersion
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Only copy part of file into the other.  Used for testing truncated fsimage
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
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|int
name|bytesWritten
init|=
literal|0
decl_stmt|;
name|int
name|count
decl_stmt|;
name|int
name|maxBytes
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
while|while
condition|(
operator|(
name|count
operator|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|)
operator|>
literal|0
operator|&&
name|bytesWritten
operator|<
name|maxBytes
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|bytesWritten
operator|+=
name|count
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Copy one file's contents into the other
DECL|method|copyFile (File src, File dest)
specifier|private
name|void
name|copyFile
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
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|OutputStream
name|out
init|=
literal|null
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
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|outputOfFileDistributionVisitor ()
specifier|public
name|void
name|outputOfFileDistributionVisitor
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/basicCheck"
argument_list|)
decl_stmt|;
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/fileDistributionCheckOutput"
argument_list|)
decl_stmt|;
name|int
name|totalFiles
init|=
literal|0
decl_stmt|;
try|try
block|{
name|copyFile
argument_list|(
name|originalFsimage
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|ImageVisitor
name|v
init|=
operator|new
name|FileDistributionVisitor
argument_list|(
name|outputFile
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|OfflineImageViewer
name|oiv
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|testFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|v
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|oiv
operator|.
name|go
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|outputFile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|line
argument_list|,
literal|"Size\tNumFiles"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|row
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|row
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|totalFiles
operator|+=
name|Integer
operator|.
name|parseInt
argument_list|(
name|row
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testFile
operator|.
name|exists
argument_list|()
condition|)
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputFile
operator|.
name|exists
argument_list|()
condition|)
name|outputFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|totalFiles
argument_list|,
name|NUM_DIRS
operator|*
name|FILES_PER_DIR
argument_list|)
expr_stmt|;
block|}
DECL|class|TestImageVisitor
specifier|private
specifier|static
class|class
name|TestImageVisitor
extends|extends
name|ImageVisitor
block|{
DECL|field|delegationTokenRenewers
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|delegationTokenRenewers
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TestImageVisitor ()
name|TestImageVisitor
parameter_list|()
block|{     }
DECL|method|getDelegationTokenRenewers ()
name|List
argument_list|<
name|String
argument_list|>
name|getDelegationTokenRenewers
parameter_list|()
block|{
return|return
name|delegationTokenRenewers
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|visit (ImageElement element, String value)
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
block|{
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_RENEWER
condition|)
block|{
name|delegationTokenRenewers
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, String value)
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
block|{     }
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
annotation|@
name|Test
DECL|method|outputOfTestVisitor ()
specifier|public
name|void
name|outputOfTestVisitor
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|ROOT
argument_list|,
literal|"/basicCheck"
argument_list|)
decl_stmt|;
try|try
block|{
name|copyFile
argument_list|(
name|originalFsimage
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|TestImageVisitor
name|v
init|=
operator|new
name|TestImageVisitor
argument_list|()
decl_stmt|;
name|OfflineImageViewer
name|oiv
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|testFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|v
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|oiv
operator|.
name|go
argument_list|()
expr_stmt|;
comment|// Validated stored delegation token identifiers.
name|List
argument_list|<
name|String
argument_list|>
name|dtrs
init|=
name|v
operator|.
name|getDelegationTokenRenewers
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dtrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_RENEWER
argument_list|,
name|dtrs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|testFile
operator|.
name|exists
argument_list|()
condition|)
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Passed TestVisitor validation."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

