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
name|BytesWritable
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
name|SequenceFile
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
name|junit
operator|.
name|Test
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
name|Random
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

begin_class
DECL|class|TestSequenceFileInputFilter
specifier|public
class|class
name|TestSequenceFileInputFilter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|FileInputFormat
operator|.
name|LOG
decl_stmt|;
DECL|field|MAX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LENGTH
init|=
literal|15000
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|job
specifier|private
specifier|static
specifier|final
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|inDir
specifier|private
specifier|static
specifier|final
name|Path
name|inDir
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
literal|"."
argument_list|)
operator|+
literal|"/mapred"
argument_list|)
decl_stmt|;
DECL|field|inFile
specifier|private
specifier|static
specifier|final
name|Path
name|inFile
init|=
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"test.seq"
argument_list|)
decl_stmt|;
DECL|field|random
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|reporter
specifier|private
specifier|static
specifier|final
name|Reporter
name|reporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
static|static
block|{
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inDir
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createSequenceFile (int numRecords)
specifier|private
specifier|static
name|void
name|createSequenceFile
parameter_list|(
name|int
name|numRecords
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a file with length entries
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|inFile
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|BytesWritable
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numRecords
condition|;
name|i
operator|++
control|)
block|{
name|Text
name|key
init|=
operator|new
name|Text
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|countRecords (int numSplits)
specifier|private
name|int
name|countRecords
parameter_list|(
name|int
name|numSplits
parameter_list|)
throws|throws
name|IOException
block|{
name|InputFormat
argument_list|<
name|Text
argument_list|,
name|BytesWritable
argument_list|>
name|format
init|=
operator|new
name|SequenceFileInputFilter
argument_list|<
name|Text
argument_list|,
name|BytesWritable
argument_list|>
argument_list|()
decl_stmt|;
name|Text
name|key
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
if|if
condition|(
name|numSplits
operator|==
literal|0
condition|)
block|{
name|numSplits
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
operator|(
name|SequenceFile
operator|.
name|SYNC_INTERVAL
operator|/
literal|20
operator|)
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
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
name|numSplits
argument_list|)
decl_stmt|;
comment|// check each split
name|int
name|count
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Generated "
operator|+
name|splits
operator|.
name|length
operator|+
literal|" splits."
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
name|splits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|BytesWritable
argument_list|>
name|reader
init|=
name|format
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|j
index|]
argument_list|,
name|job
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Accept record "
operator|+
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Test
DECL|method|testRegexFilter ()
specifier|public
name|void
name|testRegexFilter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set the filter class
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing Regex Filter with patter: \\A10*"
argument_list|)
expr_stmt|;
name|SequenceFileInputFilter
operator|.
name|setFilterClass
argument_list|(
name|job
argument_list|,
name|SequenceFileInputFilter
operator|.
name|RegexFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|SequenceFileInputFilter
operator|.
name|RegexFilter
operator|.
name|setPattern
argument_list|(
name|job
argument_list|,
literal|"\\A10*"
argument_list|)
expr_stmt|;
comment|// clean input dir
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// for a variety of lengths
for|for
control|(
name|int
name|length
init|=
literal|1
init|;
name|length
operator|<
name|MAX_LENGTH
condition|;
name|length
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|10
argument_list|)
operator|+
literal|1
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"******Number of records: "
operator|+
name|length
argument_list|)
expr_stmt|;
name|createSequenceFile
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|countRecords
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|length
operator|==
literal|0
condition|?
literal|0
else|:
operator|(
name|int
operator|)
name|Math
operator|.
name|log10
argument_list|(
name|length
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPercentFilter ()
specifier|public
name|void
name|testPercentFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing Percent Filter with frequency: 1000"
argument_list|)
expr_stmt|;
comment|// set the filter class
name|SequenceFileInputFilter
operator|.
name|setFilterClass
argument_list|(
name|job
argument_list|,
name|SequenceFileInputFilter
operator|.
name|PercentFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|SequenceFileInputFilter
operator|.
name|PercentFilter
operator|.
name|setFrequency
argument_list|(
name|job
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// clean input dir
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// for a variety of lengths
for|for
control|(
name|int
name|length
init|=
literal|0
init|;
name|length
operator|<
name|MAX_LENGTH
condition|;
name|length
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|10
argument_list|)
operator|+
literal|1
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"******Number of records: "
operator|+
name|length
argument_list|)
expr_stmt|;
name|createSequenceFile
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|countRecords
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Accepted "
operator|+
name|count
operator|+
literal|" records"
argument_list|)
expr_stmt|;
name|int
name|expectedCount
init|=
name|length
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|expectedCount
operator|*
literal|1000
operator|!=
name|length
condition|)
name|expectedCount
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|expectedCount
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMD5Filter ()
specifier|public
name|void
name|testMD5Filter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set the filter class
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing MD5 Filter with frequency: 1000"
argument_list|)
expr_stmt|;
name|SequenceFileInputFilter
operator|.
name|setFilterClass
argument_list|(
name|job
argument_list|,
name|SequenceFileInputFilter
operator|.
name|MD5Filter
operator|.
name|class
argument_list|)
expr_stmt|;
name|SequenceFileInputFilter
operator|.
name|MD5Filter
operator|.
name|setFrequency
argument_list|(
name|job
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// clean input dir
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// for a variety of lengths
for|for
control|(
name|int
name|length
init|=
literal|0
init|;
name|length
operator|<
name|MAX_LENGTH
condition|;
name|length
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|10
argument_list|)
operator|+
literal|1
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"******Number of records: "
operator|+
name|length
argument_list|)
expr_stmt|;
name|createSequenceFile
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Accepted "
operator|+
name|countRecords
argument_list|(
literal|0
argument_list|)
operator|+
literal|" records"
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

