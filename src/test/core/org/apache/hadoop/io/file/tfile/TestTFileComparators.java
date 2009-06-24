begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|io
operator|.
name|file
operator|.
name|tfile
operator|.
name|TFile
operator|.
name|Writer
import|;
end_import

begin_comment
comment|/**  *   * Byte arrays test case class using GZ compression codec, base class of none  * and LZO compression classes.  *   */
end_comment

begin_class
DECL|class|TestTFileComparators
specifier|public
class|class
name|TestTFileComparators
extends|extends
name|TestCase
block|{
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
literal|"/tmp/tfile-test"
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BLOCK_SIZE
init|=
literal|512
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|out
specifier|private
name|FSDataOutputStream
name|out
decl_stmt|;
DECL|field|writer
specifier|private
name|Writer
name|writer
decl_stmt|;
DECL|field|compression
specifier|private
name|String
name|compression
init|=
name|Compression
operator|.
name|Algorithm
operator|.
name|GZ
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|outputFile
specifier|private
name|String
name|outputFile
init|=
literal|"TFileTestComparators"
decl_stmt|;
comment|/*    * pre-sampled numbers of records in one block, based on the given the    * generated key and value strings    */
comment|// private int records1stBlock = 4314;
comment|// private int records2ndBlock = 4108;
DECL|field|records1stBlock
specifier|private
name|int
name|records1stBlock
init|=
literal|4480
decl_stmt|;
DECL|field|records2ndBlock
specifier|private
name|int
name|records2ndBlock
init|=
literal|4263
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|ROOT
argument_list|,
name|outputFile
argument_list|)
expr_stmt|;
name|fs
operator|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// bad comparator format
DECL|method|testFailureBadComparatorNames ()
specifier|public
name|void
name|testFailureBadComparatorNames
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|writer
operator|=
operator|new
name|Writer
argument_list|(
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|compression
argument_list|,
literal|"badcmp"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to catch unsupported comparator names"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting exceptions
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|// jclass that doesn't exist
DECL|method|testFailureBadJClassNames ()
specifier|public
name|void
name|testFailureBadJClassNames
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|writer
operator|=
operator|new
name|Writer
argument_list|(
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|compression
argument_list|,
literal|"jclass: some.non.existence.clazz"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to catch unsupported comparator names"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting exceptions
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|// class exists but not a RawComparator
DECL|method|testFailureBadJClasses ()
specifier|public
name|void
name|testFailureBadJClasses
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|writer
operator|=
operator|new
name|Writer
argument_list|(
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|compression
argument_list|,
literal|"jclass:org.apache.hadoop.io.file.tfile.Chunk"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to catch unsupported comparator names"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting exceptions
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|closeOutput ()
specifier|private
name|void
name|closeOutput
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

