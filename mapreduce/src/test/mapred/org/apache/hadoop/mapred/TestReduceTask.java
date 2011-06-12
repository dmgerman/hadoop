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
name|IOException
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
name|LocalFileSystem
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
name|io
operator|.
name|WritableComparator
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
name|compress
operator|.
name|CompressionCodec
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
name|compress
operator|.
name|DefaultCodec
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

begin_comment
comment|/**  * This test exercises the ValueIterator.  */
end_comment

begin_class
DECL|class|TestReduceTask
specifier|public
class|class
name|TestReduceTask
extends|extends
name|TestCase
block|{
DECL|class|NullProgress
specifier|static
class|class
name|NullProgress
implements|implements
name|Progressable
block|{
DECL|method|progress ()
specifier|public
name|void
name|progress
parameter_list|()
block|{ }
block|}
DECL|class|Pair
specifier|private
specifier|static
class|class
name|Pair
block|{
DECL|field|key
name|String
name|key
decl_stmt|;
DECL|field|value
name|String
name|value
decl_stmt|;
DECL|method|Pair (String k, String v)
name|Pair
parameter_list|(
name|String
name|k
parameter_list|,
name|String
name|v
parameter_list|)
block|{
name|key
operator|=
name|k
expr_stmt|;
name|value
operator|=
name|v
expr_stmt|;
block|}
block|}
DECL|field|testCases
specifier|private
specifier|static
name|Pair
index|[]
index|[]
name|testCases
init|=
operator|new
name|Pair
index|[]
index|[]
block|{
operator|new
name|Pair
index|[]
block|{
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k3"
argument_list|,
literal|"v3"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k3"
argument_list|,
literal|"v4"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k4"
argument_list|,
literal|"v5"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k5"
argument_list|,
literal|"v6"
argument_list|)
block|,       }
block|,
operator|new
name|Pair
index|[]
block|{
operator|new
name|Pair
argument_list|(
literal|""
argument_list|,
literal|"v1"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v2"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k2"
argument_list|,
literal|"v3"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k2"
argument_list|,
literal|"v4"
argument_list|)
block|,       }
block|,
operator|new
name|Pair
index|[]
block|{}
block|,
operator|new
name|Pair
index|[]
block|{
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v2"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v3"
argument_list|)
block|,
operator|new
name|Pair
argument_list|(
literal|"k1"
argument_list|,
literal|"v4"
argument_list|)
block|,       }
block|}
decl_stmt|;
DECL|method|runValueIterator (Path tmpDir, Pair[] vals, Configuration conf, CompressionCodec codec)
specifier|public
name|void
name|runValueIterator
parameter_list|(
name|Path
name|tmpDir
parameter_list|,
name|Pair
index|[]
name|vals
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|rfs
init|=
operator|(
operator|(
name|LocalFileSystem
operator|)
name|localFs
operator|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
argument_list|,
literal|"data.in"
argument_list|)
decl_stmt|;
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|writer
init|=
operator|new
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|conf
argument_list|,
name|rfs
argument_list|,
name|path
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
name|p
range|:
name|vals
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
operator|new
name|Text
argument_list|(
name|p
operator|.
name|key
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|p
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|RawKeyValueIterator
name|rawItr
init|=
name|Merger
operator|.
name|merge
argument_list|(
name|conf
argument_list|,
name|rfs
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|codec
argument_list|,
operator|new
name|Path
index|[]
block|{
name|path
block|}
argument_list|,
literal|false
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|JobContext
operator|.
name|IO_SORT_FACTOR
argument_list|,
literal|100
argument_list|)
argument_list|,
name|tmpDir
argument_list|,
operator|new
name|Text
operator|.
name|Comparator
argument_list|()
argument_list|,
operator|new
name|NullProgress
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// WritableComparators are not generic
name|ReduceTask
operator|.
name|ValuesIterator
name|valItr
init|=
operator|new
name|ReduceTask
operator|.
name|ValuesIterator
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|rawItr
argument_list|,
name|WritableComparator
operator|.
name|get
argument_list|(
name|Text
operator|.
name|class
argument_list|)
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|conf
argument_list|,
operator|new
name|NullProgress
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|valItr
operator|.
name|more
argument_list|()
condition|)
block|{
name|Object
name|key
init|=
name|valItr
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|keyString
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// make sure it matches!
name|assertEquals
argument_list|(
name|vals
index|[
name|i
index|]
operator|.
name|key
argument_list|,
name|keyString
argument_list|)
expr_stmt|;
comment|// must have at least 1 value!
name|assertTrue
argument_list|(
name|valItr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|valItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|valueString
init|=
name|valItr
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// make sure the values match
name|assertEquals
argument_list|(
name|vals
index|[
name|i
index|]
operator|.
name|value
argument_list|,
name|valueString
argument_list|)
expr_stmt|;
comment|// make sure the keys match
name|assertEquals
argument_list|(
name|vals
index|[
name|i
index|]
operator|.
name|key
argument_list|,
name|valItr
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|1
expr_stmt|;
block|}
comment|// make sure the key hasn't changed under the hood
name|assertEquals
argument_list|(
name|keyString
argument_list|,
name|valItr
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|valItr
operator|.
name|nextKey
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|vals
operator|.
name|length
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// make sure we have progress equal to 1.0
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|rawItr
operator|.
name|getProgress
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testValueIterator ()
specifier|public
name|void
name|testValueIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmpDir
init|=
operator|new
name|Path
argument_list|(
literal|"build/test/test.reduce.task"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
index|[]
name|testCase
range|:
name|testCases
control|)
block|{
name|runValueIterator
argument_list|(
name|tmpDir
argument_list|,
name|testCase
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testValueIteratorWithCompression ()
specifier|public
name|void
name|testValueIteratorWithCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmpDir
init|=
operator|new
name|Path
argument_list|(
literal|"build/test/test.reduce.task.compression"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DefaultCodec
name|codec
init|=
operator|new
name|DefaultCodec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|Pair
index|[]
name|testCase
range|:
name|testCases
control|)
block|{
name|runValueIterator
argument_list|(
name|tmpDir
argument_list|,
name|testCase
argument_list|,
name|conf
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

