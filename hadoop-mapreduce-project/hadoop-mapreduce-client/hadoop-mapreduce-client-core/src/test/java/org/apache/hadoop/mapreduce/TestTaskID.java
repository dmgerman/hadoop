begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|DataInputByteBuffer
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

begin_comment
comment|/**  * Test the {@link TaskID} class.  */
end_comment

begin_class
DECL|class|TestTaskID
specifier|public
class|class
name|TestTaskID
block|{
comment|/**    * Test of getJobID method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testGetJobID ()
specifier|public
name|void
name|testGetJobID
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"TaskID did not store the JobID correctly"
argument_list|,
name|jobId
argument_list|,
name|taskId
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|taskId
operator|=
operator|new
name|TaskID
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Job ID was set unexpectedly in default contsructor"
argument_list|,
literal|""
argument_list|,
name|taskId
operator|.
name|getJobID
argument_list|()
operator|.
name|getJtIdentifier
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of isMap method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testIsMap ()
specifier|public
name|void
name|testIsMap
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskType
name|type
range|:
name|TaskType
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
name|assertTrue
argument_list|(
literal|"TaskID for map task did not correctly identify itself "
operator|+
literal|"as a map task"
argument_list|,
name|taskId
operator|.
name|isMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"TaskID for "
operator|+
name|type
operator|+
literal|" task incorrectly identified "
operator|+
literal|"itself as a map task"
argument_list|,
name|taskId
operator|.
name|isMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"TaskID of default type incorrectly identified itself as a "
operator|+
literal|"map task"
argument_list|,
name|taskId
operator|.
name|isMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of getTaskType method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testGetTaskType0args ()
specifier|public
name|void
name|testGetTaskType0args
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskType
name|type
range|:
name|TaskType
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TaskID incorrectly reported its type"
argument_list|,
name|type
argument_list|,
name|taskId
operator|.
name|getTaskType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TaskID of default type incorrectly reported its type"
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
name|taskId
operator|.
name|getTaskType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of equals method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|JobID
name|jobId1
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|JobID
name|jobId2
init|=
operator|new
name|JobID
argument_list|(
literal|"2345"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|TaskID
name|taskId1
init|=
operator|new
name|TaskID
argument_list|(
name|jobId1
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|TaskID
name|taskId2
init|=
operator|new
name|TaskID
argument_list|(
name|jobId1
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The equals() method reported two equal task IDs were not equal"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
name|taskId2
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId2
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The equals() method reported two task IDs with different "
operator|+
literal|"job IDs were equal"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
name|taskId2
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId1
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The equals() method reported two task IDs with different IDs "
operator|+
literal|"were equal"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
name|TaskType
index|[]
name|types
init|=
name|TaskType
operator|.
name|values
argument_list|()
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|types
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|taskId1
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId1
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|taskId2
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId1
argument_list|,
name|types
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|j
condition|)
block|{
name|assertTrue
argument_list|(
literal|"The equals() method reported two equal task IDs were not "
operator|+
literal|"equal"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"The equals() method reported two task IDs with "
operator|+
literal|"different types were equal"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertFalse
argument_list|(
literal|"The equals() method matched against a JobID object"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
name|jobId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The equals() method matched against a null object"
argument_list|,
name|taskId1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of compareTo method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testCompareTo ()
specifier|public
name|void
name|testCompareTo
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TaskID
name|taskId1
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|TaskID
name|taskId2
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The compareTo() method returned non-zero for two equal "
operator|+
literal|"task IDs"
argument_list|,
literal|0
argument_list|,
name|taskId1
operator|.
name|compareTo
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
name|taskId2
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The compareTo() method did not weigh task type more than task "
operator|+
literal|"ID"
argument_list|,
name|taskId1
operator|.
name|compareTo
argument_list|(
name|taskId2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|TaskType
index|[]
name|types
init|=
name|TaskType
operator|.
name|values
argument_list|()
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|types
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|taskId1
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|taskId2
operator|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|types
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|j
condition|)
block|{
name|assertEquals
argument_list|(
literal|"The compareTo() method returned non-zero for two equal "
operator|+
literal|"task IDs"
argument_list|,
literal|0
argument_list|,
name|taskId1
operator|.
name|compareTo
argument_list|(
name|taskId2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
name|j
condition|)
block|{
name|assertTrue
argument_list|(
literal|"The compareTo() method did not order "
operator|+
name|types
index|[
name|i
index|]
operator|+
literal|" before "
operator|+
name|types
index|[
name|j
index|]
argument_list|,
name|taskId1
operator|.
name|compareTo
argument_list|(
name|taskId2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"The compareTo() method did not order "
operator|+
name|types
index|[
name|i
index|]
operator|+
literal|" after "
operator|+
name|types
index|[
name|j
index|]
argument_list|,
name|taskId1
operator|.
name|compareTo
argument_list|(
name|taskId2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|taskId1
operator|.
name|compareTo
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The compareTo() method allowed comparison to a JobID object"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|taskId1
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The compareTo() method allowed comparison to a null object"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Test of toString method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testToString ()
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskType
name|type
range|:
name|TaskType
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|String
operator|.
name|format
argument_list|(
literal|"task_1234_0001_%c_000000"
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|type
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The toString() method returned the wrong value"
argument_list|,
name|str
argument_list|,
name|taskId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test of appendTo method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testAppendTo ()
specifier|public
name|void
name|testAppendTo
parameter_list|()
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskType
name|type
range|:
name|TaskType
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|String
operator|.
name|format
argument_list|(
literal|"_1234_0001_%c_000000"
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|type
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The appendTo() method appended the wrong value"
argument_list|,
name|str
argument_list|,
name|taskId
operator|.
name|appendTo
argument_list|(
name|builder
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|TaskID
argument_list|()
operator|.
name|appendTo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The appendTo() method allowed a null builder"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Test of hashCode method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testHashCode ()
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|TaskType
index|[]
name|types
init|=
name|TaskType
operator|.
name|values
argument_list|()
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
operator|+
name|i
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|TaskID
name|taskId1
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|TaskID
name|taskId2
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The hashcode() method gave unequal hash codes for two equal "
operator|+
literal|"task IDs"
argument_list|,
name|taskId1
operator|.
name|hashCode
argument_list|()
operator|==
name|taskId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test of readFields method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testReadFields ()
specifier|public
name|void
name|testReadFields
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x31
block|,
literal|0x32
block|,
literal|0x33
block|,
literal|0x34
block|}
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|)
expr_stmt|;
name|DataInputByteBuffer
name|in
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TaskID
name|instance
init|=
operator|new
name|TaskID
argument_list|()
decl_stmt|;
name|instance
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The readFields() method did not produce the expected task ID"
argument_list|,
literal|"task_1234_0001_r_000000"
argument_list|,
name|instance
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of write method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testWrite ()
specifier|public
name|void
name|testWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|(
literal|"1234"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TaskID
name|taskId
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|taskId
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DataInputByteBuffer
name|in
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The write() method did not write the expected task ID"
argument_list|,
literal|0
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The write() method did not write the expected job ID"
argument_list|,
literal|1
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The write() method did not write the expected job "
operator|+
literal|"identifier length"
argument_list|,
literal|4
argument_list|,
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The write() method did not write the expected job "
operator|+
literal|"identifier length"
argument_list|,
literal|"1234"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The write() method did not write the expected task type"
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|TaskType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of forName method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testForName ()
specifier|public
name|void
name|testForName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"The forName() method did not parse the task ID string "
operator|+
literal|"correctly"
argument_list|,
literal|"task_1_0001_m_000000"
argument_list|,
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_1_0001_m_000"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The forName() method did not parse the task ID string "
operator|+
literal|"correctly"
argument_list|,
literal|"task_23_0002_r_000001"
argument_list|,
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_23_0002_r_0001"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The forName() method did not parse the task ID string "
operator|+
literal|"correctly"
argument_list|,
literal|"task_345_0003_s_000002"
argument_list|,
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_345_0003_s_00002"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The forName() method did not parse the task ID string "
operator|+
literal|"correctly"
argument_list|,
literal|"task_6789_0004_c_000003"
argument_list|,
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_6789_0004_c_000003"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The forName() method did not parse the task ID string "
operator|+
literal|"correctly"
argument_list|,
literal|"task_12345_0005_t_4000000"
argument_list|,
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_0005_t_4000000"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"tisk_12345_0005_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"tisk_12345_0005_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"tisk_12345_0005_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"tisk_12345_0005_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_abc_0005_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_abc_0005_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_xyz_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_xyz_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_0005_x_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_0005_x_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_0005_t_jkl"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_0005_t_jkl"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_0005_t"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_0005_t"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_0005_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_0005_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_12345_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"task_12345_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|TaskID
operator|.
name|forName
argument_list|(
literal|"12345_0005_t_4000000"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The forName() method parsed an invalid job ID: "
operator|+
literal|"12345_0005_t_4000000"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Test of getRepresentingCharacter method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testGetRepresentingCharacter ()
specifier|public
name|void
name|testGetRepresentingCharacter
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"The getRepresentingCharacter() method did not return the "
operator|+
literal|"expected character"
argument_list|,
literal|'m'
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getRepresentingCharacter() method did not return the "
operator|+
literal|"expected character"
argument_list|,
literal|'r'
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getRepresentingCharacter() method did not return the "
operator|+
literal|"expected character"
argument_list|,
literal|'s'
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|TaskType
operator|.
name|JOB_SETUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getRepresentingCharacter() method did not return the "
operator|+
literal|"expected character"
argument_list|,
literal|'c'
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getRepresentingCharacter() method did not return the "
operator|+
literal|"expected character"
argument_list|,
literal|'t'
argument_list|,
name|TaskID
operator|.
name|getRepresentingCharacter
argument_list|(
name|TaskType
operator|.
name|TASK_CLEANUP
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of getTaskType method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testGetTaskTypeChar ()
specifier|public
name|void
name|testGetTaskTypeChar
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"The getTaskType() method did not return the expected type"
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'m'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getTaskType() method did not return the expected type"
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'r'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getTaskType() method did not return the expected type"
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'s'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getTaskType() method did not return the expected type"
argument_list|,
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'c'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The getTaskType() method did not return the expected type"
argument_list|,
name|TaskType
operator|.
name|TASK_CLEANUP
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'t'
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"The getTaskType() method did not return null for an unknown "
operator|+
literal|"type"
argument_list|,
name|TaskID
operator|.
name|getTaskType
argument_list|(
literal|'x'
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of getAllTaskTypes method, of class TaskID.    */
annotation|@
name|Test
DECL|method|testGetAllTaskTypes ()
specifier|public
name|void
name|testGetAllTaskTypes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"The getAllTaskTypes method did not return the expected "
operator|+
literal|"string"
argument_list|,
literal|"(m|r|s|c|t)"
argument_list|,
name|TaskID
operator|.
name|getAllTaskTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

