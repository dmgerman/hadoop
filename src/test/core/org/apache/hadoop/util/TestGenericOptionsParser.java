begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|FileNotFoundException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_class
DECL|class|TestGenericOptionsParser
specifier|public
class|class
name|TestGenericOptionsParser
extends|extends
name|TestCase
block|{
DECL|field|testDir
name|File
name|testDir
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|localFs
name|FileSystem
name|localFs
decl_stmt|;
DECL|method|testFilesOption ()
specifier|public
name|void
name|testFilesOption
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"tmpfile"
argument_list|)
decl_stmt|;
name|Path
name|tmpPath
init|=
operator|new
name|Path
argument_list|(
name|tmpFile
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|create
argument_list|(
name|tmpPath
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
comment|// pass a files option
name|args
index|[
literal|0
index|]
operator|=
literal|"-files"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|tmpFile
operator|.
name|toString
argument_list|()
expr_stmt|;
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|files
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"tmpfiles"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"files is null"
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"files option does not match"
argument_list|,
name|localFs
operator|.
name|makeQualified
argument_list|(
name|tmpPath
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// pass file as uri
name|Configuration
name|conf1
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|URI
name|tmpURI
init|=
operator|new
name|URI
argument_list|(
name|tmpFile
operator|.
name|toString
argument_list|()
operator|+
literal|"#link"
argument_list|)
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-files"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|tmpURI
operator|.
name|toString
argument_list|()
expr_stmt|;
operator|new
name|GenericOptionsParser
argument_list|(
name|conf1
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|files
operator|=
name|conf1
operator|.
name|get
argument_list|(
literal|"tmpfiles"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"files is null"
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"files option does not match"
argument_list|,
name|localFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|tmpURI
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// pass a file that does not exist.
comment|// GenericOptionParser should throw exception
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-files"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
literal|"file:///xyz.txt"
expr_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
operator|new
name|GenericOptionsParser
argument_list|(
name|conf2
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"throwable is null"
argument_list|,
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FileNotFoundException is not thrown"
argument_list|,
name|th
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
name|files
operator|=
name|conf2
operator|.
name|get
argument_list|(
literal|"tmpfiles"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"files is not null"
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"generic"
argument_list|)
expr_stmt|;
if|if
condition|(
name|testDir
operator|.
name|exists
argument_list|()
condition|)
name|localFs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|testDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|localFs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * testing -fileCache option    * @throws IOException    */
DECL|method|testTokenCacheOption ()
specifier|public
name|void
name|testTokenCacheOption
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"tokenCacheFile"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
comment|// pass a files option
name|args
index|[
literal|0
index|]
operator|=
literal|"-tokenCacheFile"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|tmpFile
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// test non existing file
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FileNotFoundException is not thrown"
argument_list|,
name|th
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
comment|// create file
name|Path
name|tmpPath
init|=
operator|new
name|Path
argument_list|(
name|tmpFile
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|create
argument_list|(
name|tmpPath
argument_list|)
expr_stmt|;
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"tokenCacheFile"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"files is null"
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"files option does not match"
argument_list|,
name|localFs
operator|.
name|makeQualified
argument_list|(
name|tmpPath
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

