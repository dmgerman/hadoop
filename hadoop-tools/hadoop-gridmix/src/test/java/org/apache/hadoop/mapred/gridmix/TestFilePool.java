begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|BlockLocation
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
name|FileStatus
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|CombineFileSplit
import|;
end_import

begin_class
DECL|class|TestFilePool
specifier|public
class|class
name|TestFilePool
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestFileQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NFILES
specifier|static
specifier|final
name|int
name|NFILES
init|=
literal|26
decl_stmt|;
DECL|field|base
specifier|static
specifier|final
name|Path
name|base
init|=
name|getBaseDir
argument_list|()
decl_stmt|;
DECL|method|getBaseDir ()
specifier|static
name|Path
name|getBaseDir
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
return|return
name|fs
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
literal|"testFilePool"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|base
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NFILES
condition|;
operator|++
name|i
control|)
block|{
name|Path
name|file
init|=
name|base
decl_stmt|;
for|for
control|(
name|double
name|d
init|=
literal|0.6
init|;
name|d
operator|>
literal|0.0
condition|;
name|d
operator|*=
literal|0.8
control|)
block|{
if|if
condition|(
name|r
operator|.
name|nextDouble
argument_list|()
operator|<
name|d
condition|)
block|{
name|file
operator|=
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
break|break;
block|}
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|,
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|b
argument_list|,
call|(
name|byte
call|)
argument_list|(
literal|'A'
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|len
init|=
operator|(
operator|(
name|i
operator|%
literal|13
operator|)
operator|+
literal|1
operator|)
operator|*
literal|1024
init|;
name|len
operator|>
literal|0
condition|;
name|len
operator|-=
literal|1024
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
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
block|}
block|}
block|}
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnsuitable ()
specifier|public
name|void
name|testUnsuitable
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// all files 13k or less
name|conf
operator|.
name|setLong
argument_list|(
name|FilePool
operator|.
name|GRIDMIX_MIN_FILE
argument_list|,
literal|14
operator|*
literal|1024
argument_list|)
expr_stmt|;
specifier|final
name|FilePool
name|pool
init|=
operator|new
name|FilePool
argument_list|(
name|conf
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|pool
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return;
block|}
name|fail
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPool ()
specifier|public
name|void
name|testPool
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FilePool
operator|.
name|GRIDMIX_MIN_FILE
argument_list|,
literal|3
operator|*
literal|1024
argument_list|)
expr_stmt|;
specifier|final
name|FilePool
name|pool
init|=
operator|new
name|FilePool
argument_list|(
name|conf
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|pool
operator|.
name|refresh
argument_list|()
expr_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
comment|// ensure 1k, 2k files excluded
specifier|final
name|int
name|expectedPoolSize
init|=
operator|(
name|NFILES
operator|/
literal|2
operator|*
operator|(
name|NFILES
operator|/
literal|2
operator|+
literal|1
operator|)
operator|-
literal|6
operator|)
operator|*
literal|1024
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedPoolSize
argument_list|,
name|pool
operator|.
name|getInputFiles
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NFILES
operator|-
literal|4
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// exact match
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedPoolSize
argument_list|,
name|pool
operator|.
name|getInputFiles
argument_list|(
name|expectedPoolSize
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
comment|// match random within 12k
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|long
name|rand
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|expectedPoolSize
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missed: "
operator|+
name|rand
argument_list|,
operator|(
name|NFILES
operator|/
literal|2
operator|)
operator|*
literal|1024
operator|>
name|rand
operator|-
name|pool
operator|.
name|getInputFiles
argument_list|(
name|rand
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
comment|// all files
name|conf
operator|.
name|setLong
argument_list|(
name|FilePool
operator|.
name|GRIDMIX_MIN_FILE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|pool
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|NFILES
operator|/
literal|2
operator|*
operator|(
name|NFILES
operator|/
literal|2
operator|+
literal|1
operator|)
operator|)
operator|*
literal|1024
argument_list|,
name|pool
operator|.
name|getInputFiles
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkSplitEq (FileSystem fs, CombineFileSplit split, long bytes)
name|void
name|checkSplitEq
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|CombineFileSplit
name|split
parameter_list|,
name|long
name|bytes
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|splitBytes
init|=
literal|0L
decl_stmt|;
name|HashSet
argument_list|<
name|Path
argument_list|>
name|uniq
init|=
operator|new
name|HashSet
argument_list|<
name|Path
argument_list|>
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
name|split
operator|.
name|getNumPaths
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|splitBytes
operator|+=
name|split
operator|.
name|getLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|split
operator|.
name|getLength
argument_list|(
name|i
argument_list|)
operator|<=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|split
operator|.
name|getPath
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uniq
operator|.
name|contains
argument_list|(
name|split
operator|.
name|getPath
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|uniq
operator|.
name|add
argument_list|(
name|split
operator|.
name|getPath
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bytes
argument_list|,
name|splitBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStriper ()
specifier|public
name|void
name|testStriper
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FilePool
operator|.
name|GRIDMIX_MIN_FILE
argument_list|,
literal|3
operator|*
literal|1024
argument_list|)
expr_stmt|;
specifier|final
name|FilePool
name|pool
init|=
operator|new
name|FilePool
argument_list|(
name|conf
argument_list|,
name|base
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|BlockLocation
index|[]
name|locationsFor
parameter_list|(
name|FileStatus
name|stat
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BlockLocation
index|[]
block|{
operator|new
name|BlockLocation
argument_list|()
block|}
return|;
block|}
block|}
decl_stmt|;
name|pool
operator|.
name|refresh
argument_list|()
expr_stmt|;
specifier|final
name|int
name|expectedPoolSize
init|=
operator|(
name|NFILES
operator|/
literal|2
operator|*
operator|(
name|NFILES
operator|/
literal|2
operator|+
literal|1
operator|)
operator|-
literal|6
operator|)
operator|*
literal|1024
decl_stmt|;
specifier|final
name|InputStriper
name|striper
init|=
operator|new
name|InputStriper
argument_list|(
name|pool
argument_list|,
name|expectedPoolSize
argument_list|)
decl_stmt|;
name|int
name|last
init|=
literal|0
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
name|expectedPoolSize
condition|;
name|last
operator|=
name|Math
operator|.
name|min
argument_list|(
name|expectedPoolSize
operator|-
name|i
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
name|expectedPoolSize
argument_list|)
argument_list|)
control|)
block|{
name|checkSplitEq
argument_list|(
name|fs
argument_list|,
name|striper
operator|.
name|splitFor
argument_list|(
name|pool
argument_list|,
name|last
argument_list|,
literal|0
argument_list|)
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|i
operator|+=
name|last
expr_stmt|;
block|}
specifier|final
name|InputStriper
name|striper2
init|=
operator|new
name|InputStriper
argument_list|(
name|pool
argument_list|,
name|expectedPoolSize
argument_list|)
decl_stmt|;
name|checkSplitEq
argument_list|(
name|fs
argument_list|,
name|striper2
operator|.
name|splitFor
argument_list|(
name|pool
argument_list|,
name|expectedPoolSize
argument_list|,
literal|0
argument_list|)
argument_list|,
name|expectedPoolSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

