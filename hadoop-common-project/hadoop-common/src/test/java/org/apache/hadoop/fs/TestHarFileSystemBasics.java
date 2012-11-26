begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|IOException
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
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
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

begin_comment
comment|/**  * This test class checks basic operations with {@link HarFileSystem} including  * various initialization cases, getters, and modification methods.  *   * NB: to run this test from an IDE make sure the folder  * "hadoop-common-project/hadoop-common/src/main/resources/" is added as a  * source path. This will allow the system to pick up the "core-default.xml" and  * "META-INF/services/..." resources from the class-path in the runtime.  */
end_comment

begin_class
DECL|class|TestHarFileSystemBasics
specifier|public
class|class
name|TestHarFileSystemBasics
block|{
DECL|field|ROOT_PATH
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_PATH
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
DECL|field|rootPath
specifier|private
specifier|static
specifier|final
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
operator|new
name|File
argument_list|(
name|ROOT_PATH
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/localfs"
argument_list|)
decl_stmt|;
comment|// NB: .har suffix is necessary
DECL|field|harPath
specifier|private
specifier|static
specifier|final
name|Path
name|harPath
init|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"path1/path2/my.har"
argument_list|)
decl_stmt|;
DECL|field|localFileSystem
specifier|private
name|FileSystem
name|localFileSystem
decl_stmt|;
DECL|field|harFileSystem
specifier|private
name|HarFileSystem
name|harFileSystem
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/*    * creates and returns fully initialized HarFileSystem    */
DECL|method|createHarFileSysten (final Configuration conf)
specifier|private
name|HarFileSystem
name|createHarFileSysten
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|localFileSystem
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|localFileSystem
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"file:///"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|localFileSystem
operator|.
name|mkdirs
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|localFileSystem
operator|.
name|mkdirs
argument_list|(
name|harPath
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|indexPath
init|=
operator|new
name|Path
argument_list|(
name|harPath
argument_list|,
literal|"_index"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|masterIndexPath
init|=
operator|new
name|Path
argument_list|(
name|harPath
argument_list|,
literal|"_masterindex"
argument_list|)
decl_stmt|;
name|localFileSystem
operator|.
name|createNewFile
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localFileSystem
operator|.
name|exists
argument_list|(
name|indexPath
argument_list|)
argument_list|)
expr_stmt|;
name|localFileSystem
operator|.
name|createNewFile
argument_list|(
name|masterIndexPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localFileSystem
operator|.
name|exists
argument_list|(
name|masterIndexPath
argument_list|)
argument_list|)
expr_stmt|;
name|writeVersionToMasterIndexImpl
argument_list|(
name|HarFileSystem
operator|.
name|VERSION
argument_list|)
expr_stmt|;
specifier|final
name|HarFileSystem
name|harFileSystem
init|=
operator|new
name|HarFileSystem
argument_list|(
name|localFileSystem
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"har://"
operator|+
name|harPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|harFileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|harFileSystem
return|;
block|}
DECL|method|writeVersionToMasterIndexImpl (int version)
specifier|private
name|void
name|writeVersionToMasterIndexImpl
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|masterIndexPath
init|=
operator|new
name|Path
argument_list|(
name|harPath
argument_list|,
literal|"_masterindex"
argument_list|)
decl_stmt|;
comment|// write Har version into the master index:
specifier|final
name|FSDataOutputStream
name|fsdos
init|=
name|localFileSystem
operator|.
name|create
argument_list|(
name|masterIndexPath
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|versionString
init|=
name|version
operator|+
literal|"\n"
decl_stmt|;
name|fsdos
operator|.
name|write
argument_list|(
name|versionString
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|fsdos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fsdos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|rootDirIoFile
init|=
operator|new
name|File
argument_list|(
name|rootPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|rootDirIoFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|rootDirIoFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create temp directory ["
operator|+
name|rootDirIoFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// create Har to test:
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|harFileSystem
operator|=
name|createHarFileSysten
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
comment|// close Har FS:
specifier|final
name|FileSystem
name|harFS
init|=
name|harFileSystem
decl_stmt|;
if|if
condition|(
name|harFS
operator|!=
literal|null
condition|)
block|{
name|harFS
operator|.
name|close
argument_list|()
expr_stmt|;
name|harFileSystem
operator|=
literal|null
expr_stmt|;
block|}
comment|// cleanup: delete all the temporary files:
specifier|final
name|File
name|rootDirIoFile
init|=
operator|new
name|File
argument_list|(
name|rootPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootDirIoFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|rootDirIoFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rootDirIoFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete temp directory ["
operator|+
name|rootDirIoFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|// ======== Positive tests:
annotation|@
name|Test
DECL|method|testPositiveHarFileSystemBasics ()
specifier|public
name|void
name|testPositiveHarFileSystemBasics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check Har version:
name|assertEquals
argument_list|(
name|HarFileSystem
operator|.
name|VERSION
argument_list|,
name|harFileSystem
operator|.
name|getHarVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// check Har URI:
specifier|final
name|URI
name|harUri
init|=
name|harFileSystem
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|harPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|harUri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"har"
argument_list|,
name|harUri
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
comment|// check Har home path:
specifier|final
name|Path
name|homePath
init|=
name|harFileSystem
operator|.
name|getHomeDirectory
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|harPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|homePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// check working directory:
specifier|final
name|Path
name|workDirPath0
init|=
name|harFileSystem
operator|.
name|getWorkingDirectory
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|homePath
argument_list|,
name|workDirPath0
argument_list|)
expr_stmt|;
comment|// check that its impossible to reset the working directory
comment|// (#setWorkingDirectory should have no effect):
name|harFileSystem
operator|.
name|setWorkingDirectory
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|workDirPath0
argument_list|,
name|harFileSystem
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPositiveNewHarFsOnTheSameUnderlyingFs ()
specifier|public
name|void
name|testPositiveNewHarFsOnTheSameUnderlyingFs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Init 2nd har file system on the same underlying FS, so the
comment|// metadata gets reused:
specifier|final
name|HarFileSystem
name|hfs
init|=
operator|new
name|HarFileSystem
argument_list|(
name|localFileSystem
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"har://"
operator|+
name|harPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|hfs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
comment|// the metadata should be reused from cache:
name|assertTrue
argument_list|(
name|hfs
operator|.
name|getMetadata
argument_list|()
operator|==
name|harFileSystem
operator|.
name|getMetadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPositiveInitWithoutUnderlyingFS ()
specifier|public
name|void
name|testPositiveInitWithoutUnderlyingFS
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Init HarFS with no constructor arg, so that the underlying FS object
comment|// is created on demand or got from cache in #initialize() method.
specifier|final
name|HarFileSystem
name|hfs
init|=
operator|new
name|HarFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"har://"
operator|+
name|harPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|hfs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// ========== Negative:
annotation|@
name|Test
DECL|method|testNegativeInitWithoutIndex ()
specifier|public
name|void
name|testNegativeInitWithoutIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// delete the index file:
specifier|final
name|Path
name|indexPath
init|=
operator|new
name|Path
argument_list|(
name|harPath
argument_list|,
literal|"_index"
argument_list|)
decl_stmt|;
name|localFileSystem
operator|.
name|delete
argument_list|(
name|indexPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// now init the HarFs:
specifier|final
name|HarFileSystem
name|hfs
init|=
operator|new
name|HarFileSystem
argument_list|(
name|localFileSystem
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"har://"
operator|+
name|harPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|hfs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
block|}
annotation|@
name|Test
DECL|method|testNegativeGetHarVersionOnNotInitializedFS ()
specifier|public
name|void
name|testNegativeGetHarVersionOnNotInitializedFS
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|HarFileSystem
name|hfs
init|=
operator|new
name|HarFileSystem
argument_list|(
name|localFileSystem
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|version
init|=
name|hfs
operator|.
name|getHarVersion
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception expected, but got a Har version "
operator|+
name|version
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
block|}
annotation|@
name|Test
DECL|method|testNegativeInitWithAnUnsupportedVersion ()
specifier|public
name|void
name|testNegativeInitWithAnUnsupportedVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NB: should wait at least 1 second to ensure the timestamp of the master
comment|// index will change upon the writing, because Linux seems to update the
comment|// file modification
comment|// time with 1 second accuracy:
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// write an unsupported version:
name|writeVersionToMasterIndexImpl
argument_list|(
literal|7777
argument_list|)
expr_stmt|;
comment|// init the Har:
specifier|final
name|HarFileSystem
name|hfs
init|=
operator|new
name|HarFileSystem
argument_list|(
name|localFileSystem
argument_list|)
decl_stmt|;
comment|// the metadata should *not* be reused from cache:
name|assertFalse
argument_list|(
name|hfs
operator|.
name|getMetadata
argument_list|()
operator|==
name|harFileSystem
operator|.
name|getMetadata
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"har://"
operator|+
name|harPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|hfs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
block|}
annotation|@
name|Test
DECL|method|testNegativeHarFsModifications ()
specifier|public
name|void
name|testNegativeHarFsModifications
parameter_list|()
throws|throws
name|Exception
block|{
comment|// all the modification methods of HarFS must lead to IOE.
specifier|final
name|Path
name|fooPath
init|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"foo/bar"
argument_list|)
decl_stmt|;
name|localFileSystem
operator|.
name|createNewFile
argument_list|(
name|fooPath
argument_list|)
expr_stmt|;
try|try
block|{
name|harFileSystem
operator|.
name|create
argument_list|(
name|fooPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"+rwx"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|88
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|setReplication
argument_list|(
name|fooPath
argument_list|,
operator|(
name|short
operator|)
literal|55
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|delete
argument_list|(
name|fooPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|mkdirs
argument_list|(
name|fooPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"+rwx"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
specifier|final
name|Path
name|indexPath
init|=
operator|new
name|Path
argument_list|(
name|harPath
argument_list|,
literal|"_index"
argument_list|)
decl_stmt|;
try|try
block|{
name|harFileSystem
operator|.
name|copyFromLocalFile
argument_list|(
literal|false
argument_list|,
name|indexPath
argument_list|,
name|fooPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|startLocalOutput
argument_list|(
name|fooPath
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|completeLocalOutput
argument_list|(
name|fooPath
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|setOwner
argument_list|(
name|fooPath
argument_list|,
literal|"user"
argument_list|,
literal|"group"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
try|try
block|{
name|harFileSystem
operator|.
name|setPermission
argument_list|(
name|fooPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"+x"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"IOException expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ok, expected.
block|}
block|}
block|}
end_class

end_unit

