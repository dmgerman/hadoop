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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|mock
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
name|when
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
name|doAnswer
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|DataInputBuffer
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
name|RawComparator
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
name|Counters
operator|.
name|Counter
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
name|IFile
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
name|mapred
operator|.
name|Merger
operator|.
name|Segment
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
name|util
operator|.
name|Progress
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
name|util
operator|.
name|Progressable
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
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestMerger
specifier|public
class|class
name|TestMerger
block|{
annotation|@
name|Test
DECL|method|testCompressed ()
specifier|public
name|void
name|testCompressed
parameter_list|()
throws|throws
name|IOException
block|{
name|testMergeShouldReturnProperProgress
argument_list|(
name|getCompressedSegments
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUncompressed ()
specifier|public
name|void
name|testUncompressed
parameter_list|()
throws|throws
name|IOException
block|{
name|testMergeShouldReturnProperProgress
argument_list|(
name|getUncompressedSegments
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"deprecation"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|testMergeShouldReturnProperProgress ( List<Segment<Text, Text>> segments)
specifier|public
name|void
name|testMergeShouldReturnProperProgress
parameter_list|(
name|List
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|segments
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|tmpDir
init|=
operator|new
name|Path
argument_list|(
literal|"localpath"
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|Text
argument_list|>
name|keyClass
init|=
operator|(
name|Class
argument_list|<
name|Text
argument_list|>
operator|)
name|jobConf
operator|.
name|getMapOutputKeyClass
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|Text
argument_list|>
name|valueClass
init|=
operator|(
name|Class
argument_list|<
name|Text
argument_list|>
operator|)
name|jobConf
operator|.
name|getMapOutputValueClass
argument_list|()
decl_stmt|;
name|RawComparator
argument_list|<
name|Text
argument_list|>
name|comparator
init|=
name|jobConf
operator|.
name|getOutputKeyComparator
argument_list|()
decl_stmt|;
name|Counter
name|readsCounter
init|=
operator|new
name|Counter
argument_list|()
decl_stmt|;
name|Counter
name|writesCounter
init|=
operator|new
name|Counter
argument_list|()
decl_stmt|;
name|Progress
name|mergePhase
init|=
operator|new
name|Progress
argument_list|()
decl_stmt|;
name|RawKeyValueIterator
name|mergeQueue
init|=
name|Merger
operator|.
name|merge
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|,
name|segments
argument_list|,
literal|2
argument_list|,
name|tmpDir
argument_list|,
name|comparator
argument_list|,
name|getReporter
argument_list|()
argument_list|,
name|readsCounter
argument_list|,
name|writesCounter
argument_list|,
name|mergePhase
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|mergeQueue
operator|.
name|getProgress
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getReporter ()
specifier|private
name|Progressable
name|getReporter
parameter_list|()
block|{
name|Progressable
name|reporter
init|=
operator|new
name|Progressable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|progress
parameter_list|()
block|{       }
block|}
decl_stmt|;
return|return
name|reporter
return|;
block|}
DECL|method|getUncompressedSegments ()
specifier|private
name|List
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|getUncompressedSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|segments
operator|.
name|add
argument_list|(
name|getUncompressedSegment
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding segment"
argument_list|)
expr_stmt|;
block|}
return|return
name|segments
return|;
block|}
DECL|method|getCompressedSegments ()
specifier|private
name|List
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|getCompressedSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|segments
operator|.
name|add
argument_list|(
name|getCompressedSegment
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding segment"
argument_list|)
expr_stmt|;
block|}
return|return
name|segments
return|;
block|}
DECL|method|getUncompressedSegment (int i)
specifier|private
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|getUncompressedSegment
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|getReader
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getCompressedSegment (int i)
specifier|private
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|getCompressedSegment
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Segment
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|getReader
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|3000l
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getReader (int i)
specifier|private
name|Reader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|getReader
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|Reader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|readerMock
init|=
name|mock
argument_list|(
name|Reader
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|readerMock
operator|.
name|getPosition
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0l
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10l
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|20l
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|readerMock
operator|.
name|nextRawKey
argument_list|(
name|any
argument_list|(
name|DataInputBuffer
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|getKeyAnswer
argument_list|(
literal|"Segment"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
name|getValueAnswer
argument_list|(
literal|"Segment"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|readerMock
argument_list|)
operator|.
name|nextRawValue
argument_list|(
name|any
argument_list|(
name|DataInputBuffer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|readerMock
return|;
block|}
DECL|method|getKeyAnswer (final String segmentName)
specifier|private
name|Answer
argument_list|<
name|?
argument_list|>
name|getKeyAnswer
parameter_list|(
specifier|final
name|String
name|segmentName
parameter_list|)
block|{
return|return
operator|new
name|Answer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|Boolean
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|key
init|=
operator|(
name|DataInputBuffer
operator|)
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|++
operator|==
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
name|key
operator|.
name|reset
argument_list|(
operator|(
literal|"Segement Key "
operator|+
name|segmentName
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
DECL|method|getValueAnswer (final String segmentName)
specifier|private
name|Answer
argument_list|<
name|?
argument_list|>
name|getValueAnswer
parameter_list|(
specifier|final
name|String
name|segmentName
parameter_list|)
block|{
return|return
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|key
init|=
operator|(
name|DataInputBuffer
operator|)
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|++
operator|==
literal|2
condition|)
block|{
return|return
literal|null
return|;
block|}
name|key
operator|.
name|reset
argument_list|(
operator|(
literal|"Segement Value "
operator|+
name|segmentName
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

