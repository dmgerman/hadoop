begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|FileWriter
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
name|LongWritable
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
name|Text
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
name|Reporter
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
name|JobConf
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
name|TextInputFormat
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
name|FileUtil
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
name|mockito
operator|.
name|Mockito
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|times
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
name|verify
import|;
end_import

begin_class
DECL|class|TestCombineFileRecordReader
specifier|public
class|class
name|TestCombineFileRecordReader
block|{
DECL|field|outDir
specifier|private
specifier|static
name|Path
name|outDir
init|=
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
name|TestCombineFileRecordReader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|TextRecordReaderWrapper
specifier|private
specifier|static
class|class
name|TextRecordReaderWrapper
extends|extends
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
name|CombineFileRecordReaderWrapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
comment|// this constructor signature is required by CombineFileRecordReader
DECL|method|TextRecordReaderWrapper (CombineFileSplit split, Configuration conf, Reporter reporter, Integer idx)
specifier|public
name|TextRecordReaderWrapper
parameter_list|(
name|CombineFileSplit
name|split
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|Integer
name|idx
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|TextInputFormat
argument_list|()
argument_list|,
name|split
argument_list|,
name|conf
argument_list|,
name|reporter
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testInitNextRecordReader ()
specifier|public
name|void
name|testInitNextRecordReader
parameter_list|()
throws|throws
name|IOException
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|Path
index|[]
name|paths
init|=
operator|new
name|Path
index|[
literal|3
index|]
decl_stmt|;
name|long
index|[]
name|fileLength
init|=
operator|new
name|long
index|[
literal|3
index|]
decl_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
index|[
literal|3
index|]
decl_stmt|;
name|LongWritable
name|key
init|=
operator|new
name|LongWritable
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Text
name|value
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|fileLength
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|files
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"testfile"
operator|+
name|i
argument_list|)
expr_stmt|;
name|FileWriter
name|fileWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|outDir
operator|+
literal|"/testfile"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|CombineFileSplit
name|combineFileSplit
init|=
operator|new
name|CombineFileSplit
argument_list|(
name|conf
argument_list|,
name|paths
argument_list|,
name|fileLength
argument_list|)
decl_stmt|;
name|Reporter
name|reporter
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Reporter
operator|.
name|class
argument_list|)
decl_stmt|;
name|CombineFileRecordReader
name|cfrr
init|=
operator|new
name|CombineFileRecordReader
argument_list|(
name|conf
argument_list|,
name|combineFileSplit
argument_list|,
name|reporter
argument_list|,
name|TextRecordReaderWrapper
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|reporter
argument_list|)
operator|.
name|progress
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|cfrr
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|reporter
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

