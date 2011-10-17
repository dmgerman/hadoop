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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Arrays
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
name|Map
operator|.
name|Entry
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
name|Set
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirType
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
name|namenode
operator|.
name|FileJournalManager
operator|.
name|EditLogFile
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
name|FSImageStorageInspector
operator|.
name|FSImageFile
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
operator|.
name|NameNodeDirType
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
name|util
operator|.
name|MD5FileUtils
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
name|Lists
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
name|ImmutableList
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
name|Sets
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
name|io
operator|.
name|Files
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * Utility functions for testing fsimage storage.  */
end_comment

begin_class
DECL|class|FSImageTestUtil
specifier|public
specifier|abstract
class|class
name|FSImageTestUtil
block|{
comment|/**    * The position in the fsimage header where the txid is    * written.    */
DECL|field|IMAGE_TXID_POS
specifier|private
specifier|static
specifier|final
name|long
name|IMAGE_TXID_POS
init|=
literal|24
decl_stmt|;
comment|/**    * This function returns a md5 hash of a file.    *     * @param file input file    * @return The md5 string    */
DECL|method|getFileMD5 (File file)
specifier|public
specifier|static
name|String
name|getFileMD5
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MD5FileUtils
operator|.
name|computeMd5ForFile
argument_list|(
name|file
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Calculate the md5sum of an image after zeroing out the transaction ID    * field in the header. This is useful for tests that want to verify    * that two checkpoints have identical namespaces.    */
DECL|method|getImageFileMD5IgnoringTxId (File imageFile)
specifier|public
specifier|static
name|String
name|getImageFileMD5IgnoringTxId
parameter_list|(
name|File
name|imageFile
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"hadoop_imagefile_tmp"
argument_list|,
literal|"fsimage"
argument_list|)
decl_stmt|;
name|tmpFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|copy
argument_list|(
name|imageFile
argument_list|,
name|tmpFile
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|tmpFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|raf
operator|.
name|seek
argument_list|(
name|IMAGE_TXID_POS
argument_list|)
expr_stmt|;
name|raf
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|raf
argument_list|)
expr_stmt|;
block|}
return|return
name|getFileMD5
argument_list|(
name|tmpFile
argument_list|)
return|;
block|}
finally|finally
block|{
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mockStorageDirectory ( File currentDir, NameNodeDirType type)
specifier|public
specifier|static
name|StorageDirectory
name|mockStorageDirectory
parameter_list|(
name|File
name|currentDir
parameter_list|,
name|NameNodeDirType
name|type
parameter_list|)
block|{
comment|// Mock the StorageDirectory interface to just point to this file
name|StorageDirectory
name|sd
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|type
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getStorageDirType
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|currentDir
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getCurrentDir
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|currentDir
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|mockFile
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getVersionFile
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|mockFile
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getPreviousDir
argument_list|()
expr_stmt|;
return|return
name|sd
return|;
block|}
comment|/**    * Make a mock storage directory that returns some set of file contents.    * @param type type of storage dir    * @param previousExists should we mock that the previous/ dir exists?    * @param fileNames the names of files contained in current/    */
DECL|method|mockStorageDirectory ( StorageDirType type, boolean previousExists, String... fileNames)
specifier|static
name|StorageDirectory
name|mockStorageDirectory
parameter_list|(
name|StorageDirType
name|type
parameter_list|,
name|boolean
name|previousExists
parameter_list|,
name|String
modifier|...
name|fileNames
parameter_list|)
block|{
name|StorageDirectory
name|sd
init|=
name|mock
argument_list|(
name|StorageDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|type
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getStorageDirType
argument_list|()
expr_stmt|;
comment|// Version file should always exist
name|doReturn
argument_list|(
name|mockFile
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getVersionFile
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|mockFile
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getRoot
argument_list|()
expr_stmt|;
comment|// Previous dir optionally exists
name|doReturn
argument_list|(
name|mockFile
argument_list|(
name|previousExists
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getPreviousDir
argument_list|()
expr_stmt|;
comment|// Return a mock 'current' directory which has the given paths
name|File
index|[]
name|files
init|=
operator|new
name|File
index|[
name|fileNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|fileNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|File
name|mockDir
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|File
argument_list|(
literal|"/dir/current"
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|files
argument_list|)
operator|.
name|when
argument_list|(
name|mockDir
argument_list|)
operator|.
name|listFiles
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|mockDir
argument_list|)
operator|.
name|when
argument_list|(
name|sd
argument_list|)
operator|.
name|getCurrentDir
argument_list|()
expr_stmt|;
return|return
name|sd
return|;
block|}
DECL|method|mockFile (boolean exists)
specifier|static
name|File
name|mockFile
parameter_list|(
name|boolean
name|exists
parameter_list|)
block|{
name|File
name|mockFile
init|=
name|mock
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|exists
argument_list|)
operator|.
name|when
argument_list|(
name|mockFile
argument_list|)
operator|.
name|exists
argument_list|()
expr_stmt|;
return|return
name|mockFile
return|;
block|}
DECL|method|inspectStorageDirectory ( File dir, NameNodeDirType dirType)
specifier|public
specifier|static
name|FSImageTransactionalStorageInspector
name|inspectStorageDirectory
parameter_list|(
name|File
name|dir
parameter_list|,
name|NameNodeDirType
name|dirType
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|()
decl_stmt|;
name|inspector
operator|.
name|inspectDirectory
argument_list|(
name|mockStorageDirectory
argument_list|(
name|dir
argument_list|,
name|dirType
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|inspector
return|;
block|}
comment|/**    * Return a standalone instance of FSEditLog that will log into the given    * log directory. The returned instance is not yet opened.    */
DECL|method|createStandaloneEditLog (File logDir)
specifier|public
specifier|static
name|FSEditLog
name|createStandaloneEditLog
parameter_list|(
name|File
name|logDir
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|logDir
operator|.
name|mkdirs
argument_list|()
operator|||
name|logDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|deleteDirectoryContents
argument_list|(
name|logDir
argument_list|)
expr_stmt|;
name|NNStorage
name|storage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NNStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|FSImageTestUtil
operator|.
name|mockStorageDirectory
argument_list|(
name|logDir
argument_list|,
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StorageDirectory
argument_list|>
name|sds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|sds
argument_list|)
operator|.
name|when
argument_list|(
name|storage
argument_list|)
operator|.
name|dirIterable
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|sd
argument_list|)
operator|.
name|when
argument_list|(
name|storage
argument_list|)
operator|.
name|getStorageDirectory
argument_list|(
name|Matchers
operator|.
expr|<
name|URI
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|FSEditLog
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|storage
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|logDir
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Assert that all of the given directories have the same newest filename    * for fsimage that they hold the same data.    */
DECL|method|assertSameNewestImage (List<File> dirs)
specifier|public
specifier|static
name|void
name|assertSameNewestImage
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|dirs
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|dirs
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
return|return;
name|long
name|imageTxId
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|imageFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|dir
range|:
name|dirs
control|)
block|{
name|FSImageTransactionalStorageInspector
name|inspector
init|=
name|inspectStorageDirectory
argument_list|(
name|dir
argument_list|,
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
decl_stmt|;
name|FSImageFile
name|latestImage
init|=
name|inspector
operator|.
name|getLatestImage
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No image in "
operator|+
name|dir
argument_list|,
name|latestImage
argument_list|)
expr_stmt|;
name|long
name|thisTxId
init|=
name|latestImage
operator|.
name|getCheckpointTxId
argument_list|()
decl_stmt|;
if|if
condition|(
name|imageTxId
operator|!=
operator|-
literal|1
operator|&&
name|thisTxId
operator|!=
name|imageTxId
condition|)
block|{
name|fail
argument_list|(
literal|"Storage directory "
operator|+
name|dir
operator|+
literal|" does not have the same "
operator|+
literal|"last image index "
operator|+
name|imageTxId
operator|+
literal|" as another"
argument_list|)
expr_stmt|;
block|}
name|imageTxId
operator|=
name|thisTxId
expr_stmt|;
name|imageFiles
operator|.
name|add
argument_list|(
name|inspector
operator|.
name|getLatestImage
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFileContentsSame
argument_list|(
name|imageFiles
operator|.
name|toArray
argument_list|(
operator|new
name|File
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a list of directories, assert that any files that are named    * the same thing have the same contents. For example, if a file    * named "fsimage_1" shows up in more than one directory, then it must    * be the same.    * @throws Exception     */
DECL|method|assertParallelFilesAreIdentical (List<File> dirs, Set<String> ignoredFileNames)
specifier|public
specifier|static
name|void
name|assertParallelFilesAreIdentical
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|dirs
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|ignoredFileNames
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|File
argument_list|>
argument_list|>
name|groupedByName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|File
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|dir
range|:
name|dirs
control|)
block|{
for|for
control|(
name|File
name|f
range|:
name|dir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|ignoredFileNames
operator|.
name|contains
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|List
argument_list|<
name|File
argument_list|>
name|fileList
init|=
name|groupedByName
operator|.
name|get
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileList
operator|==
literal|null
condition|)
block|{
name|fileList
operator|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
expr_stmt|;
name|groupedByName
operator|.
name|put
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|fileList
argument_list|)
expr_stmt|;
block|}
name|fileList
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|List
argument_list|<
name|File
argument_list|>
name|sameNameList
range|:
name|groupedByName
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|sameNameList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// recurse
name|assertParallelFilesAreIdentical
argument_list|(
name|sameNameList
argument_list|,
name|ignoredFileNames
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"VERSION"
operator|.
name|equals
argument_list|(
name|sameNameList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertPropertiesFilesSame
argument_list|(
name|sameNameList
operator|.
name|toArray
argument_list|(
operator|new
name|File
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFileContentsSame
argument_list|(
name|sameNameList
operator|.
name|toArray
argument_list|(
operator|new
name|File
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Assert that a set of properties files all contain the same data.    * We cannot simply check the md5sums here, since Properties files    * contain timestamps -- thus, two properties files from the same    * saveNamespace operation may actually differ in md5sum.    * @param propFiles the files to compare    * @throws IOException if the files cannot be opened or read    * @throws AssertionError if the files differ    */
DECL|method|assertPropertiesFilesSame (File[] propFiles)
specifier|public
specifier|static
name|void
name|assertPropertiesFilesSame
parameter_list|(
name|File
index|[]
name|propFiles
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
name|prevProps
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|propFiles
control|)
block|{
name|Properties
name|props
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prevProps
operator|==
literal|null
condition|)
block|{
name|prevProps
operator|=
name|props
operator|.
name|entrySet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|>
name|diff
init|=
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|prevProps
argument_list|,
name|props
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Properties file "
operator|+
name|f
operator|+
literal|" differs from "
operator|+
name|propFiles
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Assert that all of the given paths have the exact same    * contents     */
DECL|method|assertFileContentsSame (File... files)
specifier|public
specifier|static
name|void
name|assertFileContentsSame
parameter_list|(
name|File
modifier|...
name|files
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|files
operator|.
name|length
operator|<
literal|2
condition|)
return|return;
name|Map
argument_list|<
name|File
argument_list|,
name|String
argument_list|>
name|md5s
init|=
name|getFileMD5s
argument_list|(
name|files
argument_list|)
decl_stmt|;
if|if
condition|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|md5s
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|fail
argument_list|(
literal|"File contents differed:\n  "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"\n  "
argument_list|)
operator|.
name|withKeyValueSeparator
argument_list|(
literal|"="
argument_list|)
operator|.
name|join
argument_list|(
name|md5s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert that the given files are not all the same, and in fact that    * they have<code>expectedUniqueHashes</code> unique contents.    */
DECL|method|assertFileContentsDifferent ( int expectedUniqueHashes, File... files)
specifier|public
specifier|static
name|void
name|assertFileContentsDifferent
parameter_list|(
name|int
name|expectedUniqueHashes
parameter_list|,
name|File
modifier|...
name|files
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|File
argument_list|,
name|String
argument_list|>
name|md5s
init|=
name|getFileMD5s
argument_list|(
name|files
argument_list|)
decl_stmt|;
if|if
condition|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|md5s
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
operator|!=
name|expectedUniqueHashes
condition|)
block|{
name|fail
argument_list|(
literal|"Expected "
operator|+
name|expectedUniqueHashes
operator|+
literal|" different hashes, got:\n  "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"\n  "
argument_list|)
operator|.
name|withKeyValueSeparator
argument_list|(
literal|"="
argument_list|)
operator|.
name|join
argument_list|(
name|md5s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFileMD5s (File... files)
specifier|public
specifier|static
name|Map
argument_list|<
name|File
argument_list|,
name|String
argument_list|>
name|getFileMD5s
parameter_list|(
name|File
modifier|...
name|files
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|File
argument_list|,
name|String
argument_list|>
name|ret
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|assertTrue
argument_list|(
literal|"Must exist: "
operator|+
name|f
argument_list|,
name|f
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|f
argument_list|,
name|getFileMD5
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * @return a List which contains the "current" dir for each storage    * directory of the given type.     */
DECL|method|getCurrentDirs (NNStorage storage, NameNodeDirType type)
specifier|public
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getCurrentDirs
parameter_list|(
name|NNStorage
name|storage
parameter_list|,
name|NameNodeDirType
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|ret
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|storage
operator|.
name|dirIterable
argument_list|(
name|type
argument_list|)
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * @return the fsimage file with the most recent transaction ID in the    * given storage directory.    */
DECL|method|findLatestImageFile (StorageDirectory sd)
specifier|public
specifier|static
name|File
name|findLatestImageFile
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|()
decl_stmt|;
name|inspector
operator|.
name|inspectDirectory
argument_list|(
name|sd
argument_list|)
expr_stmt|;
return|return
name|inspector
operator|.
name|getLatestImage
argument_list|()
operator|.
name|getFile
argument_list|()
return|;
block|}
comment|/**    * @return the fsimage file with the most recent transaction ID in the    * given 'current/' directory.    */
DECL|method|findNewestImageFile (String currentDirPath)
specifier|public
specifier|static
name|File
name|findNewestImageFile
parameter_list|(
name|String
name|currentDirPath
parameter_list|)
throws|throws
name|IOException
block|{
name|StorageDirectory
name|sd
init|=
name|FSImageTestUtil
operator|.
name|mockStorageDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|currentDirPath
argument_list|)
argument_list|,
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
decl_stmt|;
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|()
decl_stmt|;
name|inspector
operator|.
name|inspectDirectory
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|FSImageFile
name|latestImage
init|=
name|inspector
operator|.
name|getLatestImage
argument_list|()
decl_stmt|;
return|return
operator|(
name|latestImage
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|latestImage
operator|.
name|getFile
argument_list|()
return|;
block|}
comment|/**    * Assert that the NameNode has checkpoints at the expected    * transaction IDs.    */
DECL|method|assertNNHasCheckpoints (MiniDFSCluster cluster, List<Integer> txids)
specifier|static
name|void
name|assertNNHasCheckpoints
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|txids
parameter_list|)
block|{
for|for
control|(
name|File
name|nameDir
range|:
name|getNameNodeCurrentDirs
argument_list|(
name|cluster
argument_list|)
control|)
block|{
comment|// Should have fsimage_N for the three checkpoints
for|for
control|(
name|long
name|checkpointTxId
range|:
name|txids
control|)
block|{
name|File
name|image
init|=
operator|new
name|File
argument_list|(
name|nameDir
argument_list|,
name|NNStorage
operator|.
name|getImageFileName
argument_list|(
name|checkpointTxId
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected non-empty "
operator|+
name|image
argument_list|,
name|image
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getNameNodeCurrentDirs (MiniDFSCluster cluster)
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getNameNodeCurrentDirs
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|nameDirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|URI
name|u
range|:
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|0
argument_list|)
control|)
block|{
name|nameDirs
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"current"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nameDirs
return|;
block|}
comment|/**    * @return the latest edits log, finalized or otherwise, from the given    * storage directory.    */
DECL|method|findLatestEditsLog (StorageDirectory sd)
specifier|public
specifier|static
name|EditLogFile
name|findLatestEditsLog
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|currentDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|foundEditLogs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|FileJournalManager
operator|.
name|matchEditLogs
argument_list|(
name|currentDir
operator|.
name|listFiles
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|max
argument_list|(
name|foundEditLogs
argument_list|,
name|EditLogFile
operator|.
name|COMPARE_BY_START_TXID
argument_list|)
return|;
block|}
comment|/**    * Corrupt the given VERSION file by replacing a given    * key with a new value and re-writing the file.    *     * @param versionFile the VERSION file to corrupt    * @param key the key to replace    * @param value the new value for this key    */
DECL|method|corruptVersionFile (File versionFile, String key, String value)
specifier|public
specifier|static
name|void
name|corruptVersionFile
parameter_list|(
name|File
name|versionFile
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|versionFile
argument_list|)
decl_stmt|;
name|FileOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|versionFile
argument_list|)
expr_stmt|;
name|props
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|null
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
name|fis
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertReasonableNameCurrentDir (File curDir)
specifier|public
specifier|static
name|void
name|assertReasonableNameCurrentDir
parameter_list|(
name|File
name|curDir
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|curDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|curDir
argument_list|,
literal|"VERSION"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|curDir
argument_list|,
literal|"seen_txid"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|image
init|=
name|findNewestImageFile
argument_list|(
name|curDir
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|image
argument_list|)
expr_stmt|;
block|}
DECL|method|logStorageContents (Log LOG, NNStorage storage)
specifier|public
specifier|static
name|void
name|logStorageContents
parameter_list|(
name|Log
name|LOG
parameter_list|,
name|NNStorage
name|storage
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"current storages and corresponding sizes:"
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|storage
operator|.
name|dirIterable
argument_list|(
literal|null
argument_list|)
control|)
block|{
name|File
name|curDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"In directory "
operator|+
name|curDir
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|curDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|files
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"  file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"; len = "
operator|+
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** get the fsImage*/
DECL|method|getFSImage (NameNode node)
specifier|public
specifier|static
name|FSImage
name|getFSImage
parameter_list|(
name|NameNode
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getFSImage
argument_list|()
return|;
block|}
comment|/**    * get NameSpace quota.    */
DECL|method|getNSQuota (FSNamesystem ns)
specifier|public
specifier|static
name|long
name|getNSQuota
parameter_list|(
name|FSNamesystem
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|.
name|dir
operator|.
name|rootDir
operator|.
name|getNsQuota
argument_list|()
return|;
block|}
block|}
end_class

end_unit

