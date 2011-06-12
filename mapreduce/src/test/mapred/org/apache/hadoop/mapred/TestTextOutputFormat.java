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
name|*
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
name|fs
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
name|io
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestTextOutputFormat
specifier|public
class|class
name|TestTextOutputFormat
extends|extends
name|TestCase
block|{
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
comment|// A random task attempt id for testing.
DECL|field|attempt
specifier|private
specifier|static
name|String
name|attempt
init|=
literal|"attempt_200707121733_0001_m_000000_0"
decl_stmt|;
DECL|field|workDir
specifier|private
specifier|static
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
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
literal|"."
argument_list|)
argument_list|,
literal|"data"
argument_list|)
argument_list|,
name|FileOutputCommitter
operator|.
name|TEMP_DIR_NAME
argument_list|)
argument_list|,
literal|"_"
operator|+
name|attempt
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|workDir
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setWorkOutputPath
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|workDir
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|workDir
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Failed to create output directory"
argument_list|)
expr_stmt|;
block|}
name|String
name|file
init|=
literal|"test.txt"
decl_stmt|;
comment|// A reporter that does nothing
name|Reporter
name|reporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
name|TextOutputFormat
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
name|localFs
argument_list|,
name|job
argument_list|,
name|file
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|Text
name|key1
init|=
operator|new
name|Text
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|Text
name|key2
init|=
operator|new
name|Text
argument_list|(
literal|"key2"
argument_list|)
decl_stmt|;
name|Text
name|val1
init|=
operator|new
name|Text
argument_list|(
literal|"val1"
argument_list|)
decl_stmt|;
name|Text
name|val2
init|=
operator|new
name|Text
argument_list|(
literal|"val2"
argument_list|)
decl_stmt|;
name|NullWritable
name|nullWritable
init|=
name|NullWritable
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|nullWritable
argument_list|,
name|val2
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|val2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|theRecordWriter
operator|.
name|close
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
block|}
name|File
name|expectedFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
name|file
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuffer
name|expectedOutput
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|UtilsForTests
operator|.
name|slurp
argument_list|(
name|expectedFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|expectedOutput
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testFormatWithCustomSeparator ()
specifier|public
name|void
name|testFormatWithCustomSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|String
name|separator
init|=
literal|"\u0001"
decl_stmt|;
name|job
operator|.
name|set
argument_list|(
literal|"mapreduce.output.textoutputformat.separator"
argument_list|,
name|separator
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|workDir
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setWorkOutputPath
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|workDir
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|workDir
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Failed to create output directory"
argument_list|)
expr_stmt|;
block|}
name|String
name|file
init|=
literal|"test.txt"
decl_stmt|;
comment|// A reporter that does nothing
name|Reporter
name|reporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
name|TextOutputFormat
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
name|localFs
argument_list|,
name|job
argument_list|,
name|file
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|Text
name|key1
init|=
operator|new
name|Text
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|Text
name|key2
init|=
operator|new
name|Text
argument_list|(
literal|"key2"
argument_list|)
decl_stmt|;
name|Text
name|val1
init|=
operator|new
name|Text
argument_list|(
literal|"val1"
argument_list|)
decl_stmt|;
name|Text
name|val2
init|=
operator|new
name|Text
argument_list|(
literal|"val2"
argument_list|)
decl_stmt|;
name|NullWritable
name|nullWritable
init|=
name|NullWritable
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|nullWritable
argument_list|,
name|val2
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|val2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|theRecordWriter
operator|.
name|close
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
block|}
name|File
name|expectedFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
name|file
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuffer
name|expectedOutput
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
name|separator
argument_list|)
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
name|separator
argument_list|)
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|UtilsForTests
operator|.
name|slurp
argument_list|(
name|expectedFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|expectedOutput
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestTextOutputFormat
argument_list|()
operator|.
name|testFormat
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

