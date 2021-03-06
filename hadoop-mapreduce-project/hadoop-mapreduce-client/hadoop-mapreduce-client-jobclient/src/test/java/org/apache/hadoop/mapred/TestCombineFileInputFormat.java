begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|mapred
operator|.
name|lib
operator|.
name|CombineFileInputFormat
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
name|mapred
operator|.
name|lib
operator|.
name|CombineFileSplit
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
name|mapred
operator|.
name|lib
operator|.
name|CombineFileRecordReader
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

begin_class
DECL|class|TestCombineFileInputFormat
specifier|public
class|class
name|TestCombineFileInputFormat
block|{
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
name|TestCombineFileInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultConf
specifier|private
specifier|static
name|JobConf
name|defaultConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
init|=
literal|null
decl_stmt|;
static|static
block|{
try|try
block|{
name|defaultConf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|defaultConf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"init failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|workDir
specifier|private
specifier|static
name|Path
name|workDir
init|=
name|localFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
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
literal|"TestCombineFileInputFormat"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|writeFile (FileSystem fs, Path name, String contents)
specifier|private
specifier|static
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|stm
decl_stmt|;
name|stm
operator|=
name|fs
operator|.
name|create
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|contents
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test getSplits    */
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSplits ()
specifier|public
name|void
name|testSplits
parameter_list|()
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|localFs
argument_list|,
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"test.txt"
argument_list|)
argument_list|,
literal|"the quick\nbrown\nfox jumped\nover\n the lazy\n dog\n"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|CombineFileInputFormat
name|format
init|=
operator|new
name|CombineFileInputFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordReader
name|getRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CombineFileRecordReader
argument_list|(
name|job
argument_list|,
operator|(
name|CombineFileSplit
operator|)
name|split
argument_list|,
name|reporter
argument_list|,
name|CombineFileRecordReader
operator|.
name|class
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|int
name|SIZE_SPLITS
init|=
literal|1
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to getSplits with splits = "
operator|+
name|SIZE_SPLITS
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|SIZE_SPLITS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got getSplits = "
operator|+
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits == "
operator|+
name|SIZE_SPLITS
argument_list|,
name|SIZE_SPLITS
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

