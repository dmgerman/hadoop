begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

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
name|LocatedFileStatus
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
name|fs
operator|.
name|RemoteIterator
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
name|Collection
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|S3AUtils
operator|.
name|ACCEPT_ALL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|Listing
operator|.
name|ProvidedFileStatusIterator
import|;
end_import

begin_comment
comment|/**  * Place for the S3A listing classes; keeps all the small classes under control.  */
end_comment

begin_class
DECL|class|TestListing
specifier|public
class|class
name|TestListing
extends|extends
name|AbstractS3AMockTest
block|{
DECL|class|MockRemoteIterator
specifier|private
specifier|static
class|class
name|MockRemoteIterator
parameter_list|<
name|FileStatus
parameter_list|>
implements|implements
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|FileStatus
argument_list|>
name|iterator
decl_stmt|;
DECL|method|MockRemoteIterator (Collection<FileStatus> source)
name|MockRemoteIterator
parameter_list|(
name|Collection
argument_list|<
name|FileStatus
argument_list|>
name|source
parameter_list|)
block|{
name|iterator
operator|=
name|source
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next ()
specifier|public
name|FileStatus
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|blankFileStatus (Path path)
specifier|private
name|FileStatus
name|blankFileStatus
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testTombstoneReconcilingIterator ()
specifier|public
name|void
name|testTombstoneReconcilingIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|parent
init|=
operator|new
name|Path
argument_list|(
literal|"/parent"
argument_list|)
decl_stmt|;
name|Path
name|liveChild
init|=
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
literal|"/liveChild"
argument_list|)
decl_stmt|;
name|Path
name|deletedChild
init|=
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
literal|"/deletedChild"
argument_list|)
decl_stmt|;
name|Path
index|[]
name|allFiles
init|=
block|{
name|parent
block|,
name|liveChild
block|,
name|deletedChild
block|}
decl_stmt|;
name|Path
index|[]
name|liveFiles
init|=
block|{
name|parent
block|,
name|liveChild
block|}
decl_stmt|;
name|Listing
name|listing
init|=
operator|new
name|Listing
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|FileStatus
argument_list|>
name|statuses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|statuses
operator|.
name|add
argument_list|(
name|blankFileStatus
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
name|statuses
operator|.
name|add
argument_list|(
name|blankFileStatus
argument_list|(
name|liveChild
argument_list|)
argument_list|)
expr_stmt|;
name|statuses
operator|.
name|add
argument_list|(
name|blankFileStatus
argument_list|(
name|deletedChild
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Path
argument_list|>
name|tombstones
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|tombstones
operator|.
name|add
argument_list|(
name|deletedChild
argument_list|)
expr_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|sourceIterator
init|=
operator|new
name|MockRemoteIterator
argument_list|(
name|statuses
argument_list|)
decl_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|locatedIterator
init|=
name|listing
operator|.
name|createLocatedFileStatusIterator
argument_list|(
name|sourceIterator
argument_list|)
decl_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|reconcilingIterator
init|=
name|listing
operator|.
name|createTombstoneReconcilingIterator
argument_list|(
name|locatedIterator
argument_list|,
name|tombstones
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Path
argument_list|>
name|expectedPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedPaths
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|expectedPaths
operator|.
name|add
argument_list|(
name|liveChild
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Path
argument_list|>
name|actualPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|reconcilingIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|actualPaths
operator|.
name|add
argument_list|(
name|reconcilingIterator
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|actualPaths
operator|.
name|equals
argument_list|(
name|expectedPaths
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProvidedFileStatusIteratorEnd ()
specifier|public
name|void
name|testProvidedFileStatusIteratorEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStatus
index|[]
name|statuses
init|=
block|{
operator|new
name|FileStatus
argument_list|(
literal|100
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|8192
argument_list|,
literal|0
argument_list|,
operator|new
name|Path
argument_list|(
literal|"s3a://blah/blah"
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|ProvidedFileStatusIterator
name|it
init|=
operator|new
name|ProvidedFileStatusIterator
argument_list|(
name|statuses
argument_list|,
name|ACCEPT_ALL
argument_list|,
operator|new
name|Listing
operator|.
name|AcceptAllButS3nDirs
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"hasNext() should return true first time"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"first element should not be null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"hasNext() should now be false"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"next() should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
comment|// Correct behavior.  Any other exceptions are propagated as failure.
return|return;
block|}
block|}
block|}
end_class

end_unit

