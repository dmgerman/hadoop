begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|test
operator|.
name|PathUtils
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
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
name|ExpectedException
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
name|Timeout
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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

begin_comment
comment|/**  * Unit tests for {@link OmUtils}.  */
end_comment

begin_class
DECL|class|TestOmUtils
specifier|public
class|class
name|TestOmUtils
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|60_000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Test {@link OmUtils#getOmDbDir}.    */
annotation|@
name|Test
DECL|method|testGetOmDbDir ()
specifier|public
name|void
name|testGetOmDbDir
parameter_list|()
block|{
specifier|final
name|File
name|testDir
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestOmUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|File
name|dbDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"omDbDir"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|metaDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"metaDir"
argument_list|)
decl_stmt|;
comment|// should be ignored.
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_DB_DIRS
argument_list|,
name|dbDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|dbDir
argument_list|,
name|OmUtils
operator|.
name|getOmDbDir
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dbDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// should have been created.
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dbDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test {@link OmUtils#getOmDbDir} with fallback to OZONE_METADATA_DIRS    * when OZONE_OM_DB_DIRS is undefined.    */
annotation|@
name|Test
DECL|method|testGetOmDbDirWithFallback ()
specifier|public
name|void
name|testGetOmDbDirWithFallback
parameter_list|()
block|{
specifier|final
name|File
name|testDir
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestOmUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|File
name|metaDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"metaDir"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|metaDir
argument_list|,
name|OmUtils
operator|.
name|getOmDbDir
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metaDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// should have been created.
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|metaDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoOmDbDirConfigured ()
specifier|public
name|void
name|testNoOmDbDirConfigured
parameter_list|()
block|{
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|OmUtils
operator|.
name|getOmDbDir
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateTarFile ()
specifier|public
name|void
name|testCreateTarFile
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempSnapshotDir
init|=
literal|null
decl_stmt|;
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|File
name|tarFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|testDirName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|testDirName
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|testDirName
operator|+=
literal|"/"
expr_stmt|;
block|}
name|testDirName
operator|+=
literal|"TestCreateTarFile_Dir"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|tempSnapshotDir
operator|=
operator|new
name|File
argument_list|(
name|testDirName
argument_list|)
expr_stmt|;
name|tempSnapshotDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|testDirName
operator|+
literal|"/temp1.txt"
argument_list|)
decl_stmt|;
name|FileWriter
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"Test data 1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|testDirName
operator|+
literal|"/temp2.txt"
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"Test data 2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|tarFile
operator|=
name|OmUtils
operator|.
name|createTarFile
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|testDirName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|tarFile
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempSnapshotDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tarFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

