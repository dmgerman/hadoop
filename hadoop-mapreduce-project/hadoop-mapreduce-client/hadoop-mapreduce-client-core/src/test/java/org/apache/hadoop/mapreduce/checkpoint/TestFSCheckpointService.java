begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.checkpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|checkpoint
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|DataOutputBuffer
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
name|checkpoint
operator|.
name|CheckpointService
operator|.
name|CheckpointWriteChannel
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestFSCheckpointService
specifier|public
class|class
name|TestFSCheckpointService
block|{
DECL|field|BUFSIZE
specifier|private
specifier|final
name|int
name|BUFSIZE
init|=
literal|1024
decl_stmt|;
annotation|@
name|Test
DECL|method|testCheckpointCreate ()
specifier|public
name|void
name|testCheckpointCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkpointCreate
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|BUFSIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckpointCreateDirect ()
specifier|public
name|void
name|testCheckpointCreateDirect
parameter_list|()
throws|throws
name|Exception
block|{
name|checkpointCreate
argument_list|(
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|BUFSIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkpointCreate (ByteBuffer b)
specifier|public
name|void
name|checkpointCreate
parameter_list|(
name|ByteBuffer
name|b
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|WRITES
init|=
literal|128
decl_stmt|;
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|hdfs
init|=
name|spy
argument_list|(
operator|new
name|FSDataOutputStream
argument_list|(
name|dob
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
comment|// backed by array
name|DataOutputBuffer
name|verif
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|hdfs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|rename
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Path
name|base
init|=
operator|new
name|Path
argument_list|(
literal|"/chk"
argument_list|)
decl_stmt|;
name|Path
name|finalLoc
init|=
operator|new
name|Path
argument_list|(
literal|"/chk/checkpoint_chk0"
argument_list|)
decl_stmt|;
name|Path
name|tmp
init|=
name|FSCheckpointService
operator|.
name|tmpfile
argument_list|(
name|finalLoc
argument_list|)
decl_stmt|;
name|FSCheckpointService
name|chk
init|=
operator|new
name|FSCheckpointService
argument_list|(
name|fs
argument_list|,
name|base
argument_list|,
operator|new
name|SimpleNamingService
argument_list|(
literal|"chk0"
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|CheckpointWriteChannel
name|out
init|=
name|chk
operator|.
name|create
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|randBytes
init|=
operator|new
name|byte
index|[
name|BUFSIZE
index|]
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
name|WRITES
condition|;
operator|++
name|i
control|)
block|{
name|r
operator|.
name|nextBytes
argument_list|(
name|randBytes
argument_list|)
expr_stmt|;
name|int
name|s
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|BUFSIZE
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|e
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|BUFSIZE
operator|-
name|s
argument_list|)
operator|+
literal|1
decl_stmt|;
name|verif
operator|.
name|write
argument_list|(
name|randBytes
argument_list|,
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|b
operator|.
name|clear
argument_list|()
expr_stmt|;
name|b
operator|.
name|put
argument_list|(
name|randBytes
argument_list|)
operator|.
name|flip
argument_list|()
expr_stmt|;
name|b
operator|.
name|position
argument_list|(
name|s
argument_list|)
operator|.
name|limit
argument_list|(
name|b
operator|.
name|position
argument_list|()
operator|+
name|e
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|rename
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
name|finalLoc
argument_list|)
argument_list|)
expr_stmt|;
name|CheckpointID
name|cid
init|=
name|chk
operator|.
name|commit
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|hdfs
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|rename
argument_list|(
name|eq
argument_list|(
name|tmp
argument_list|)
argument_list|,
name|eq
argument_list|(
name|finalLoc
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|verif
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|verif
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelete ()
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|chkloc
init|=
operator|new
name|Path
argument_list|(
literal|"/chk/chk0"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|chkloc
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Path
name|base
init|=
operator|new
name|Path
argument_list|(
literal|"/otherchk"
argument_list|)
decl_stmt|;
name|FSCheckpointID
name|id
init|=
operator|new
name|FSCheckpointID
argument_list|(
name|chkloc
argument_list|)
decl_stmt|;
name|FSCheckpointService
name|chk
init|=
operator|new
name|FSCheckpointService
argument_list|(
name|fs
argument_list|,
name|base
argument_list|,
operator|new
name|SimpleNamingService
argument_list|(
literal|"chk0"
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|chk
operator|.
name|delete
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|chkloc
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

