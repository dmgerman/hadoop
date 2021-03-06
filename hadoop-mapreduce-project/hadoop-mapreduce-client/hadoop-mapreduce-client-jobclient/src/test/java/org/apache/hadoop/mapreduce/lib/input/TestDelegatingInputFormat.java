begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.input
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
package|;
end_package

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
name|IOException
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|mapreduce
operator|.
name|InputSplit
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|Mapper
import|;
end_import

begin_class
DECL|class|TestDelegatingInputFormat
specifier|public
class|class
name|TestDelegatingInputFormat
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testSplitting ()
specifier|public
name|void
name|testSplitting
parameter_list|()
throws|throws
name|Exception
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dfs
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|4
argument_list|)
operator|.
name|racks
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/rack0"
block|,
literal|"/rack0"
block|,
literal|"/rack1"
block|,
literal|"/rack1"
block|}
argument_list|)
operator|.
name|hosts
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"host0"
block|,
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|getPath
argument_list|(
literal|"/foo/bar"
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
name|getPath
argument_list|(
literal|"/foo/baz"
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|Path
name|path3
init|=
name|getPath
argument_list|(
literal|"/bar/bar"
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|Path
name|path4
init|=
name|getPath
argument_list|(
literal|"/bar/baz"
argument_list|,
name|fs
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numSplits
init|=
literal|100
decl_stmt|;
name|FileInputFormat
operator|.
name|setMaxInputSplitSize
argument_list|(
name|job
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
operator|/
name|numSplits
argument_list|)
expr_stmt|;
name|MultipleInputs
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|path
argument_list|,
name|TextInputFormat
operator|.
name|class
argument_list|,
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|MultipleInputs
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|path2
argument_list|,
name|TextInputFormat
operator|.
name|class
argument_list|,
name|MapClass2
operator|.
name|class
argument_list|)
expr_stmt|;
name|MultipleInputs
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|path3
argument_list|,
name|KeyValueTextInputFormat
operator|.
name|class
argument_list|,
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|MultipleInputs
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|path4
argument_list|,
name|TextInputFormat
operator|.
name|class
argument_list|,
name|MapClass2
operator|.
name|class
argument_list|)
expr_stmt|;
name|DelegatingInputFormat
name|inFormat
init|=
operator|new
name|DelegatingInputFormat
argument_list|()
decl_stmt|;
name|int
index|[]
name|bins
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
operator|(
name|List
argument_list|<
name|InputSplit
argument_list|>
operator|)
name|inFormat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|split
operator|instanceof
name|TaggedInputSplit
argument_list|)
expr_stmt|;
specifier|final
name|TaggedInputSplit
name|tis
init|=
operator|(
name|TaggedInputSplit
operator|)
name|split
decl_stmt|;
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|tis
operator|.
name|getInputFormatClass
argument_list|()
operator|.
name|equals
argument_list|(
name|KeyValueTextInputFormat
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// path3
name|index
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tis
operator|.
name|getMapperClass
argument_list|()
operator|.
name|equals
argument_list|(
name|MapClass
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// path
name|index
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// path2 and path4
name|index
operator|=
literal|2
expr_stmt|;
block|}
name|bins
index|[
name|index
index|]
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"count is not equal to num splits"
argument_list|,
name|numSplits
argument_list|,
name|bins
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"count is not equal to num splits"
argument_list|,
name|numSplits
argument_list|,
name|bins
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"count is not equal to 2 * num splits"
argument_list|,
name|numSplits
operator|*
literal|2
argument_list|,
name|bins
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPath (final String location, final FileSystem fs)
specifier|static
name|Path
name|getPath
parameter_list|(
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|location
argument_list|)
decl_stmt|;
comment|// create a multi-block file on hdfs
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|512
argument_list|,
literal|null
argument_list|)
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|writeChars
argument_list|(
literal|"Hello\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|class|MapClass
specifier|static
class|class
name|MapClass
extends|extends
name|Mapper
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|String
argument_list|,
name|String
argument_list|>
block|{   }
DECL|class|MapClass2
specifier|static
class|class
name|MapClass2
extends|extends
name|MapClass
block|{   }
block|}
end_class

end_unit

