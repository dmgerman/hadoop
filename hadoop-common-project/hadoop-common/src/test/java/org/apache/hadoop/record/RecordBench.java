begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

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
name|lang
operator|.
name|reflect
operator|.
name|Method
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

begin_comment
comment|/**  * Benchmark for various types of serializations  */
end_comment

begin_class
DECL|class|RecordBench
specifier|public
class|class
name|RecordBench
block|{
DECL|class|Times
specifier|private
specifier|static
class|class
name|Times
block|{
DECL|field|init
name|long
name|init
decl_stmt|;
DECL|field|serialize
name|long
name|serialize
decl_stmt|;
DECL|field|deserialize
name|long
name|deserialize
decl_stmt|;
DECL|field|write
name|long
name|write
decl_stmt|;
DECL|field|readFields
name|long
name|readFields
decl_stmt|;
block|}
empty_stmt|;
DECL|field|SEED
specifier|private
specifier|static
specifier|final
name|long
name|SEED
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|rand
specifier|private
specifier|static
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|/** Do not allow to create a new instance of RecordBench */
DECL|method|RecordBench ()
specifier|private
name|RecordBench
parameter_list|()
block|{}
DECL|method|initBuffers (Record[] buffers)
specifier|private
specifier|static
name|void
name|initBuffers
parameter_list|(
name|Record
index|[]
name|buffers
parameter_list|)
block|{
specifier|final
name|int
name|BUFLEN
init|=
literal|32
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|buffers
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|buffers
index|[
name|idx
index|]
operator|=
operator|new
name|RecBuffer
argument_list|()
expr_stmt|;
name|int
name|buflen
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|BUFLEN
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|buflen
index|]
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
operator|(
operator|(
name|RecBuffer
operator|)
name|buffers
index|[
name|idx
index|]
operator|)
operator|.
name|setData
argument_list|(
operator|new
name|Buffer
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initStrings (Record[] strings)
specifier|private
specifier|static
name|void
name|initStrings
parameter_list|(
name|Record
index|[]
name|strings
parameter_list|)
block|{
specifier|final
name|int
name|STRLEN
init|=
literal|32
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|strings
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|strings
index|[
name|idx
index|]
operator|=
operator|new
name|RecString
argument_list|()
expr_stmt|;
name|int
name|strlen
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|STRLEN
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|strlen
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ich
init|=
literal|0
init|;
name|ich
operator|<
name|strlen
condition|;
name|ich
operator|++
control|)
block|{
name|int
name|cpt
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|cpt
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|0x10FFFF
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|Utils
operator|.
name|isValidCodePoint
argument_list|(
name|cpt
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|sb
operator|.
name|appendCodePoint
argument_list|(
name|cpt
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|RecString
operator|)
name|strings
index|[
name|idx
index|]
operator|)
operator|.
name|setData
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initInts (Record[] ints)
specifier|private
specifier|static
name|void
name|initInts
parameter_list|(
name|Record
index|[]
name|ints
parameter_list|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|ints
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|ints
index|[
name|idx
index|]
operator|=
operator|new
name|RecInt
argument_list|()
expr_stmt|;
operator|(
operator|(
name|RecInt
operator|)
name|ints
index|[
name|idx
index|]
operator|)
operator|.
name|setData
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeArray (String type, int numRecords, Times times)
specifier|private
specifier|static
name|Record
index|[]
name|makeArray
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|numRecords
parameter_list|,
name|Times
name|times
parameter_list|)
block|{
name|Method
name|init
init|=
literal|null
decl_stmt|;
try|try
block|{
name|init
operator|=
name|RecordBench
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"init"
operator|+
name|toCamelCase
argument_list|(
name|type
argument_list|)
operator|+
literal|"s"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Record
index|[]
operator|.
expr|class
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|Record
index|[]
name|records
init|=
operator|new
name|Record
index|[
name|numRecords
index|]
decl_stmt|;
name|times
operator|.
name|init
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
try|try
block|{
name|init
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|records
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|times
operator|.
name|init
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|init
expr_stmt|;
return|return
name|records
return|;
block|}
DECL|method|runBinaryBench (String type, int numRecords, Times times)
specifier|private
specifier|static
name|void
name|runBinaryBench
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|numRecords
parameter_list|,
name|Times
name|times
parameter_list|)
throws|throws
name|IOException
block|{
name|Record
index|[]
name|records
init|=
name|makeArray
argument_list|(
name|type
argument_list|,
name|numRecords
argument_list|,
name|times
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|BinaryRecordOutput
name|rout
init|=
operator|new
name|BinaryRecordOutput
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|DataOutputStream
name|dout
init|=
operator|new
name|DataOutputStream
argument_list|(
name|bout
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|bout
operator|.
name|reset
argument_list|()
expr_stmt|;
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|serialize
expr_stmt|;
name|byte
index|[]
name|serialized
init|=
name|bout
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bin
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|BinaryRecordInput
name|rin
init|=
operator|new
name|BinaryRecordInput
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|deserialize
argument_list|(
name|rin
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|deserialize
expr_stmt|;
name|bout
operator|.
name|reset
argument_list|()
expr_stmt|;
name|times
operator|.
name|write
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|write
argument_list|(
name|dout
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|write
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|write
expr_stmt|;
name|bin
operator|.
name|reset
argument_list|()
expr_stmt|;
name|DataInputStream
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|times
operator|.
name|readFields
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|readFields
argument_list|(
name|din
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|readFields
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|readFields
expr_stmt|;
block|}
DECL|method|runCsvBench (String type, int numRecords, Times times)
specifier|private
specifier|static
name|void
name|runCsvBench
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|numRecords
parameter_list|,
name|Times
name|times
parameter_list|)
throws|throws
name|IOException
block|{
name|Record
index|[]
name|records
init|=
name|makeArray
argument_list|(
name|type
argument_list|,
name|numRecords
argument_list|,
name|times
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|CsvRecordOutput
name|rout
init|=
operator|new
name|CsvRecordOutput
argument_list|(
name|bout
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|bout
operator|.
name|reset
argument_list|()
expr_stmt|;
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|serialize
expr_stmt|;
name|byte
index|[]
name|serialized
init|=
name|bout
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bin
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|CsvRecordInput
name|rin
init|=
operator|new
name|CsvRecordInput
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|deserialize
argument_list|(
name|rin
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|deserialize
expr_stmt|;
block|}
DECL|method|runXmlBench (String type, int numRecords, Times times)
specifier|private
specifier|static
name|void
name|runXmlBench
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|numRecords
parameter_list|,
name|Times
name|times
parameter_list|)
throws|throws
name|IOException
block|{
name|Record
index|[]
name|records
init|=
name|makeArray
argument_list|(
name|type
argument_list|,
name|numRecords
argument_list|,
name|times
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|XmlRecordOutput
name|rout
init|=
operator|new
name|XmlRecordOutput
argument_list|(
name|bout
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|bout
operator|.
name|reset
argument_list|()
expr_stmt|;
name|bout
operator|.
name|write
argument_list|(
literal|"<records>\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|serialize
argument_list|(
name|rout
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|serialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|serialize
expr_stmt|;
name|bout
operator|.
name|write
argument_list|(
literal|"</records>\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|serialized
init|=
name|bout
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bin
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|XmlRecordInput
name|rin
init|=
operator|new
name|XmlRecordInput
argument_list|(
name|bin
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|numRecords
condition|;
name|idx
operator|++
control|)
block|{
name|records
index|[
name|idx
index|]
operator|.
name|deserialize
argument_list|(
name|rin
argument_list|)
expr_stmt|;
block|}
name|times
operator|.
name|deserialize
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|times
operator|.
name|deserialize
expr_stmt|;
block|}
DECL|method|printTimes (String type, String format, int numRecords, Times times)
specifier|private
specifier|static
name|void
name|printTimes
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|format
parameter_list|,
name|int
name|numRecords
parameter_list|,
name|Times
name|times
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Type: "
operator|+
name|type
operator|+
literal|" Format: "
operator|+
name|format
operator|+
literal|" #Records: "
operator|+
name|numRecords
argument_list|)
expr_stmt|;
if|if
condition|(
name|times
operator|.
name|init
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Initialization Time (Per record) : "
operator|+
name|times
operator|.
name|init
operator|/
name|numRecords
operator|+
literal|" Nanoseconds"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|times
operator|.
name|serialize
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Serialization Time (Per Record) : "
operator|+
name|times
operator|.
name|serialize
operator|/
name|numRecords
operator|+
literal|" Nanoseconds"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|times
operator|.
name|deserialize
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deserialization Time (Per Record) : "
operator|+
name|times
operator|.
name|deserialize
operator|/
name|numRecords
operator|+
literal|" Nanoseconds"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|times
operator|.
name|write
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Write Time (Per Record) : "
operator|+
name|times
operator|.
name|write
operator|/
name|numRecords
operator|+
literal|" Nanoseconds"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|times
operator|.
name|readFields
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ReadFields Time (Per Record) : "
operator|+
name|times
operator|.
name|readFields
operator|/
name|numRecords
operator|+
literal|" Nanoseconds"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|method|toCamelCase (String inp)
specifier|private
specifier|static
name|String
name|toCamelCase
parameter_list|(
name|String
name|inp
parameter_list|)
block|{
name|char
name|firstChar
init|=
name|inp
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isLowerCase
argument_list|(
name|firstChar
argument_list|)
condition|)
block|{
return|return
literal|""
operator|+
name|Character
operator|.
name|toUpperCase
argument_list|(
name|firstChar
argument_list|)
operator|+
name|inp
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|inp
return|;
block|}
DECL|method|exitOnError ()
specifier|private
specifier|static
name|void
name|exitOnError
parameter_list|()
block|{
name|String
name|usage
init|=
literal|"RecordBench {buffer|string|int}"
operator|+
literal|" {binary|csv|xml}<numRecords>"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param args the command line arguments    */
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
name|IOException
block|{
name|String
name|version
init|=
literal|"RecordBench v0.1"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|version
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
name|exitOnError
argument_list|()
expr_stmt|;
block|}
name|String
name|typeName
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|format
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|int
name|numRecords
init|=
name|Integer
operator|.
name|decode
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|Method
name|bench
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bench
operator|=
name|RecordBench
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"run"
operator|+
name|toCamelCase
argument_list|(
name|format
argument_list|)
operator|+
literal|"Bench"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|Integer
operator|.
name|TYPE
block|,
name|Times
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exitOnError
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|numRecords
operator|<
literal|0
condition|)
block|{
name|exitOnError
argument_list|()
expr_stmt|;
block|}
comment|// dry run
name|rand
operator|.
name|setSeed
argument_list|(
name|SEED
argument_list|)
expr_stmt|;
name|Times
name|times
init|=
operator|new
name|Times
argument_list|()
decl_stmt|;
try|try
block|{
name|bench
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|typeName
block|,
name|numRecords
block|,
name|times
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// timed run
name|rand
operator|.
name|setSeed
argument_list|(
name|SEED
argument_list|)
expr_stmt|;
try|try
block|{
name|bench
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|typeName
block|,
name|numRecords
block|,
name|times
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|printTimes
argument_list|(
name|typeName
argument_list|,
name|format
argument_list|,
name|numRecords
argument_list|,
name|times
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

