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
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|fail
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
name|WritableUtils
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
name|Reader
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
name|Reader
operator|.
name|Scanner
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
name|GenericTestUtils
import|;
end_import

begin_comment
comment|/**  *   * Streaming interfaces test case class using GZ compression codec, base class  * of none and LZO compression classes.  *   */
end_comment

begin_class
DECL|class|TestTFileStreams
specifier|public
class|class
name|TestTFileStreams
block|{
DECL|field|ROOT
specifier|private
specifier|static
name|String
name|ROOT
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
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
DECL|field|K
specifier|private
specifier|final
specifier|static
name|int
name|K
init|=
literal|1024
decl_stmt|;
DECL|field|M
specifier|private
specifier|final
specifier|static
name|int
name|M
init|=
name|K
operator|*
name|K
decl_stmt|;
DECL|field|skip
specifier|protected
name|boolean
name|skip
init|=
literal|false
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
DECL|field|comparator
specifier|private
name|String
name|comparator
init|=
literal|"memcmp"
decl_stmt|;
DECL|field|outputFile
specifier|private
specifier|final
name|String
name|outputFile
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|method|init (String compression, String comparator)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|compression
parameter_list|,
name|String
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|compression
operator|=
name|compression
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Before
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
name|comparator
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|skip
condition|)
block|{
try|try
block|{
name|closeOutput
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// no-op
block|}
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
block|}
annotation|@
name|Test
DECL|method|testNoEntry ()
specifier|public
name|void
name|testNoEntry
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|closeOutput
argument_list|()
expr_stmt|;
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneEntryKnownLength ()
specifier|public
name|void
name|testOneEntryKnownLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneEntryUnknownLength ()
specifier|public
name|void
name|testOneEntryUnknownLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// TODO: will throw exception at getValueLength, it's inconsistent though;
comment|// getKeyLength returns a value correctly, though initial length is -1
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// known key length, unknown value length
annotation|@
name|Test
DECL|method|testOneEntryMixedLengths1 ()
specifier|public
name|void
name|testOneEntryMixedLengths1
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// unknown key length, known value length
annotation|@
name|Test
DECL|method|testOneEntryMixedLengths2 ()
specifier|public
name|void
name|testOneEntryMixedLengths2
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoEntriesKnownLength ()
specifier|public
name|void
name|testTwoEntriesKnownLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TestTFileByteArrays
operator|.
name|readRecords
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|2
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// Negative test
annotation|@
name|Test
DECL|method|testFailureAddKeyWithoutValue ()
specifier|public
name|void
name|testFailureAddKeyWithoutValue
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|dos
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|dos
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|closeOutput
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot add only a key without a value. "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureAddValueWithoutKey ()
specifier|public
name|void
name|testFailureAddValueWithoutKey
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|outValue
operator|=
name|writer
operator|.
name|prepareAppendValue
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|outValue
operator|.
name|write
argument_list|(
literal|"value0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot add a value without adding key first. "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
finally|finally
block|{
if|if
condition|(
name|outValue
operator|!=
literal|null
condition|)
block|{
name|outValue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureOneEntryKnownLength ()
specifier|public
name|void
name|testFailureOneEntryKnownLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Specified key length mismatched the actual key length."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
name|DataOutputStream
name|outValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|outValue
operator|=
name|writer
operator|.
name|prepareAppendValue
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|outValue
operator|.
name|write
argument_list|(
literal|"value0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureKeyTooLong ()
specifier|public
name|void
name|testFailureKeyTooLong
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Key is longer than requested."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
finally|finally
block|{     }
block|}
annotation|@
name|Test
DECL|method|testFailureKeyTooShort ()
specifier|public
name|void
name|testFailureKeyTooShort
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|DataOutputStream
name|outValue
init|=
name|writer
operator|.
name|prepareAppendValue
argument_list|(
literal|15
argument_list|)
decl_stmt|;
try|try
block|{
name|outValue
operator|.
name|write
argument_list|(
literal|"value0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outValue
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Value is shorter than expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
finally|finally
block|{     }
block|}
annotation|@
name|Test
DECL|method|testFailureValueTooLong ()
specifier|public
name|void
name|testFailureValueTooLong
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|DataOutputStream
name|outValue
init|=
name|writer
operator|.
name|prepareAppendValue
argument_list|(
literal|3
argument_list|)
decl_stmt|;
try|try
block|{
name|outValue
operator|.
name|write
argument_list|(
literal|"value0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outValue
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Value is longer than expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
try|try
block|{
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Second or more close() should have no effect."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureValueTooShort ()
specifier|public
name|void
name|testFailureValueTooShort
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|8
argument_list|)
decl_stmt|;
try|try
block|{
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Key is shorter than expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
finally|finally
block|{     }
block|}
annotation|@
name|Test
DECL|method|testFailureCloseKeyStreamManyTimesInWriter ()
specifier|public
name|void
name|testFailureCloseKeyStreamManyTimesInWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|4
argument_list|)
decl_stmt|;
try|try
block|{
name|outKey
operator|.
name|write
argument_list|(
literal|"key0"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// noop, expecting an exception
block|}
finally|finally
block|{
try|try
block|{
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// no-op
block|}
block|}
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Multiple close should have no effect."
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureKeyLongerThan64K ()
specifier|public
name|void
name|testFailureKeyLongerThan64K
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
try|try
block|{
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
literal|64
operator|*
name|K
operator|+
literal|1
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Failed to handle key longer than 64K."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// noop, expecting exceptions
block|}
name|closeOutput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureKeyLongerThan64K_2 ()
specifier|public
name|void
name|testFailureKeyLongerThan64K_2
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|K
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|nx
init|=
literal|0
init|;
name|nx
operator|<
name|K
operator|+
literal|2
condition|;
name|nx
operator|++
control|)
block|{
name|rand
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to handle key longer than 64K."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// noop, expecting exceptions
block|}
finally|finally
block|{
try|try
block|{
name|closeOutput
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// no-op
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureNegativeOffset ()
specifier|public
name|void
name|testFailureNegativeOffset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|writeRecords
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
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
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Scanner
name|scanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|K
index|]
decl_stmt|;
try|try
block|{
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKey
argument_list|(
name|buf
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to handle key negative offset."
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
block|}
finally|finally
block|{     }
name|scanner
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify that the compressed data size is less than raw data size.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testFailureCompressionNotWorking ()
specifier|public
name|void
name|testFailureCompressionNotWorking
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|long
name|rawDataSize
init|=
name|writeRecords
argument_list|(
literal|10000
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|compression
operator|.
name|equalsIgnoreCase
argument_list|(
name|Compression
operator|.
name|Algorithm
operator|.
name|NONE
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|out
operator|.
name|getPos
argument_list|()
operator|<
name|rawDataSize
argument_list|)
expr_stmt|;
block|}
name|closeOutput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureCompressionNotWorking2 ()
specifier|public
name|void
name|testFailureCompressionNotWorking2
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|skip
condition|)
return|return;
name|long
name|rawDataSize
init|=
name|writeRecords
argument_list|(
literal|10000
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|compression
operator|.
name|equalsIgnoreCase
argument_list|(
name|Compression
operator|.
name|Algorithm
operator|.
name|NONE
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|out
operator|.
name|getPos
argument_list|()
operator|<
name|rawDataSize
argument_list|)
expr_stmt|;
block|}
name|closeOutput
argument_list|()
expr_stmt|;
block|}
DECL|method|writeRecords (int count, boolean knownKeyLength, boolean knownValueLength, boolean close)
specifier|private
name|long
name|writeRecords
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|knownKeyLength
parameter_list|,
name|boolean
name|knownValueLength
parameter_list|,
name|boolean
name|close
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|rawDataSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|nx
init|=
literal|0
init|;
name|nx
operator|<
name|count
condition|;
name|nx
operator|++
control|)
block|{
name|String
name|key
init|=
name|TestTFileByteArrays
operator|.
name|composeSortedKey
argument_list|(
literal|"key"
argument_list|,
name|nx
argument_list|)
decl_stmt|;
name|DataOutputStream
name|outKey
init|=
name|writer
operator|.
name|prepareAppendKey
argument_list|(
name|knownKeyLength
condition|?
name|key
operator|.
name|length
argument_list|()
else|:
operator|-
literal|1
argument_list|)
decl_stmt|;
name|outKey
operator|.
name|write
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outKey
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|value
init|=
literal|"value"
operator|+
name|nx
decl_stmt|;
name|DataOutputStream
name|outValue
init|=
name|writer
operator|.
name|prepareAppendValue
argument_list|(
name|knownValueLength
condition|?
name|value
operator|.
name|length
argument_list|()
else|:
operator|-
literal|1
argument_list|)
decl_stmt|;
name|outValue
operator|.
name|write
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|outValue
operator|.
name|close
argument_list|()
expr_stmt|;
name|rawDataSize
operator|+=
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
operator|+
name|key
operator|.
name|getBytes
argument_list|()
operator|.
name|length
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
operator|+
name|value
operator|.
name|getBytes
argument_list|()
operator|.
name|length
expr_stmt|;
block|}
if|if
condition|(
name|close
condition|)
block|{
name|closeOutput
argument_list|()
expr_stmt|;
block|}
return|return
name|rawDataSize
return|;
block|}
DECL|method|writeRecords (int count, boolean knownKeyLength, boolean knownValueLength)
specifier|private
name|long
name|writeRecords
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|knownKeyLength
parameter_list|,
name|boolean
name|knownValueLength
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeRecords
argument_list|(
name|count
argument_list|,
name|knownKeyLength
argument_list|,
name|knownValueLength
argument_list|,
literal|true
argument_list|)
return|;
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

